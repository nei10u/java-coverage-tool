package com.coveragetool.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Git统计信息模型 - 存储Git仓库的分析统计结果
 * 
 * 这个类汇总了Git仓库的所有分析数据，包括提交历史、开发者统计、
 * 代码变更趋势等。这些信息用于关联测试覆盖率与代码提交情况。
 */
public class GitStatistics {
    
    /**
     * 所有提交记录列表
     * 按时间倒序排列，最新的提交在前
     */
    private List<CommitInfo> commits;
    
    /**
     * 开发者统计信息映射
     * Key：开发者姓名或邮箱
     * Value：该开发者的统计数据
     */
    private Map<String, DeveloperStats> developerStats;
    
    /**
     * 覆盖率趋势数据
     * 记录覆盖率随时间的变化情况
     */
    private CoverageTrend trend;
    
    /**
     * 分析开始日期
     * 限制Git历史分析的时间范围
     */
    private Date sinceDate;
    
    /**
     * 分析结束日期
     */
    private Date untilDate;
    
    /**
     * 总提交次数
     */
    private int totalCommits;
    
    /**
     * 参与开发的开发者数量
     */
    private int totalDevelopers;

    // Getter和Setter方法
    
    public List<CommitInfo> getCommits() {
        return commits;
    }

    public void setCommits(List<CommitInfo> commits) {
        this.commits = commits;
    }

    public Map<String, DeveloperStats> getDeveloperStats() {
        return developerStats;
    }

    public void setDeveloperStats(Map<String, DeveloperStats> developerStats) {
        this.developerStats = developerStats;
    }

    public CoverageTrend getTrend() {
        return trend;
    }

    public void setTrend(CoverageTrend trend) {
        this.trend = trend;
    }

    public Date getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(Date sinceDate) {
        this.sinceDate = sinceDate;
    }

    public Date getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Date untilDate) {
        this.untilDate = untilDate;
    }

    public int getTotalCommits() {
        return totalCommits;
    }

    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }

    public int getTotalDevelopers() {
        return totalDevelopers;
    }

    public void setTotalDevelopers(int totalDevelopers) {
        this.totalDevelopers = totalDevelopers;
    }
}
