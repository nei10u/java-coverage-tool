package com.coveragetool.scanner;

import com.coveragetool.model.DirectoryNode;
import com.coveragetool.model.ProjectType;
import java.io.File;
import java.util.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * 项目扫描器 - 扫描Java项目结构并识别目录布局
 * 
 * 这个类负责扫描Java项目的文件系统结构，识别项目类型（Maven/Gradle），
 * 找出源码目录和测试目录。这是分析的第一步，为后续的代码分析做准备。
 */
public class ProjectScanner {
    
    /**
     * 扫描项目并返回项目结构信息
     * 
     * 这是主要的扫描方法，接收项目根路径，分析项目结构，
     * 识别项目类型和目录布局。
     * 
     * @param projectPath 项目的根目录绝对路径
     * @return ProjectStructure 包含项目结构信息的对象
     */
    public ProjectStructure scan(String projectPath) {
        // 创建项目根目录的File对象
        File projectRoot = new File(projectPath);
        
        // 验证项目路径是否有效
        if (!projectRoot.exists() || !projectRoot.isDirectory()) {
            throw new IllegalArgumentException("项目路径不存在或不是目录: " + projectPath);
        }
        
        // 创建项目结构对象来存储扫描结果
        ProjectStructure structure = new ProjectStructure();
        structure.setProjectPath(projectPath);
        
        // 检测项目类型（Maven、Gradle等）
        ProjectType projectType = detectProjectType(projectRoot);
        structure.setProjectType(projectType);
        
        // 根据项目类型检测源码目录
        List<String> sourceDirectories = detectSourceDirectories(projectRoot, projectType);
        structure.setSourceDirectories(sourceDirectories);
        
        // 根据项目类型检测测试目录
        List<String> testDirectories = detectTestDirectories(projectRoot, projectType);
        structure.setTestDirectories(testDirectories);
        
        // 扫描所有Java文件
        List<String> allJavaFiles = scanJavaFiles(projectRoot);
        structure.setAllJavaFiles(allJavaFiles);
        
        // 构建完整目录树
        DirectoryNode directoryTree = buildDirectoryTree(projectRoot, sourceDirectories, testDirectories);
        structure.setDirectoryTree(directoryTree);
        
        // 构建源码目录树（仅src/main/java下的内容）
        DirectoryNode sourceTree = buildSourceTree(projectRoot, sourceDirectories);
        structure.setSourceTree(sourceTree);
        
        // 构建测试目录树（仅src/test/java下的内容）
        DirectoryNode testTree = buildTestTree(projectRoot, testDirectories);
        structure.setTestTree(testTree);
        
        // 检查是否为Git仓库并统计提交数量
        int commitCount = checkGitRepositoryAndGetCommitCount(projectRoot);
        structure.setGitRepository(commitCount >= 0);
        structure.setCommitCount(commitCount >= 0 ? commitCount : 0);
        
        return structure;
    }
    
    /**
     * 检查项目是否为Git仓库并统计提交数量
     * 
     * 使用JGit库检查项目是否为Git仓库，并统计提交数量。
     * 
     * @param projectRoot 项目根目录
     * @return 提交数量（如果是Git仓库），-1（如果不是Git仓库）
     */
    private int checkGitRepositoryAndGetCommitCount(File projectRoot) {
        // 首先检查.git目录是否存在
        File gitDir = new File(projectRoot, ".git");
        if (!gitDir.exists() || !gitDir.isDirectory()) {
            return -1;  // 不是Git仓库
        }
        
        // 使用JGit统计提交数量
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder
                .setGitDir(gitDir)
                .readEnvironment()
                .findGitDir()
                .build();
            
            Git git = new Git(repository);
            
            // 获取所有提交
            Iterable<RevCommit> commits = git.log().all().call();
            
            int count = 0;
            for (RevCommit commit : commits) {
                count++;
                // 限制最大统计数量，避免大型仓库耗时过长
                if (count >= 10000) {
                    break;
                }
            }
            
            // 关闭资源
            git.close();
            repository.close();
            
            return count;
            
        } catch (Exception e) {
            // 如果Git操作失败，仍然认为它是Git仓库，但提交数量为0
            System.err.println("统计Git提交数量失败: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * 检测项目类型
     * 
     * 通过检查项目根目录下的配置文件来判断项目类型。
     * - 存在pom.xml -> Maven项目
     * - 存在build.gradle -> Gradle项目
     * - 都不存在 -> 未知类型
     * 
     * @param projectRoot 项目根目录
     * @return 检测到的项目类型
     */
    public ProjectType detectProjectType(File projectRoot) {
        // 检查是否存在pom.xml（Maven项目标志）
        File pomXml = new File(projectRoot, "pom.xml");
        if (pomXml.exists()) {
            return ProjectType.MAVEN;
        }
        
        // 检查是否存在build.gradle（Gradle项目标志）
        File buildGradle = new File(projectRoot, "build.gradle");
        if (buildGradle.exists()) {
            return ProjectType.GRADLE;
        }
        
        // 也检查build.gradle.kts（Gradle Kotlin DSL）
        File buildGradleKts = new File(projectRoot, "build.gradle.kts");
        if (buildGradleKts.exists()) {
            return ProjectType.GRADLE;
        }
        
        // 都不存在，返回未知类型
        return ProjectType.UNKNOWN;
    }
    
    /**
     * 检测源码目录
     * 
     * 根据项目类型检测标准的源码目录。
     * Maven/Gradle标准结构：src/main/java
     * 
     * @param projectRoot 项目根目录
     * @param projectType 项目类型
     * @return 检测到的源码目录列表（相对路径）
     */
    private List<String> detectSourceDirectories(File projectRoot, ProjectType projectType) {
        List<String> sourceDirs = new ArrayList<>();
        
        // Maven/Gradle标准源码目录
        File standardSourceDir = new File(projectRoot, "src/main/java");
        if (standardSourceDir.exists() && standardSourceDir.isDirectory()) {
            sourceDirs.add("src/main/java");
        }
        
        return sourceDirs;
    }
    
    /**
     * 检测测试目录
     * 
     * 根据项目类型检测标准的测试目录。
     * Maven/Gradle标准结构：src/test/java
     * 
     * @param projectRoot 项目根目录
     * @param projectType 项目类型
     * @return 检测到的测试目录列表（相对路径）
     */
    private List<String> detectTestDirectories(File projectRoot, ProjectType projectType) {
        List<String> testDirs = new ArrayList<>();
        
        // Maven/Gradle标准测试目录
        File standardTestDir = new File(projectRoot, "src/test/java");
        if (standardTestDir.exists() && standardTestDir.isDirectory()) {
            testDirs.add("src/test/java");
        }
        
        return testDirs;
    }
    
    /**
     * 扫描所有Java文件
     * 
     * 递归扫描项目中的所有.java文件。
     * 
     * @param projectRoot 项目根目录
     * @return Java文件的绝对路径列表
     */
    private List<String> scanJavaFiles(File projectRoot) {
        List<String> javaFiles = new ArrayList<>();
        scanJavaFilesRecursive(projectRoot, projectRoot, javaFiles);
        return javaFiles;
    }
    
    /**
     * 递归扫描Java文件的辅助方法
     * 
     * @param currentDir 当前扫描的目录
     * @param projectRoot 项目根目录
     * @param javaFiles 存储Java文件路径的列表
     */
    private void scanJavaFilesRecursive(File currentDir, File projectRoot, List<String> javaFiles) {
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            return;
        }
        
        File[] files = currentDir.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                // 跳过隐藏目录和build目录
                if (!file.getName().startsWith(".") && 
                    !file.getName().equals("target") && 
                    !file.getName().equals("build")) {
                    scanJavaFilesRecursive(file, projectRoot, javaFiles);
                }
            } else if (file.getName().endsWith(".java")) {
                // 添加Java文件的绝对路径
                javaFiles.add(file.getAbsolutePath());
            }
        }
    }
    
    /**
     * 构建完整目录树
     * 
     * @param projectRoot 项目根目录
     * @param sourceDirectories 源码目录列表
     * @param testDirectories 测试目录列表
     * @return 目录树的根节点
     */
    private DirectoryNode buildDirectoryTree(File projectRoot, 
                                              List<String> sourceDirectories, 
                                              List<String> testDirectories) {
        // 创建根节点
        DirectoryNode root = new DirectoryNode();
        root.setTitle(projectRoot.getName());
        root.setKey("");
        root.setValue("");
        root.setType("directory");
        
        // 添加src目录
        DirectoryNode srcNode = new DirectoryNode();
        srcNode.setTitle("src");
        srcNode.setKey("src");
        srcNode.setValue("src");
        srcNode.setType("directory");
        
        // 添加main目录
        DirectoryNode mainNode = new DirectoryNode();
        mainNode.setTitle("main");
        mainNode.setKey("src/main");
        mainNode.setValue("src/main");
        mainNode.setType("directory");
        
        // 添加java目录
        DirectoryNode javaNode = new DirectoryNode();
        javaNode.setTitle("java");
        javaNode.setKey("src/main/java");
        javaNode.setValue("src/main/java");
        javaNode.setType("directory");
        
        // 构建java目录的子节点
        List<DirectoryNode> javaChildren = buildPackageStructure(
            new File(projectRoot, "src/main/java"), "src/main/java");
        javaNode.setChildren(javaChildren);
        
        List<DirectoryNode> mainChildren = new ArrayList<>();
        mainChildren.add(javaNode);
        mainNode.setChildren(mainChildren);
        
        List<DirectoryNode> srcChildren = new ArrayList<>();
        srcChildren.add(mainNode);
        
        // 添加test目录
        DirectoryNode testNode = new DirectoryNode();
        testNode.setTitle("test");
        testNode.setKey("src/test");
        testNode.setValue("src/test");
        testNode.setType("directory");
        
        DirectoryNode testJavaNode = new DirectoryNode();
        testJavaNode.setTitle("java");
        testJavaNode.setKey("src/test/java");
        testJavaNode.setValue("src/test/java");
        testJavaNode.setType("directory");
        
        List<DirectoryNode> testJavaChildren = buildPackageStructure(
            new File(projectRoot, "src/test/java"), "src/test/java");
        testJavaNode.setChildren(testJavaChildren);
        
        List<DirectoryNode> testChildren = new ArrayList<>();
        testChildren.add(testJavaNode);
        testNode.setChildren(testChildren);
        
        srcChildren.add(testNode);
        srcNode.setChildren(srcChildren);
        
        List<DirectoryNode> rootChildren = new ArrayList<>();
        rootChildren.add(srcNode);
        root.setChildren(rootChildren);
        
        return root;
    }
    
    /**
     * 构建包结构
     * 
     * @param directory 目录
     * @param basePath 基础路径
     * @return 目录节点列表
     */
    private List<DirectoryNode> buildPackageStructure(File directory, String basePath) {
        List<DirectoryNode> nodes = new ArrayList<>();
        
        if (!directory.exists() || !directory.isDirectory()) {
            return nodes;
        }
        
        File[] files = directory.listFiles();
        if (files == null) {
            return nodes;
        }
        
        // 排序：目录在前，文件在后
        Arrays.sort(files, (a, b) -> {
            boolean aIsDir = a.isDirectory();
            boolean bIsDir = b.isDirectory();
            if (aIsDir && !bIsDir) return -1;
            if (!aIsDir && bIsDir) return 1;
            return a.getName().compareTo(b.getName());
        });
        
        for (File file : files) {
            DirectoryNode node = new DirectoryNode();
            String nodePath = basePath + "/" + file.getName();
            node.setTitle(file.getName());
            node.setKey(nodePath);
            node.setValue(nodePath);
            
            if (file.isDirectory()) {
                node.setType("directory");
                node.setChildren(buildPackageStructure(file, nodePath));
            } else {
                node.setType("file");
                node.setChildren(new ArrayList<>());
            }
            
            nodes.add(node);
        }
        
        return nodes;
    }
    
    /**
     * 构建源码目录树
     * 
     * @param projectRoot 项目根目录
     * @param sourceDirectories 源码目录列表
     * @return 源码目录树
     */
    private DirectoryNode buildSourceTree(File projectRoot, List<String> sourceDirectories) {
        if (sourceDirectories.isEmpty()) {
            DirectoryNode empty = new DirectoryNode();
            empty.setTitle("src/main/java");
            empty.setKey("src/main/java");
            empty.setValue("src/main/java");
            empty.setType("directory");
            empty.setChildren(new ArrayList<>());
            return empty;
        }
        
        String sourcePath = sourceDirectories.get(0);
        File sourceDir = new File(projectRoot, sourcePath);
        
        DirectoryNode root = new DirectoryNode();
        root.setTitle("src/main/java");
        root.setKey(sourcePath);
        root.setValue(sourcePath);
        root.setType("directory");
        root.setChildren(buildPackageStructure(sourceDir, sourcePath));
        
        return root;
    }
    
    /**
     * 构建测试目录树
     * 
     * @param projectRoot 项目根目录
     * @param testDirectories 测试目录列表
     * @return 测试目录树
     */
    private DirectoryNode buildTestTree(File projectRoot, List<String> testDirectories) {
        if (testDirectories.isEmpty()) {
            DirectoryNode empty = new DirectoryNode();
            empty.setTitle("src/test/java");
            empty.setKey("src/test/java");
            empty.setValue("src/test/java");
            empty.setType("directory");
            empty.setChildren(new ArrayList<>());
            return empty;
        }
        
        String testPath = testDirectories.get(0);
        File testDir = new File(projectRoot, testPath);
        
        DirectoryNode root = new DirectoryNode();
        root.setTitle("src/test/java");
        root.setKey(testPath);
        root.setValue(testPath);
        root.setType("directory");
        root.setChildren(buildPackageStructure(testDir, testPath));
        
        return root;
    }
}
