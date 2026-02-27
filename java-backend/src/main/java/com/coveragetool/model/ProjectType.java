package com.coveragetool.model;

/**
 * 项目类型枚举 - 定义支持的Java项目构建工具类型
 * 
 * 不同的构建工具（Maven、Gradle等）有不同的目录结构和配置文件，
 * 这个枚举帮助系统识别项目类型并采用相应的扫描策略。
 */
public enum ProjectType {
    
    /**
     * Maven项目
     * 特征：存在pom.xml文件，标准目录结构为src/main/java和src/test/java
     * Maven是最常用的Java构建工具，具有规范的目录结构和依赖管理
     */
    MAVEN("Maven", "pom.xml"),
    
    /**
     * Gradle项目
     * 特征：存在build.gradle文件，目录结构类似Maven但配置更灵活
     * Gradle使用Groovy或Kotlin DSL进行配置，构建速度通常比Maven快
     */
    GRADLE("Gradle", "build.gradle"),
    
    /**
     * 未知或非标准项目
     * 当无法识别项目类型时使用，系统将尝试通用的扫描策略
     * 这种情况下可能需要用户手动指定源码和测试目录
     */
    UNKNOWN("Unknown", "");

    /**
     * 项目类型的显示名称
     * 用于在用户界面和报告中展示友好的类型名称
     */
    private final String displayName;
    
    /**
     * 项目配置文件的名称
     * 用于识别项目类型，通过检查文件系统中是否存在该文件来判断
     */
    private final String configFile;

    /**
     * 枚举构造函数
     * 
     * @param displayName 项目类型的显示名称
     * @param configFile 项目配置文件名称
     */
    ProjectType(String displayName, String configFile) {
        this.displayName = displayName;
        this.configFile = configFile;
    }

    /**
     * 获取项目类型的显示名称
     * @return 用于UI显示的友好名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取项目配置文件名称
     * @return 配置文件名（如pom.xml、build.gradle）
     */
    public String getConfigFile() {
        return configFile;
    }
}
