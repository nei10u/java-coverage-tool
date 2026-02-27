package com.coveragetool.model;

import java.util.Date;

/**
 * 报告历史记录模型 - 存储已生成报告的元信息
 * 
 * 用于管理和追踪所有生成的报告，支持历史查看、删除等操作。
 */
public class ReportHistory {
    
    /**
     * 报告ID（与分析ID相同）
     */
    private String reportId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 项目路径
     */
    private String projectPath;
    
    /**
     * 报告生成时间
     */
    private Date generatedTime;
    
    /**
     * 总体覆盖率
     */
    private double overallCoverage;
    
    /**
     * 业务类总数
     */
    private int totalBusinessClasses;
    
    /**
     * 测试类总数
     */
    private int totalTestClasses;
    
    /**
     * 总方法数
     */
    private int totalMethods;
    
    /**
     * 已覆盖方法数
     */
    private int coveredMethods;
    
    /**
     * 未覆盖方法数
     */
    private int uncoveredMethods;
    
    /**
     * 报告保存路径（本地文件路径）
     */
    private String savedPath;
    
    /**
     * 报告状态
     * GENERATED: 已生成
     * SAVED: 已保存到文件
     * DELETED: 已删除
     */
    private ReportStatus status;
    
    /**
     * 报告文件大小（字节）
     */
    private long fileSize;

    /**
     * 无参构造函数
     */
    public ReportHistory() {
        this.generatedTime = new Date();
        this.status = ReportStatus.GENERATED;
    }

    /**
     * 从分析结果创建报告历史
     * 
     * @param reportId 报告ID
     * @param result 分析结果
     * @return 报告历史对象
     */
    public static ReportHistory fromAnalysisResult(String reportId, AnalysisResult result) {
        ReportHistory history = new ReportHistory();
        history.setReportId(reportId);
        history.setProjectName(result.getProjectInfo() != null ? 
            result.getProjectInfo().getProjectName() : "Unknown");
        history.setProjectPath(result.getProjectInfo() != null ? 
            result.getProjectInfo().getProjectPath() : "");
        history.setGeneratedTime(result.getAnalysisTime());
        
        if (result.getCoverageReport() != null) {
            CoverageReport coverage = result.getCoverageReport();
            history.setOverallCoverage(coverage.getOverallCoverage());
            history.setTotalBusinessClasses(coverage.getTotalBusinessClasses());
            history.setTotalTestClasses(coverage.getTotalTestClasses());
            history.setTotalMethods(coverage.getTotalMethods());
            history.setCoveredMethods(coverage.getCoveredMethods());
            history.setUncoveredMethods(coverage.getUncoveredMethods());
        }
        
        return history;
    }

    // Getter和Setter方法
    
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

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

    public Date getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(Date generatedTime) {
        this.generatedTime = generatedTime;
    }

    public double getOverallCoverage() {
        return overallCoverage;
    }

    public void setOverallCoverage(double overallCoverage) {
        this.overallCoverage = overallCoverage;
    }

    public int getTotalBusinessClasses() {
        return totalBusinessClasses;
    }

    public void setTotalBusinessClasses(int totalBusinessClasses) {
        this.totalBusinessClasses = totalBusinessClasses;
    }

    public int getTotalTestClasses() {
        return totalTestClasses;
    }

    public void setTotalTestClasses(int totalTestClasses) {
        this.totalTestClasses = totalTestClasses;
    }

    public int getTotalMethods() {
        return totalMethods;
    }

    public void setTotalMethods(int totalMethods) {
        this.totalMethods = totalMethods;
    }

    public int getCoveredMethods() {
        return coveredMethods;
    }

    public void setCoveredMethods(int coveredMethods) {
        this.coveredMethods = coveredMethods;
    }

    public int getUncoveredMethods() {
        return uncoveredMethods;
    }

    public void setUncoveredMethods(int uncoveredMethods) {
        this.uncoveredMethods = uncoveredMethods;
    }

    public String getSavedPath() {
        return savedPath;
    }

    public void setSavedPath(String savedPath) {
        this.savedPath = savedPath;
        if (savedPath != null) {
            this.status = ReportStatus.SAVED;
        }
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
