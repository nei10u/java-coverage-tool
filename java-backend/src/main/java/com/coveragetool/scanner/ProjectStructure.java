package com.coveragetool.scanner;

import com.coveragetool.model.ProjectType;
import com.coveragetool.model.DirectoryNode;
import java.util.List;

/**
 * 项目结构模型 - 存储扫描后的项目结构信息
 * 
 * 这个类封装了项目扫描的结果，包括项目类型、源码目录、测试目录等信息。
 * 这些信息将被用于后续的代码分析和覆盖率计算。
 */
public class ProjectStructure {
    
    /**
     * 项目根目录的绝对路径
     * 所有相对路径的基准路径
     */
    private String projectPath;
    
    /**
     * 项目类型
     * Maven、Gradle或Unknown
     */
    private ProjectType projectType;
    
    /**
     * 源码目录列表
     * 相对于项目根目录的路径，如：src/main/java
     */
    private List<String> sourceDirectories;
    
    /**
     * 测试目录列表
     * 相对于项目根目录的路径，如：src/test/java
     */
    private List<String> testDirectories;
    
    /**
     * 所有Java文件的绝对路径列表
     * 包含源码和测试代码的所有.java文件
     */
    private List<String> allJavaFiles;
    
    /**
     * 项目中的源码Java文件列表
     * 仅包含源码目录下的.java文件
     */
    private List<String> sourceFiles;
    
    /**
     * 项目中的测试Java文件列表
     * 仅包含测试目录下的.java文件
     */
    private List<String> testFiles;
    
    /**
     * 项目名称
     * 通常从项目根目录名称提取
     */
    private String projectName;
    
    /**
     * 完整的目录树结构
     * 用于前端展示可展开的目录树
     */
    private DirectoryNode directoryTree;
    
    /**
     * 源码目录树
     * 仅包含src/main/java下的内容
     */
    private DirectoryNode sourceTree;
    
    /**
     * 测试目录树
     * 仅包含src/test/java下的内容
     */
    private DirectoryNode testTree;
    
    /**
     * 是否为Git仓库
     * 用于判断是否可以进行提交级统计分析
     */
    private boolean isGitRepository;
    
    /**
     * 提交数量
     * 如果是Git仓库，记录提交数量
     */
    private int commitCount;

    // Getter和Setter方法
    
    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
        // 从路径中提取项目名称
        if (projectPath != null) {
            int lastSeparator = projectPath.lastIndexOf(java.io.File.separator);
            if (lastSeparator >= 0 && lastSeparator < projectPath.length() - 1) {
                this.projectName = projectPath.substring(lastSeparator + 1);
            }
        }
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public List<String> getSourceDirectories() {
        return sourceDirectories;
    }

    public void setSourceDirectories(List<String> sourceDirectories) {
        this.sourceDirectories = sourceDirectories;
    }

    public List<String> getTestDirectories() {
        return testDirectories;
    }

    public void setTestDirectories(List<String> testDirectories) {
        this.testDirectories = testDirectories;
    }

    public List<String> getAllJavaFiles() {
        return allJavaFiles;
    }

    public void setAllJavaFiles(List<String> allJavaFiles) {
        this.allJavaFiles = allJavaFiles;
    }

    public List<String> getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(List<String> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public List<String> getTestFiles() {
        return testFiles;
    }

    public void setTestFiles(List<String> testFiles) {
        this.testFiles = testFiles;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public DirectoryNode getDirectoryTree() {
        return directoryTree;
    }
    
    public void setDirectoryTree(DirectoryNode directoryTree) {
        this.directoryTree = directoryTree;
    }
    
    public DirectoryNode getSourceTree() {
        return sourceTree;
    }
    
    public void setSourceTree(DirectoryNode sourceTree) {
        this.sourceTree = sourceTree;
    }
    
    public DirectoryNode getTestTree() {
        return testTree;
    }
    
    public void setTestTree(DirectoryNode testTree) {
        this.testTree = testTree;
    }
    
    public boolean isGitRepository() {
        return isGitRepository;
    }
    
    public void setGitRepository(boolean gitRepository) {
        isGitRepository = gitRepository;
    }
    
    public int getCommitCount() {
        return commitCount;
    }
    
    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }
}
