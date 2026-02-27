package com.coveragetool.model;

/**
 * 项目信息模型类 - 存储Java项目的基本信息
 * 
 * 这个类封装了被分析项目的基础元数据，包括项目路径、名称、类型等信息。
 * 这些信息在分析开始时收集，用于标识和描述被分析的项目。
 */
public class ProjectInfo {
    
    /**
     * 项目名称
     * 通常从项目根目录名称或pom.xml/build.gradle中提取
     * 用于在报告中显示易于理解的项目标识
     */
    private String projectName;
    
    /**
     * 项目绝对路径
     * 项目在文件系统中的完整路径，作为所有相对路径的基准
     * 所有后续的文件扫描和分析都基于此路径
     */
    private String projectPath;
    
    /**
     * 项目类型
     * 识别项目的构建工具类型，如Maven、Gradle等
     * 不同项目类型可能需要采用不同的扫描策略
     */
    private ProjectType projectType;
    
    /**
     * 项目版本
     * 从pom.xml或build.gradle中提取的版本号
     * 用于版本追踪和报告中显示
     */
    private String version;
    
    /**
     * 项目描述
     * 从构建文件中提取的项目描述信息
     * 帮助用户更好地理解项目用途
     */
    private String description;

    /**
     * 默认构造函数
     */
    public ProjectInfo() {
    }

    /**
     * 带参数的构造函数 - 快速创建包含基本信息的ProjectInfo对象
     * 
     * @param projectName 项目名称
     * @param projectPath 项目绝对路径
     * @param projectType 项目类型（Maven/Gradle等）
     */
    public ProjectInfo(String projectName, String projectPath, ProjectType projectType) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.projectType = projectType;
    }

    // Getter和Setter方法
    
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
