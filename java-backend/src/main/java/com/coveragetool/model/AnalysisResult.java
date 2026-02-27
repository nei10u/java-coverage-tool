package com.coveragetool.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 分析结果模型类 - 存储整个项目分析过程的完整结果
 * 
 * 这个类是整个分析系统的核心数据结构，包含了从项目扫描到覆盖率分析的所有数据。
 * 它作为各个分析模块之间的数据传递载体，也是最终报告生成的数据源。
 */
public class AnalysisResult {
    
    /**
     * 项目唯一标识符
     * 使用UUID确保每个分析任务都有唯一标识，便于前端查询和管理多个分析任务
     */
    private String projectId;
    
    /**
     * 分析执行的时间戳
     * 记录分析开始的时间，用于报告中的时间显示和历史记录追踪
     */
    private Date analysisTime;
    
    /**
     * 项目基本信息
     * 包含项目名称、路径、类型等基础信息，帮助用户确认分析的是哪个项目
     */
    private ProjectInfo projectInfo;
    
    /**
     * 识别出的业务类列表
     * 存储所有被分析的业务类（Service、Controller、Repository等），
     * 这些类是我们需要验证测试覆盖情况的目标对象
     */
    private List<BusinessClass> businessClasses;
    
    /**
     * 识别出的测试类列表
     * 存储所有单元测试类，这些类中包含的测试方法将用于验证业务类的覆盖情况
     */
    private List<TestClass> testClasses;
    
    /**
     * Git统计信息
     * 包含提交历史、开发者统计、代码变更等Git相关的分析数据，
     * 用于关联测试覆盖率与代码提交情况
     */
    private GitStatistics gitStatistics;
    
    /**
     * 覆盖率报告
     * 存储覆盖率的核心统计数据，包括方法级、类级、项目级的覆盖率百分比
     */
    private CoverageReport coverageReport;
    
    /**
     * 多维度报告数据
     * 支持从开发者、提交、文件、方法四个维度查看覆盖率数据，
     * 满足不同场景下的分析需求
     */
    private MultiDimensionalReport multiDimensionalReport;

    /**
     * 默认构造函数
     * 初始化分析结果对象，通常在开始新的分析任务时调用
     */
    public AnalysisResult() {
        // 在对象创建时自动记录当前时间作为分析时间
        this.analysisTime = new Date();
    }

    /**
     * 获取项目ID
     * @return 项目唯一标识符
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 设置项目ID
     * @param projectId 项目唯一标识符，通常使用UUID生成
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取分析时间
     * @return 分析执行的日期时间
     */
    public Date getAnalysisTime() {
        return analysisTime;
    }

    /**
     * 设置分析时间
     * @param analysisTime 分析执行的日期时间
     */
    public void setAnalysisTime(Date analysisTime) {
        this.analysisTime = analysisTime;
    }

    /**
     * 获取项目信息
     * @return 项目基本信息对象
     */
    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    /**
     * 设置项目信息
     * @param projectInfo 项目基本信息对象
     */
    public void setProjectInfo(ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    /**
     * 获取业务类列表
     * @return 所有识别到的业务类列表
     */
    public List<BusinessClass> getBusinessClasses() {
        return businessClasses;
    }

    /**
     * 设置业务类列表
     * @param businessClasses 业务类列表
     */
    public void setBusinessClasses(List<BusinessClass> businessClasses) {
        this.businessClasses = businessClasses;
    }

    /**
     * 获取测试类列表
     * @return 所有识别到的测试类列表
     */
    public List<TestClass> getTestClasses() {
        return testClasses;
    }

    /**
     * 设置测试类列表
     * @param testClasses 测试类列表
     */
    public void setTestClasses(List<TestClass> testClasses) {
        this.testClasses = testClasses;
    }

    /**
     * 获取Git统计信息
     * @return Git分析统计数据
     */
    public GitStatistics getGitStatistics() {
        return gitStatistics;
    }

    /**
     * 设置Git统计信息
     * @param gitStatistics Git分析统计数据
     */
    public void setGitStatistics(GitStatistics gitStatistics) {
        this.gitStatistics = gitStatistics;
    }

    /**
     * 获取覆盖率报告
     * @return 覆盖率分析报告
     */
    public CoverageReport getCoverageReport() {
        return coverageReport;
    }

    /**
     * 设置覆盖率报告
     * @param coverageReport 覆盖率分析报告
     */
    public void setCoverageReport(CoverageReport coverageReport) {
        this.coverageReport = coverageReport;
    }

    /**
     * 获取多维度报告
     * @return 多维度分析报告数据
     */
    public MultiDimensionalReport getMultiDimensionalReport() {
        return multiDimensionalReport;
    }

    /**
     * 设置多维度报告
     * @param multiDimensionalReport 多维度分析报告数据
     */
    public void setMultiDimensionalReport(MultiDimensionalReport multiDimensionalReport) {
        this.multiDimensionalReport = multiDimensionalReport;
    }
}
