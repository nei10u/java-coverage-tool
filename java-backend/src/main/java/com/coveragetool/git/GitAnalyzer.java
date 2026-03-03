package com.coveragetool.git;

import com.coveragetool.model.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.blame.BlameGenerator;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Git分析器 - 分析Git仓库的提交历史和代码变更
 * 
 * 这个类使用JGit库访问Git仓库，提取提交历史、开发者信息、代码变更等数据。
 * 主要功能：
 * 1. 获取提交历史记录
 * 2. 分析每次提交的代码变更
 * 3. 统计开发者的贡献情况
 * 4. 计算覆盖率趋势
 */
public class GitAnalyzer {
    
    /**
     * Git仓库对象
     */
    private Repository repository;
    
    /**
     * Git对象
     */
    private Git git;
    
    /**
     * 初始化Git仓库
     * 
     * 打开指定路径下的Git仓库，为后续分析做准备。
     * 
     * @param projectPath 项目根路径（包含.git目录）
     * @throws IOException 如果仓库不存在或无法访问
     */
    public void initialize(String projectPath) throws IOException {
        // 构建仓库目录
        File repoDir = new File(projectPath, ".git");
        
        // 创建仓库构建器
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        
        // 打开仓库
        repository = builder.setGitDir(repoDir)
                           .readEnvironment() // 从环境变量读取配置
                           .findGitDir()      // 查找.git目录
                           .build();
        
        // 创建Git对象
        git = new Git(repository);
    }
    
    /**
     * 检查指定路径是否为Git仓库
     * 
     * 判断项目根目录下是否存在.git目录。
     * 
     * @param projectPath 项目根路径
     * @return true如果是Git仓库，false否则
     */
    public boolean isGitRepository(String projectPath) {
        File gitDir = new File(projectPath, ".git");
        return gitDir.exists() && gitDir.isDirectory();
    }
    
    /**
     * 获取提交历史
     * 
     * 获取指定时间范围内的所有提交记录，按时间倒序排列。
     * 
     * @param projectPath 项目根路径
     * @param since 开始日期（可选，为null表示不限制）
     * @param until 结束日期（可选，为null表示不限制）
     * @param maxCount 最大提交数量（可选，为null表示不限制，扫描全部）
     * @return 提交信息列表
     */
    public List<CommitInfo> getCommitHistory(String projectPath, Date since, Date until, Integer maxCount) {
        List<CommitInfo> commits = new ArrayList<>();
        
        try {
            // 确保仓库已初始化
            if (repository == null) {
                initialize(projectPath);
            }
            
            // 创建日志命令
            LogCommand logCommand = git.log();
            
            // 限制提交数量（如果指定了maxCount）
            if (maxCount != null && maxCount > 0) {
                logCommand.setMaxCount(maxCount);
            }
            // 如果maxCount为null，则不设置限制，扫描全部提交
            
            // 执行命令，获取提交迭代器
            Iterable<RevCommit> commitIterable = logCommand.call();
            
            // 遍历所有提交
            for (RevCommit revCommit : commitIterable) {
                CommitInfo commitInfo = parseCommit(revCommit);
                
                // 如果指定了时间范围，进行过滤
                if (since != null && commitInfo.getCommitDate().before(since)) {
                    continue; // 跳过早于since的提交
                }
                if (until != null && commitInfo.getCommitDate().after(until)) {
                    continue; // 跳过晚于until的提交
                }
                
                commits.add(commitInfo);
            }
            
        } catch (Exception e) {
            System.err.println("获取Git历史失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return commits;
    }
    
    /**
     * 解析单个提交
     * 
     * 将JGit的RevCommit对象转换为我们自己的CommitInfo对象。
     * 
     * @param revCommit JGit的提交对象
     * @return 我们的提交信息对象
     */
    private CommitInfo parseCommit(RevCommit revCommit) {
        CommitInfo commitInfo = new CommitInfo();
        
        // 设置提交哈希
        commitInfo.setCommitHash(revCommit.getId().getName());
        
        // 设置提交者信息
        commitInfo.setAuthor(revCommit.getAuthorIdent().getName());
        commitInfo.setAuthorEmail(revCommit.getAuthorIdent().getEmailAddress());
        
        // 设置提交时间
        commitInfo.setCommitDate(new Date(revCommit.getCommitTime() * 1000L));
        
        // 设置提交消息
        commitInfo.setMessage(revCommit.getFullMessage());
        
        // 分析代码变更
        try {
            List<CodeChange> changes = analyzeChanges(revCommit);
            commitInfo.setChanges(changes);
            
            // 计算新增和删除的行数
            int linesAdded = 0;
            int linesDeleted = 0;
            for (CodeChange change : changes) {
                linesAdded += change.getLinesAdded();
                linesDeleted += change.getLinesDeleted();
            }
            commitInfo.setLinesAdded(linesAdded);
            commitInfo.setLinesDeleted(linesDeleted);
            
        } catch (Exception e) {
            System.err.println("分析提交变更失败: " + e.getMessage());
        }
        
        return commitInfo;
    }
    
    /**
     * 分析提交的代码变更
     * 
     * 获取指定提交中所有文件的变更详情，包括新增、修改、删除的行号。
     * 
     * @param commit 提交对象
     * @return 代码变更列表
     * @throws Exception 分析失败时抛出异常
     */
    private List<CodeChange> analyzeChanges(RevCommit commit) throws Exception {
        List<CodeChange> changes = new ArrayList<>();
        
        // 获取父提交（第一个父提交，对于合并提交只考虑第一个父提交）
        RevCommit parent = null;
        if (commit.getParentCount() > 0) {
            parent = commit.getParent(0);
        }
        
        // 创建差异格式化器
        DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        diffFormatter.setRepository(repository);
        diffFormatter.setDetectRenames(true); // 检测重命名
        
        // 计算差异
        List<DiffEntry> diffs;
        if (parent != null) {
            diffs = diffFormatter.scan(parent.getTree(), commit.getTree());
        } else {
            // 初始提交，没有父提交
            ObjectId emptyTree = repository.newObjectInserter().idFor(
                org.eclipse.jgit.lib.Constants.OBJ_TREE, new byte[0]);
            diffs = diffFormatter.scan(emptyTree, commit.getTree());
        }
        
        // 处理每个差异条目
        for (DiffEntry diff : diffs) {
            CodeChange change = new CodeChange();
            
            // 设置文件路径
            String newPath = diff.getNewPath();
            String oldPath = diff.getOldPath();
            
            // 设置变更类型
            switch (diff.getChangeType()) {
                case ADD:
                    change.setFilePath(newPath);
                    change.setChangeType(CodeChange.ChangeType.ADD);
                    break;
                case MODIFY:
                    change.setFilePath(newPath);
                    change.setChangeType(CodeChange.ChangeType.MODIFY);
                    break;
                case DELETE:
                    change.setFilePath(oldPath);
                    change.setChangeType(CodeChange.ChangeType.DELETE);
                    break;
                case RENAME:
                    change.setFilePath(newPath);
                    change.setChangeType(CodeChange.ChangeType.RENAME);
                    break;
                default:
                    change.setFilePath(newPath);
                    change.setChangeType(CodeChange.ChangeType.MODIFY);
            }
            
            // 只处理Java文件
            if (newPath.endsWith(".java") || oldPath.endsWith(".java")) {
                // 分析具体行的变更
                try {
                    FileHeader fileHeader = diffFormatter.toFileHeader(diff);
                    analyzeFileChanges(fileHeader, change);
                } catch (Exception e) {
                    // 忽略单个文件的解析错误
                }
                
                changes.add(change);
            }
        }
        
        diffFormatter.close();
        return changes;
    }
    
    /**
     * 分析文件的具体变更
     * 
     * 提取文件中新增、修改、删除的具体行号。
     * 
     * @param fileHeader 文件头信息
     * @param change 代码变更对象（用于存储结果）
     */
    private void analyzeFileChanges(FileHeader fileHeader, CodeChange change) {
        List<Integer> addedLines = new ArrayList<>();
        List<Integer> modifiedLines = new ArrayList<>();
        List<Integer> deletedLines = new ArrayList<>();
        
        // 遍历所有hunk（代码块）
        for (HunkHeader hunk : fileHeader.getHunks()) {
            // 遍历每个编辑操作
            for (Edit edit : hunk.toEditList()) {
                switch (edit.getType()) {
                    case INSERT:
                        // 新增的行
                        for (int i = edit.getBeginB(); i < edit.getEndB(); i++) {
                            addedLines.add(i);
                        }
                        break;
                    case DELETE:
                        // 删除的行
                        for (int i = edit.getBeginA(); i < edit.getEndA(); i++) {
                            deletedLines.add(i);
                        }
                        break;
                    case REPLACE:
                        // 修改的行（先删除后新增）
                        for (int i = edit.getBeginA(); i < edit.getEndA(); i++) {
                            deletedLines.add(i);
                        }
                        for (int i = edit.getBeginB(); i < edit.getEndB(); i++) {
                            addedLines.add(i);
                        }
                        break;
                    case EMPTY:
                        // 空操作，不做处理
                        break;
                }
            }
        }
        
        change.setAddedLines(addedLines);
        change.setModifiedLines(modifiedLines);
        change.setDeletedLines(deletedLines);
    }
    
    /**
     * 获取开发者统计信息
     * 
     * 根据提交历史统计每个开发者的贡献情况。
     * 
     * @param commits 提交历史列表
     * @return 开发者统计信息映射（Key：开发者邮箱，Value：统计信息）
     */
    public Map<String, DeveloperStats> getDeveloperStatistics(List<CommitInfo> commits) {
        Map<String, DeveloperStats> statsMap = new HashMap<>();
        
        // 遍历所有提交
        for (CommitInfo commit : commits) {
            String email = commit.getAuthorEmail();
            
            // 获取或创建开发者统计对象
            DeveloperStats stats = statsMap.computeIfAbsent(email, k -> {
                DeveloperStats s = new DeveloperStats();
                s.setDeveloperEmail(email);
                s.setDeveloperName(commit.getAuthor());
                s.setTotalCommits(0);
                s.setTotalLinesAdded(0);
                s.setTotalLinesModified(0);
                s.setTotalLinesDeleted(0);
                s.setCurrentLinesOwned(0);
                return s;
            });
            
            // 累加统计信息
            stats.setTotalCommits(stats.getTotalCommits() + 1);
            stats.setTotalLinesAdded(stats.getTotalLinesAdded() + commit.getLinesAdded());
            stats.setTotalLinesDeleted(stats.getTotalLinesDeleted() + commit.getLinesDeleted());
            
            // 计算修改的行数（这里简化处理，将删除的行也算作修改）
            stats.setTotalLinesModified(stats.getTotalLinesModified() + commit.getLinesDeleted());
        }
        
        return statsMap;
    }
    
    /**
     * 统计当前有效代码行数（通过git blame）
     * 
     * 分析当前代码库中每个文件，统计每行代码的最后修改者，
     * 从而得到每个开发者在当前代码库中的有效代码贡献。
     * 
     * @param projectPath 项目路径
     * @param sourceDirectories 源码目录列表
     * @param statsMap 开发者统计信息映射（会被更新）
     */
    public void calculateCurrentLinesOwned(String projectPath, List<String> sourceDirectories, 
                                            Map<String, DeveloperStats> statsMap) {
        // 用于存储每个邮箱对应的开发者统计
        Map<String, Integer> linesByAuthor = new HashMap<>();
        int totalLines = 0;
        
        try {
            // 确保仓库已初始化
            if (repository == null) {
                initialize(projectPath);
            }
            
            // 遍历所有源码目录
            for (String sourceDir : sourceDirectories) {
                File dir = new File(projectPath, sourceDir);
                if (!dir.exists()) {
                    // 尝试绝对路径
                    dir = new File(sourceDir);
                }
                
                if (dir.exists() && dir.isDirectory()) {
                    // 递归处理所有Java文件
                    totalLines += processDirectoryForBlame(dir, "", linesByAuthor);
                }
            }
            
            // 更新开发者统计信息
            for (Map.Entry<String, DeveloperStats> entry : statsMap.entrySet()) {
                String email = entry.getKey();
                DeveloperStats stats = entry.getValue();
                
                // 统计当前有效代码行数
                int ownedLines = 0;
                for (Map.Entry<String, Integer> authorEntry : linesByAuthor.entrySet()) {
                    // 邮箱可能不完全匹配，尝试模糊匹配
                    if (authorEntry.getKey().equalsIgnoreCase(email) ||
                        authorEntry.getKey().contains(email) ||
                        email.contains(authorEntry.getKey())) {
                        ownedLines += authorEntry.getValue();
                    }
                }
                stats.setCurrentLinesOwned(ownedLines);
                
                // 计算贡献占比
                if (totalLines > 0) {
                    stats.setContributionPercentage((ownedLines * 100.0) / totalLines);
                }
            }
            
            // 对于没有提交记录但有代码贡献的开发者，添加到统计中
            for (Map.Entry<String, Integer> authorEntry : linesByAuthor.entrySet()) {
                String authorEmail = authorEntry.getKey();
                boolean found = false;
                
                for (String existingEmail : statsMap.keySet()) {
                    if (existingEmail.equalsIgnoreCase(authorEmail) ||
                        existingEmail.contains(authorEmail) ||
                        authorEmail.contains(existingEmail)) {
                        found = true;
                        break;
                    }
                }
                
                if (!found && authorEntry.getValue() > 0) {
                    DeveloperStats newStats = new DeveloperStats();
                    newStats.setDeveloperEmail(authorEmail);
                    newStats.setDeveloperName(extractNameFromEmail(authorEmail));
                    newStats.setCurrentLinesOwned(authorEntry.getValue());
                    if (totalLines > 0) {
                        newStats.setContributionPercentage((authorEntry.getValue() * 100.0) / totalLines);
                    }
                    statsMap.put(authorEmail, newStats);
                }
            }
            
        } catch (Exception e) {
            System.err.println("统计当前有效代码行数失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 递归处理目录，对每个Java文件执行blame分析
     */
    private int processDirectoryForBlame(File dir, String relativePath, Map<String, Integer> linesByAuthor) {
        int totalLines = 0;
        
        File[] files = dir.listFiles();
        if (files == null) return 0;
        
        for (File file : files) {
            String newRelativePath = relativePath.isEmpty() ? file.getName() : relativePath + "/" + file.getName();
            
            if (file.isDirectory()) {
                // 递归处理子目录
                totalLines += processDirectoryForBlame(file, newRelativePath, linesByAuthor);
            } else if (file.getName().endsWith(".java")) {
                // 处理Java文件
                totalLines += blameFile(newRelativePath, linesByAuthor);
            }
        }
        
        return totalLines;
    }
    
    /**
     * 对单个文件执行blame分析
     */
    private int blameFile(String filePath, Map<String, Integer> linesByAuthor) {
        try {
            BlameGenerator blameGenerator = new BlameGenerator(repository, filePath);
            BlameResult blameResult = blameGenerator.computeBlameResult();
            
            if (blameResult == null) {
                return 0;
            }
            
            int lineCount = 0;
            int lastIdx = blameResult.getResultContents() != null ? 
                          blameResult.getResultContents().size() : 0;
            
            for (int i = 0; i < lastIdx; i++) {
                try {
                    RevCommit commit = blameResult.getSourceCommit(i);
                    if (commit != null) {
                        String authorEmail = commit.getAuthorIdent().getEmailAddress();
                        linesByAuthor.merge(authorEmail, 1, Integer::sum);
                        lineCount++;
                    }
                } catch (Exception e) {
                    // 忽略单行解析错误
                }
            }
            
            blameGenerator.close();
            return lineCount;
            
        } catch (Exception e) {
            // 忽略单个文件的blame错误
            return 0;
        }
    }
    
    /**
     * 从邮箱提取名字（简化处理）
     */
    private String extractNameFromEmail(String email) {
        if (email == null || email.isEmpty()) return "Unknown";
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            String name = email.substring(0, atIndex);
            // 将点号或下划线转换为空格，并首字母大写
            name = name.replace(".", " ").replace("_", " ");
            StringBuilder result = new StringBuilder();
            for (String part : name.split(" ")) {
                if (!part.isEmpty()) {
                    result.append(Character.toUpperCase(part.charAt(0)))
                          .append(part.substring(1).toLowerCase())
                          .append(" ");
                }
            }
            return result.toString().trim();
        }
        return email;
    }
    
    /**
     * 获取指定提交的详细差异内容
     * 
     * 返回简化的差异内容，用于前端显示。
     * 
     * @param commitHash 提交哈希值
     * @return 差异内容字符串
     */
    public String getCommitDiff(String commitHash) {
        try {
            // 查找指定的提交
            RevCommit targetCommit = null;
            Iterable<RevCommit> commits = git.log().setMaxCount(100).call();
            for (RevCommit commit : commits) {
                if (commit.getId().getName().equals(commitHash)) {
                    targetCommit = commit;
                    break;
                }
            }
            
            if (targetCommit == null) {
                return "未找到提交: " + commitHash;
            }
            
            // 获取父提交
            RevCommit parent = null;
            if (targetCommit.getParentCount() > 0) {
                parent = targetCommit.getParent(0);
            }
            
            StringBuilder diffBuilder = new StringBuilder();
            
            diffBuilder.append("提交: ").append(targetCommit.getId().getName().substring(0, 7)).append("\n");
            diffBuilder.append("作者: ").append(targetCommit.getAuthorIdent().getName()).append("\n");
            diffBuilder.append("邮箱: ").append(targetCommit.getAuthorIdent().getEmailAddress()).append("\n");
            diffBuilder.append("日期: ").append(new Date(targetCommit.getCommitTime() * 1000L)).append("\n");
            diffBuilder.append("消息: ").append(targetCommit.getFullMessage().trim()).append("\n\n");
            
            // 创建差异格式化器
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DiffFormatter diffFormatter = new DiffFormatter(outputStream);
            diffFormatter.setRepository(repository);
            
            // 计算差异
            List<DiffEntry> diffs;
            if (parent != null) {
                diffs = diffFormatter.scan(parent.getTree(), targetCommit.getTree());
            } else {
                // 初始提交
                ObjectId emptyTree = repository.newObjectInserter().idFor(
                    org.eclipse.jgit.lib.Constants.OBJ_TREE, new byte[0]);
                diffs = diffFormatter.scan(emptyTree, targetCommit.getTree());
            }
            
            diffBuilder.append("=== 变更文件 ===\n\n");
            
            for (DiffEntry diff : diffs) {
                diffBuilder.append("[").append(diff.getChangeType().name()).append("] ");
                diffBuilder.append(diff.getOldPath());
                if (!diff.getOldPath().equals(diff.getNewPath())) {
                    diffBuilder.append(" -> ").append(diff.getNewPath());
                }
                diffBuilder.append("\n");
            }
            
            diffBuilder.append("\n=== 详细差异 ===\n\n");
            
            // 使用DiffFormatter输出标准diff格式
            for (DiffEntry diff : diffs) {
                diffFormatter.format(diff);
            }
            
            diffFormatter.close();
            
            // 将输出流转换为字符串
            String diffText = outputStream.toString();
            diffBuilder.append(diffText);
            
            outputStream.close();
            
            return diffBuilder.toString();
            
        } catch (Exception e) {
            return "获取差异失败: " + e.getMessage();
        }
    }
    
    /**
     * 关闭Git仓库
     * 
     * 释放Git仓库占用的资源。
     */
    public void close() {
        if (git != null) {
            git.close();
        }
        if (repository != null) {
            repository.close();
        }
    }
}
