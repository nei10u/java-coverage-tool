package com.coveragetool.model;

import java.util.Date;
import java.util.List;

/**
 * 覆盖率趋势模型 - 记录测试覆盖率随时间的变化趋势
 * 
 * 通过分析Git历史和覆盖率数据，展示项目测试覆盖率的演变过程，
 * 帮助团队了解测试质量的变化趋势。
 */
public class CoverageTrend {
    
    /**
     * 趋势数据点列表
     * 每个数据点记录某个时间点的覆盖率信息
     */
    private List<TrendDataPoint> dataPoints;
    
    /**
     * 整体趋势方向
     * IMPROVING（改善中）、DECLINING（下降中）、STABLE（稳定）
     */
    private TrendDirection direction;
    
    /**
     * 平均覆盖率
     */
    private double averageCoverage;
    
    /**
     * 最高覆盖率
     */
    private double maxCoverage;
    
    /**
     * 最低覆盖率
     */
    private double minCoverage;

    /**
     * 趋势数据点内部类 - 单个时间点的覆盖率记录
     */
    public static class TrendDataPoint {
        /**
         * 数据点的时间
         */
        private Date date;
        
        /**
         * 该时间点的覆盖率
         */
        private double coverageRate;
        
        /**
         * 关联的提交哈希
         */
        private String commitHash;
        
        /**
         * 提交者
         */
        private String author;
        
        /**
         * 当时的方法总数
         */
        private int totalMethods;
        
        /**
         * 当时的已覆盖方法数
         */
        private int coveredMethods;

        // Getter和Setter方法
        
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public double getCoverageRate() {
            return coverageRate;
        }

        public void setCoverageRate(double coverageRate) {
            this.coverageRate = coverageRate;
        }

        public String getCommitHash() {
            return commitHash;
        }

        public void setCommitHash(String commitHash) {
            this.commitHash = commitHash;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
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
    }

    /**
     * 趋势方向枚举
     */
    public enum TrendDirection {
        /**
         * 覆盖率在改善
         */
        IMPROVING("改善中"),
        
        /**
         * 覆盖率在下降
         */
        DECLINING("下降中"),
        
        /**
         * 覆盖率保持稳定
         */
        STABLE("稳定");

        private final String displayName;

        TrendDirection(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Getter和Setter方法
    
    public List<TrendDataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<TrendDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public TrendDirection getDirection() {
        return direction;
    }

    public void setDirection(TrendDirection direction) {
        this.direction = direction;
    }

    public double getAverageCoverage() {
        return averageCoverage;
    }

    public void setAverageCoverage(double averageCoverage) {
        this.averageCoverage = averageCoverage;
    }

    public double getMaxCoverage() {
        return maxCoverage;
    }

    public void setMaxCoverage(double maxCoverage) {
        this.maxCoverage = maxCoverage;
    }

    public double getMinCoverage() {
        return minCoverage;
    }

    public void setMinCoverage(double minCoverage) {
        this.minCoverage = minCoverage;
    }
}
