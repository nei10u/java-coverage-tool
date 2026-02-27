package com.coveragetool.model;

import java.util.List;
import java.util.Map;

/**
 * 覆盖率报告模型 - 存储项目的测试覆盖率统计信息
 * 
 * 这个类汇总了整个项目的测试覆盖率数据，包括方法级、类级、项目级的
 * 覆盖率统计，以及未覆盖方法的详细列表。
 */
public class CoverageReport {
    
    /**
     * 项目总体覆盖率
     * 计算公式：(已覆盖方法数 / 总方法数) * 100
     */
    private double overallCoverage;
    
    /**
     * 业务类总数
     */
    private int totalBusinessClasses;
    
    /**
     * 已覆盖的业务类数（至少有一个方法被测试覆盖）
     */
    private int coveredBusinessClasses;
    
    /**
     * 测试类总数
     */
    private int totalTestClasses;
    
    /**
     * 测试方法总数
     */
    private int totalTestMethods;
    
    /**
     * 业务方法总数
     */
    private int totalMethods;
    
    /**
     * 已覆盖的方法数
     */
    private int coveredMethods;
    
    /**
     * 未覆盖的方法数
     */
    private int uncoveredMethods;
    
    /**
     * 未覆盖的方法详细列表
     */
    private List<MethodCoverage> uncoveredMethodList;
    
    /**
     * 所有方法列表（用于方法级报告）
     */
    private List<MethodCoverage> allMethodsList;
    
    /**
     * 文件级统计列表（用于文件级报告）
     */
    private List<FileStatistics> fileStatisticsList;
    
    /**
     * 提交级统计列表（用于提交级报告）
     */
    private List<CommitStatistics> commitStatisticsList;
    
    /**
     * 测试粒度分布
     * Key：粒度等级（EXCELLENT、GOOD、ACCEPTABLE、POOR）
     * Value：该等级的方法数量
     */
    private Map<GranularityLevel, Integer> granularityDistribution;
    
    /**
     * 平均测试粒度评分
     */
    private double averageGranularityScore;

    // Getter和Setter方法
    
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

    public int getCoveredBusinessClasses() {
        return coveredBusinessClasses;
    }

    public void setCoveredBusinessClasses(int coveredBusinessClasses) {
        this.coveredBusinessClasses = coveredBusinessClasses;
    }

    public int getTotalTestClasses() {
        return totalTestClasses;
    }

    public void setTotalTestClasses(int totalTestClasses) {
        this.totalTestClasses = totalTestClasses;
    }

    public int getTotalTestMethods() {
        return totalTestMethods;
    }

    public void setTotalTestMethods(int totalTestMethods) {
        this.totalTestMethods = totalTestMethods;
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

    public List<MethodCoverage> getUncoveredMethodList() {
        return uncoveredMethodList;
    }

    public void setUncoveredMethodList(List<MethodCoverage> uncoveredMethodList) {
        this.uncoveredMethodList = uncoveredMethodList;
        this.uncoveredMethods = uncoveredMethodList != null ? uncoveredMethodList.size() : 0;
    }

    public java.util.Map<GranularityLevel, Integer> getGranularityDistribution() {
        return granularityDistribution;
    }

    public void setGranularityDistribution(java.util.Map<GranularityLevel, Integer> granularityDistribution) {
        this.granularityDistribution = granularityDistribution;
    }

    public double getAverageGranularityScore() {
        return averageGranularityScore;
    }

    public void setAverageGranularityScore(double averageGranularityScore) {
        this.averageGranularityScore = averageGranularityScore;
    }

    public List<MethodCoverage> getAllMethodsList() {
        return allMethodsList;
    }

    public void setAllMethodsList(List<MethodCoverage> allMethodsList) {
        this.allMethodsList = allMethodsList;
    }

    public List<FileStatistics> getFileStatisticsList() {
        return fileStatisticsList;
    }

    public void setFileStatisticsList(List<FileStatistics> fileStatisticsList) {
        this.fileStatisticsList = fileStatisticsList;
    }

    public List<CommitStatistics> getCommitStatisticsList() {
        return commitStatisticsList;
    }

    public void setCommitStatisticsList(List<CommitStatistics> commitStatisticsList) {
        this.commitStatisticsList = commitStatisticsList;
    }
}
