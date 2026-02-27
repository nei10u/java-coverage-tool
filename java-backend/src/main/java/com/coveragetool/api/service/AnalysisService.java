package com.coveragetool.api.service;

import com.coveragetool.api.dto.AnalysisRequest;
import com.coveragetool.analyzer.CodeAnalyzer;
import com.coveragetool.coverage.CoverageAnalyzer;
import com.coveragetool.git.GitAnalyzer;
import com.coveragetool.model.*;
import com.coveragetool.report.ReportGenerator;
import com.coveragetool.scanner.ProjectScanner;
import com.coveragetool.scanner.ProjectStructure;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 分析服务 - 协调各个分析模块完成完整的分析流程
 * 
 * 这个服务类整合了项目扫描、代码分析、Git分析、覆盖率分析等功能，
 * 提供完整的分析能力。
 */
public class AnalysisService {
    
    /**
     * 项目扫描器
     */
    private ProjectScanner projectScanner;
    
    /**
     * 代码分析器
     */
    private CodeAnalyzer codeAnalyzer;
    
    /**
     * Git分析器
     */
    private GitAnalyzer gitAnalyzer;
    
    /**
     * 覆盖率分析器
     */
    private CoverageAnalyzer coverageAnalyzer;
    
    /**
     * 报告生成器
     */
    private ReportGenerator reportGenerator;
    
    /**
     * 分析结果缓存
     * Key：分析ID，Value：分析结果
     */
    private Map<String, AnalysisResult> analysisResults;
    
    /**
     * 分析进度缓存
     * Key：分析ID，Value：进度信息
     */
    private Map<String, AnalysisProgress> analysisProgress;
    
    /**
     * 报告历史列表
     */
    private List<ReportHistory> reportHistoryList;
    
    /**
     * 默认报告保存路径
     */
    private String defaultReportPath;
    
    /**
     * JSON序列化器
     */
    private Gson gson;
    
    /**
     * 构造函数 - 初始化各个分析组件
     */
    public AnalysisService() {
        this.projectScanner = new ProjectScanner();
        this.codeAnalyzer = new CodeAnalyzer();
        this.gitAnalyzer = new GitAnalyzer();
        this.coverageAnalyzer = new CoverageAnalyzer();
        this.reportGenerator = new ReportGenerator();
        this.analysisResults = new HashMap<>();
        this.analysisProgress = new HashMap<>();
        this.reportHistoryList = new ArrayList<>();
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        
        // 初始化默认报告保存路径
        String userHome = System.getProperty("user.home");
        this.defaultReportPath = userHome + File.separator + "java-coverage-reports";
        
        // 确保报告目录存在
        ensureReportDirectory();
        
        // 加载已有的报告历史
        loadReportHistory();
    }
    
    /**
     * 扫描项目
     * 
     * @param projectPath 项目路径
     * @return 项目结构信息
     */
    public ProjectStructure scanProject(String projectPath) {
        return projectScanner.scan(projectPath);
    }
    
    /**
     * 开始分析
     * 
     * @param request 分析请求
     * @return 分析ID
     */
    public String startAnalysis(AnalysisRequest request) {
        // 生成唯一分析ID
        String analysisId = UUID.randomUUID().toString();
        
        // 初始化进度
        AnalysisProgress progress = new AnalysisProgress(analysisId);
        analysisProgress.put(analysisId, progress);
        
        // 异步执行分析（避免阻塞）
        new Thread(() -> {
            try {
                executeAnalysis(analysisId, request);
            } catch (Exception e) {
                progress.setStatus("ERROR");
                progress.setMessage("分析失败: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
        
        return analysisId;
    }
    
    /**
     * 执行分析
     * 
     * @param analysisId 分析ID
     * @param request 分析请求
     */
    private void executeAnalysis(String analysisId, AnalysisRequest request) {
        AnalysisProgress progress = analysisProgress.get(analysisId);
        
        try {
            // 1. 扫描项目
            progress.update("SCANNING", 10, "正在扫描项目结构...");
            ProjectStructure structure = projectScanner.scan(request.getProjectPath());
            
            // 2. 分析业务类
            progress.update("ANALYZING_BUSINESS", 30, "正在分析业务类...");
            List<BusinessClass> businessClasses = codeAnalyzer.analyzeBusinessClasses(
                request.getSourceDirectories(), request.getProjectPath());
            
            // 3. 分析测试类
            progress.update("ANALYZING_TESTS", 50, "正在分析测试类...");
            List<TestClass> testClasses = codeAnalyzer.analyzeTestClasses(
                request.getTestDirectories(), request.getProjectPath());
            
            // 4. Git分析
            progress.update("ANALYZING_GIT", 70, "正在分析Git历史...");
            GitStatistics gitStatistics = analyzeGit(request);
            
            // 5. 覆盖率分析
            progress.update("ANALYZING_COVERAGE", 85, "正在分析覆盖率...");
            CoverageReport coverageReport = coverageAnalyzer.analyzeCoverage(
                businessClasses, testClasses);
            
            // 5.5 生成提交级统计（如果项目是Git仓库）
            if (gitStatistics.getTotalCommits() > 0) {
                progress.update("ANALYZING_COMMIT_STATS", 90, "正在生成提交级统计...");
                List<CommitStatistics> commitStats = generateCommitStatistics(
                    gitStatistics, businessClasses, testClasses, request.getProjectPath());
                coverageReport.setCommitStatisticsList(commitStats);
            }
            
            // 6. 生成多维度报告
            progress.update("GENERATING_REPORT", 95, "正在生成报告...");
            MultiDimensionalReport multiDimensionalReport = generateMultiDimensionalReport(
                businessClasses, testClasses, gitStatistics, coverageReport);
            
            // 7. 创建分析结果
            AnalysisResult result = new AnalysisResult();
            result.setProjectId(analysisId);
            result.setProjectInfo(createProjectInfo(structure));
            result.setBusinessClasses(businessClasses);
            result.setTestClasses(testClasses);
            result.setGitStatistics(gitStatistics);
            result.setCoverageReport(coverageReport);
            result.setMultiDimensionalReport(multiDimensionalReport);
            
            // 保存结果
            analysisResults.put(analysisId, result);
            
            // 完成分析
            progress.update("COMPLETED", 100, "分析完成");
            
        } catch (Exception e) {
            progress.update("ERROR", 0, "分析失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 分析Git历史
     */
    private GitStatistics analyzeGit(AnalysisRequest request) {
        GitStatistics gitStatistics = new GitStatistics();
        
        try {
            gitAnalyzer.initialize(request.getProjectPath());
            
            // 获取提交历史
            Date since = null;
            Date until = null;
            if (request.getGitOptions() != null) {
                since = request.getGitOptions().getSince();
                until = request.getGitOptions().getUntil();
            }
            
            List<CommitInfo> commits = gitAnalyzer.getCommitHistory(
                request.getProjectPath(), since, until);
            gitStatistics.setCommits(commits);
            gitStatistics.setTotalCommits(commits.size());
            
            // 获取开发者统计
            Map<String, DeveloperStats> developerStats = 
                gitAnalyzer.getDeveloperStatistics(commits);
            gitStatistics.setDeveloperStats(developerStats);
            gitStatistics.setTotalDevelopers(developerStats.size());
            
            gitStatistics.setSinceDate(since);
            gitStatistics.setUntilDate(until != null ? until : new Date());
            
        } catch (Exception e) {
            System.err.println("Git分析失败: " + e.getMessage());
        } finally {
            gitAnalyzer.close();
        }
        
        return gitStatistics;
    }
    
    /**
     * 生成提交级统计数据
     * 
     * 根据Git提交历史和代码分析结果，生成每个提交的覆盖率统计信息。
     * 
     * @param gitStatistics Git统计信息
     * @param businessClasses 业务类列表
     * @param testClasses 测试类列表
     * @param projectPath 项目路径
     * @return 提交级统计列表
     */
    private List<CommitStatistics> generateCommitStatistics(
            GitStatistics gitStatistics,
            List<BusinessClass> businessClasses,
            List<TestClass> testClasses,
            String projectPath) {
        
        List<CommitStatistics> commitStatsList = new ArrayList<>();
        
        // 获取所有提交信息
        List<CommitInfo> commits = gitStatistics.getCommits();
        
        if (commits == null || commits.isEmpty()) {
            return commitStatsList;
        }
        
        // 为每个提交生成统计信息
        for (CommitInfo commit : commits) {
            CommitStatistics commitStats = new CommitStatistics();
            
            // 设置基本信息
            commitStats.setCommitHash(commit.getCommitHash());
            commitStats.setAuthorName(commit.getAuthor());
            commitStats.setAuthorEmail(commit.getAuthorEmail());
            commitStats.setCommitDate(commit.getCommitDate());
            commitStats.setCommitMessage(commit.getMessage());
            commitStats.setLinesAdded(commit.getLinesAdded());
            commitStats.setLinesDeleted(commit.getLinesDeleted());
            
            // 分析本次提交影响的方法和覆盖率
            analyzeCommitImpact(commit, businessClasses, testClasses, commitStats, projectPath);
            
            commitStatsList.add(commitStats);
        }
        
        return commitStatsList;
    }
    
    /**
     * 分析提交的影响
     * 
     * 计算一次提交中新增/修改的方法数量，以及这些方法的测试覆盖情况。
     * 
     * @param commit 提交信息
     * @param businessClasses 业务类列表
     * @param testClasses 测试类列表
     * @param commitStats 提交统计对象（用于存储结果）
     * @param projectPath 项目路径
     */
    private void analyzeCommitImpact(CommitInfo commit,
                                     List<BusinessClass> businessClasses,
                                     List<TestClass> testClasses,
                                     CommitStatistics commitStats,
                                     String projectPath) {
        
        int methodsAdded = 0;
        int methodsModified = 0;
        int addedMethodsCovered = 0;
        int modifiedMethodsCovered = 0;
        List<String> affectedFiles = new ArrayList<>();
        
        // 获取本次提交的代码变更
        List<CodeChange> changes = commit.getChanges();
        if (changes == null || changes.isEmpty()) {
            commitStats.setMethodsAdded(0);
            commitStats.setMethodsModified(0);
            commitStats.setAddedMethodsCovered(0);
            commitStats.setModifiedMethodsCovered(0);
            commitStats.setAddedCodeCoverage(0.0);
            commitStats.setModifiedCodeCoverage(0.0);
            commitStats.setAffectedFiles(affectedFiles);
            return;
        }
        
        // 遍历每个文件变更
        for (CodeChange change : changes) {
            String filePath = change.getFilePath();
            affectedFiles.add(filePath);
            
            // 找到对应的业务类
            BusinessClass affectedClass = findBusinessClassByPath(filePath, businessClasses, projectPath);
            
            if (affectedClass != null) {
                // 找到对应的测试类
                TestClass testClass = findTestClassForBusinessClass(affectedClass, testClasses);
                
                // 分析新增行对应的方法
                for (Integer lineNum : change.getAddedLines()) {
                    Method method = findMethodByLineNumber(lineNum, affectedClass);
                    if (method != null) {
                        methodsAdded++;
                        
                        // 检查该方法是否被测试覆盖
                        if (isMethodCovered(method, testClass)) {
                            addedMethodsCovered++;
                        }
                    }
                }
            }
        }
        
        // 设置统计结果
        commitStats.setMethodsAdded(methodsAdded);
        commitStats.setMethodsModified(methodsModified);
        commitStats.setAddedMethodsCovered(addedMethodsCovered);
        commitStats.setModifiedMethodsCovered(modifiedMethodsCovered);
        commitStats.setAffectedFiles(affectedFiles);
        
        // 计算覆盖率
        double addedCoverage = methodsAdded > 0 
            ? (addedMethodsCovered * 100.0 / methodsAdded) 
            : 0.0;
        double modifiedCoverage = methodsModified > 0 
            ? (modifiedMethodsCovered * 100.0 / methodsModified) 
            : 0.0;
        
        commitStats.setAddedCodeCoverage(addedCoverage);
        commitStats.setModifiedCodeCoverage(modifiedCoverage);
    }
    
    /**
     * 根据文件路径查找业务类
     */
    private BusinessClass findBusinessClassByPath(String filePath, 
                                                   List<BusinessClass> businessClasses,
                                                   String projectPath) {
        for (BusinessClass businessClass : businessClasses) {
            if (businessClass.getFilePath() != null) {
                // 规范化路径进行比较
                String normalizedFilePath = filePath.replace("/", File.separator);
                String normalizedClassPath = businessClass.getFilePath()
                    .replace("/", File.separator);
                
                if (normalizedFilePath.endsWith(normalizedClassPath) || 
                    normalizedClassPath.endsWith(normalizedFilePath)) {
                    return businessClass;
                }
            }
        }
        return null;
    }
    
    /**
     * 查找业务类对应的测试类
     */
    private TestClass findTestClassForBusinessClass(BusinessClass businessClass,
                                                     List<TestClass> testClasses) {
        String businessClassName = businessClass.getClassName();
        
        for (TestClass testClass : testClasses) {
            // 测试类名通常是：业务类名 + Test
            if (testClass.getClassName().equals(businessClassName + "Test") ||
                testClass.getClassName().equals(businessClassName + "Tests")) {
                return testClass;
            }
        }
        
        return null;
    }
    
    /**
     * 根据行号查找方法
     */
    private Method findMethodByLineNumber(int lineNumber, BusinessClass businessClass) {
        for (Method method : businessClass.getMethods()) {
            if (method.getStartLineNumber() <= lineNumber && 
                method.getEndLineNumber() >= lineNumber) {
                return method;
            }
        }
        return null;
    }
    
    /**
     * 检查方法是否被测试覆盖
     */
    private boolean isMethodCovered(Method method, TestClass testClass) {
        if (testClass == null) {
            return false;
        }
        
        String methodName = method.getMethodName().toLowerCase();
        
        for (TestMethod testMethod : testClass.getTestMethods()) {
            String testMethodName = testMethod.getMethodName().toLowerCase();
            
            // 如果测试方法名包含业务方法名，认为已被覆盖
            if (testMethodName.contains(methodName)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 生成多维度报告
     */
    private MultiDimensionalReport generateMultiDimensionalReport(
            List<BusinessClass> businessClasses,
            List<TestClass> testClasses,
            GitStatistics gitStatistics,
            CoverageReport coverageReport) {
        
        MultiDimensionalReport report = new MultiDimensionalReport();
        
        // TODO: 实现详细的多维度报告生成逻辑
        // 这里暂时返回空报告，后续可以完善
        
        return report;
    }
    
    /**
     * 创建项目信息
     */
    private ProjectInfo createProjectInfo(ProjectStructure structure) {
        ProjectInfo info = new ProjectInfo();
        info.setProjectName(structure.getProjectName());
        info.setProjectPath(structure.getProjectPath());
        info.setProjectType(structure.getProjectType());
        return info;
    }
    
    /**
     * 获取分析结果
     */
    public AnalysisResult getAnalysisResult(String analysisId) {
        return analysisResults.get(analysisId);
    }
    
    /**
     * 获取分析进度
     */
    public AnalysisProgress getAnalysisProgress(String analysisId) {
        return analysisProgress.get(analysisId);
    }
    
    /**
     * 导出报告
     */
    public String exportReport(String analysisId) {
        AnalysisResult result = analysisResults.get(analysisId);
        if (result == null) {
            throw new RuntimeException("分析结果不存在: " + analysisId);
        }
        return reportGenerator.generateHTMLReport(result);
    }
    
    /**
     * 保存报告到指定路径
     * 
     * @param analysisId 分析ID
     * @param savePath 保存路径（目录）
     * @return 保存的文件路径
     */
    public String saveReport(String analysisId, String savePath) {
        AnalysisResult result = analysisResults.get(analysisId);
        if (result == null) {
            throw new RuntimeException("分析结果不存在: " + analysisId);
        }
        
        // 如果未指定路径，使用默认路径
        if (savePath == null || savePath.isEmpty()) {
            savePath = defaultReportPath;
        }
        
        // 确保目录存在
        Path dirPath = Paths.get(savePath);
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            throw new RuntimeException("无法创建报告目录: " + e.getMessage());
        }
        
        // 生成文件名
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String projectName = result.getProjectInfo() != null ? 
            result.getProjectInfo().getProjectName() : "unknown";
        String fileName = String.format("coverage-report-%s-%s.html", 
            projectName.replaceAll("[^a-zA-Z0-9]", "_"), timestamp);
        
        Path filePath = dirPath.resolve(fileName);
        
        // 生成HTML报告
        String htmlContent = reportGenerator.generateHTMLReport(result);
        
        // 写入文件
        try {
            Files.write(filePath, htmlContent.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("保存报告失败: " + e.getMessage());
        }
        
        // 更新报告历史
        updateReportHistory(analysisId, filePath.toString(), htmlContent.length());
        
        return filePath.toString();
    }
    
    /**
     * 获取报告历史列表
     */
    public List<ReportHistory> getReportHistory() {
        return new ArrayList<>(reportHistoryList);
    }
    
    /**
     * 删除报告
     * 
     * @param reportId 报告ID
     * @return 是否成功删除
     */
    public boolean deleteReport(String reportId) {
        // 从历史列表中查找
        ReportHistory historyToDelete = null;
        for (ReportHistory history : reportHistoryList) {
            if (history.getReportId().equals(reportId)) {
                historyToDelete = history;
                break;
            }
        }
        
        if (historyToDelete == null) {
            return false;
        }
        
        // 删除文件
        if (historyToDelete.getSavedPath() != null) {
            try {
                Files.deleteIfExists(Paths.get(historyToDelete.getSavedPath()));
            } catch (IOException e) {
                System.err.println("删除报告文件失败: " + e.getMessage());
            }
        }
        
        // 从内存中移除
        reportHistoryList.remove(historyToDelete);
        analysisResults.remove(reportId);
        
        // 更新状态为已删除
        historyToDelete.setStatus(ReportStatus.DELETED);
        
        // 保存历史记录
        saveReportHistory();
        
        return true;
    }
    
    /**
     * 设置默认报告保存路径
     */
    public void setDefaultReportPath(String path) {
        this.defaultReportPath = path;
        ensureReportDirectory();
    }
    
    /**
     * 获取默认报告保存路径
     */
    public String getDefaultReportPath() {
        return defaultReportPath;
    }
    
    /**
     * 确保报告目录存在
     */
    private void ensureReportDirectory() {
        Path path = Paths.get(defaultReportPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                System.err.println("无法创建报告目录: " + e.getMessage());
            }
        }
    }
    
    /**
     * 更新报告历史
     */
    private void updateReportHistory(String analysisId, String savedPath, long fileSize) {
        AnalysisResult result = analysisResults.get(analysisId);
        if (result == null) return;
        
        // 查找是否已存在
        ReportHistory history = null;
        for (ReportHistory h : reportHistoryList) {
            if (h.getReportId().equals(analysisId)) {
                history = h;
                break;
            }
        }
        
        if (history == null) {
            history = ReportHistory.fromAnalysisResult(analysisId, result);
            reportHistoryList.add(history);
        }
        
        history.setSavedPath(savedPath);
        history.setFileSize(fileSize);
        history.setStatus(ReportStatus.SAVED);
        
        // 保存历史记录
        saveReportHistory();
    }
    
    /**
     * 保存报告历史到文件
     */
    private void saveReportHistory() {
        Path historyFile = Paths.get(defaultReportPath, "report-history.json");
        try {
            String json = gson.toJson(reportHistoryList);
            Files.write(historyFile, json.getBytes("UTF-8"));
        } catch (IOException e) {
            System.err.println("保存报告历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 从文件加载报告历史
     */
    @SuppressWarnings("unchecked")
    private void loadReportHistory() {
        Path historyFile = Paths.get(defaultReportPath, "report-history.json");
        if (Files.exists(historyFile)) {
            try {
                String json = new String(Files.readAllBytes(historyFile), "UTF-8");
                List<ReportHistory> loaded = gson.fromJson(json, 
                    new com.google.gson.reflect.TypeToken<List<ReportHistory>>(){}.getType());
                if (loaded != null) {
                    reportHistoryList = loaded;
                }
            } catch (Exception e) {
                System.err.println("加载报告历史失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 分析进度内部类
     */
    public static class AnalysisProgress {
        private String analysisId;
        private String stage;
        private int progress;
        private String message;
        private Date startTime;
        private Date updateTime;
        
        public AnalysisProgress(String analysisId) {
            this.analysisId = analysisId;
            this.stage = "INITIALIZING";
            this.progress = 0;
            this.message = "初始化中...";
            this.startTime = new Date();
            this.updateTime = new Date();
        }
        
        public void update(String stage, int progress, String message) {
            this.stage = stage;
            this.progress = progress;
            this.message = message;
            this.updateTime = new Date();
        }
        
        // Getter方法
        public String getAnalysisId() { return analysisId; }
        public String getStage() { return stage; }
        public int getProgress() { return progress; }
        public String getMessage() { return message; }
        public Date getStartTime() { return startTime; }
        public Date getUpdateTime() { return updateTime; }
        public void setStatus(String status) { this.stage = status; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 读取文件内容
     * 
     * 用于在前端显示文件的具体代码内容。
     * 
     * @param filePath 要读取的文件路径
     * @return 文件内容模型，包含文件路径、内容、行号等信息
     * @throws IOException 如果文件读取失败
     */
    public FileContent readFileContent(String filePath) throws IOException {
        // 创建Path对象
        Path path = Paths.get(filePath);
        
        // 检查文件是否存在
        if (!Files.exists(path)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }
        
        // 检查是否为文件（不是目录）
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("路径不是文件: " + filePath);
        }
        
        // 读取文件所有行
        List<String> lines = Files.readAllLines(path);
        
        // 将行列表转换为带行号的格式
        List<FileContent.Line> numberedLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            numberedLines.add(new FileContent.Line(i + 1, lines.get(i)));
        }
        
        // 创建并返回FileContent对象
        FileContent fileContent = new FileContent();
        fileContent.setFilePath(filePath);
        fileContent.setFileName(path.getFileName().toString());
        fileContent.setTotalLines(lines.size());
        fileContent.setLines(numberedLines);
        fileContent.setRawContent(String.join("\n", lines));
        
        return fileContent;
    }
    
    /**
     * 获取提交差异内容
     * 
     * 用于在前端显示提交的代码变更详情。
     * 
     * @param commitHash 提交哈希值
     * @return 差异内容字符串
     */
    public String getCommitDiff(String commitHash) {
        try {
            return gitAnalyzer.getCommitDiff(commitHash);
        } catch (Exception e) {
            return "获取提交差异失败: " + e.getMessage();
        }
    }
}
