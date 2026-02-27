package com.coveragetool.model;

import java.util.List;

/**
 * 开发者统计模型 - 存储单个开发者的测试覆盖统计信息
 * 
 * 这个类汇总了某个开发者的所有Git提交和测试覆盖数据，
 * 用于评估开发者的代码质量和测试意识。
 */
public class DeveloperStats {
    
    /**
     * 开发者姓名
     */
    private String developerName;
    
    /**
     * 开发者邮箱
     */
    private String developerEmail;
    
    /**
     * 总提交次数
     */
    private int totalCommits;
    
    /**
     * 新增的代码总行数
     */
    private int totalLinesAdded;
    
    /**
     * 修改的代码总行数
     */
    private int totalLinesModified;
    
    /**
     * 删除的代码总行数
     */
    private int totalLinesDeleted;
    
    /**
     * 平均测试覆盖率
     * 所有提交的平均测试覆盖百分比
     */
    private double averageCoverageRate;
    
    /**
     * 未被测试覆盖的方法列表
     */
    private List<MethodCoverage> uncoveredMethods;
    
    /**
     * 未覆盖方法的数量
     */
    private int uncoveredMethodsCount;
    
    /**
     * 开发者评分（0-10分）
     * 综合考虑提交频率、代码质量、测试覆盖率等因素
     */
    private double score;

    // Getter和Setter方法
    
    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDeveloperEmail() {
        return developerEmail;
    }

    public void setDeveloperEmail(String developerEmail) {
        this.developerEmail = developerEmail;
    }

    public int getTotalCommits() {
        return totalCommits;
    }

    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }

    public int getTotalLinesAdded() {
        return totalLinesAdded;
    }

    public void setTotalLinesAdded(int totalLinesAdded) {
        this.totalLinesAdded = totalLinesAdded;
    }

    public int getTotalLinesModified() {
        return totalLinesModified;
    }

    public void setTotalLinesModified(int totalLinesModified) {
        this.totalLinesModified = totalLinesModified;
    }

    public int getTotalLinesDeleted() {
        return totalLinesDeleted;
    }

    public void setTotalLinesDeleted(int totalLinesDeleted) {
        this.totalLinesDeleted = totalLinesDeleted;
    }

    public double getAverageCoverageRate() {
        return averageCoverageRate;
    }

    public void setAverageCoverageRate(double averageCoverageRate) {
        this.averageCoverageRate = averageCoverageRate;
    }

    public List<MethodCoverage> getUncoveredMethods() {
        return uncoveredMethods;
    }

    public void setUncoveredMethods(List<MethodCoverage> uncoveredMethods) {
        this.uncoveredMethods = uncoveredMethods;
        // 自动计算未覆盖方法数量
        this.uncoveredMethodsCount = uncoveredMethods != null ? uncoveredMethods.size() : 0;
    }

    public int getUncoveredMethodsCount() {
        return uncoveredMethodsCount;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
