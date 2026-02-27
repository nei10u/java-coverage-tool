package com.coveragetool.model;

import java.util.Map;

/**
 * 多维度报告模型 - 支持从不同维度查看测试覆盖数据
 * 
 * 这个类提供了四个维度的报告数据，用户可以通过Tab切换查看：
 * 1. 开发者级 - 每个开发者的测试覆盖统计
 * 2. 提交级 - 每次提交的测试覆盖详情
 * 3. 文件级 - 每个文件的测试覆盖统计
 * 4. 方法级 - 每个方法的测试覆盖详情
 */
public class MultiDimensionalReport {
    
    /**
     * 开发者级报告
     * Key：开发者邮箱
     * Value：该开发者的详细报告
     */
    private Map<String, DeveloperReport> developerReports;
    
    /**
     * 提交级报告
     * Key：提交哈希
     * Value：该提交的详细报告
     */
    private Map<String, CommitReport> commitReports;
    
    /**
     * 文件级报告
     * Key：文件路径
     * Value：该文件的详细报告
     */
    private Map<String, FileReport> fileReports;
    
    /**
     * 方法级报告
     * Key：方法签名（类名.方法名）
     * Value：该方法的详细报告
     */
    private Map<String, MethodReport> methodReports;

    /**
     * 开发者报告内部类
     */
    public static class DeveloperReport {
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
         * 新增代码行数
         */
        private int linesAdded;
        
        /**
         * 修改代码行数
         */
        private int linesModified;
        
        /**
         * 平均覆盖率
         */
        private double averageCoverage;
        
        /**
         * 未覆盖方法数
         */
        private int uncoveredMethodsCount;
        
        /**
         * 综合评分（0-10分）
         */
        private double score;
        
        /**
         * 贡献的提交列表（哈希值）
         */
        private java.util.List<String> commitHashes;

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

        public int getLinesAdded() {
            return linesAdded;
        }

        public void setLinesAdded(int linesAdded) {
            this.linesAdded = linesAdded;
        }

        public int getLinesModified() {
            return linesModified;
        }

        public void setLinesModified(int linesModified) {
            this.linesModified = linesModified;
        }

        public double getAverageCoverage() {
            return averageCoverage;
        }

        public void setAverageCoverage(double averageCoverage) {
            this.averageCoverage = averageCoverage;
        }

        public int getUncoveredMethodsCount() {
            return uncoveredMethodsCount;
        }

        public void setUncoveredMethodsCount(int uncoveredMethodsCount) {
            this.uncoveredMethodsCount = uncoveredMethodsCount;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public java.util.List<String> getCommitHashes() {
            return commitHashes;
        }

        public void setCommitHashes(java.util.List<String> commitHashes) {
            this.commitHashes = commitHashes;
        }
    }

    /**
     * 提交报告内部类
     */
    public static class CommitReport {
        /**
         * 提交哈希
         */
        private String commitHash;
        
        /**
         * 提交者
         */
        private String author;
        
        /**
         * 提交时间
         */
        private java.util.Date commitDate;
        
        /**
         * 提交消息
         */
        private String message;
        
        /**
         * 变更文件列表
         */
        private java.util.List<String> changedFiles;
        
        /**
         * 新增代码覆盖率
         */
        private double addedCodeCoverage;
        
        /**
         * 修改代码覆盖率
         */
        private double modifiedCodeCoverage;
        
        /**
         * 新增行数
         */
        private int linesAdded;
        
        /**
         * 删除行数
         */
        private int linesDeleted;

        // Getter和Setter方法
        
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

        public java.util.Date getCommitDate() {
            return commitDate;
        }

        public void setCommitDate(java.util.Date commitDate) {
            this.commitDate = commitDate;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public java.util.List<String> getChangedFiles() {
            return changedFiles;
        }

        public void setChangedFiles(java.util.List<String> changedFiles) {
            this.changedFiles = changedFiles;
        }

        public double getAddedCodeCoverage() {
            return addedCodeCoverage;
        }

        public void setAddedCodeCoverage(double addedCodeCoverage) {
            this.addedCodeCoverage = addedCodeCoverage;
        }

        public double getModifiedCodeCoverage() {
            return modifiedCodeCoverage;
        }

        public void setModifiedCodeCoverage(double modifiedCodeCoverage) {
            this.modifiedCodeCoverage = modifiedCodeCoverage;
        }

        public int getLinesAdded() {
            return linesAdded;
        }

        public void setLinesAdded(int linesAdded) {
            this.linesAdded = linesAdded;
        }

        public int getLinesDeleted() {
            return linesDeleted;
        }

        public void setLinesDeleted(int linesDeleted) {
            this.linesDeleted = linesDeleted;
        }
    }

    /**
     * 文件报告内部类
     */
    public static class FileReport {
        /**
         * 文件路径
         */
        private String filePath;
        
        /**
         * 文件类型（业务类/测试类）
         */
        private String fileType;
        
        /**
         * 类类型（Service、Controller等）
         */
        private String classType;
        
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
         * 覆盖率
         */
        private double coverageRate;
        
        /**
         * 最后修改的提交
         */
        private String lastModifiedCommit;
        
        /**
         * 最后修改者
         */
        private String lastModifiedBy;

        // Getter和Setter方法
        
        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getClassType() {
            return classType;
        }

        public void setClassType(String classType) {
            this.classType = classType;
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

        public double getCoverageRate() {
            return coverageRate;
        }

        public void setCoverageRate(double coverageRate) {
            this.coverageRate = coverageRate;
        }

        public String getLastModifiedCommit() {
            return lastModifiedCommit;
        }

        public void setLastModifiedCommit(String lastModifiedCommit) {
            this.lastModifiedCommit = lastModifiedCommit;
        }

        public String getLastModifiedBy() {
            return lastModifiedBy;
        }

        public void setLastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
        }
    }

    /**
     * 方法报告内部类
     */
    public static class MethodReport {
        /**
         * 方法所属类名
         */
        private String className;
        
        /**
         * 方法名
         */
        private String methodName;
        
        /**
         * 方法签名
         */
        private String signature;
        
        /**
         * 覆盖状态
         */
        private String status;
        
        /**
         * 覆盖该方法的测试方法列表
         */
        private java.util.List<String> testMethods;
        
        /**
         * 测试粒度
         */
        private String testGranularity;
        
        /**
         * 最后修改时间
         */
        private java.util.Date lastModified;
        
        /**
         * 最后修改者
         */
        private String lastModifiedBy;
        
        /**
         * 方法复杂度
         */
        private int complexity;

        // Getter和Setter方法
        
        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public java.util.List<String> getTestMethods() {
            return testMethods;
        }

        public void setTestMethods(java.util.List<String> testMethods) {
            this.testMethods = testMethods;
        }

        public String getTestGranularity() {
            return testGranularity;
        }

        public void setTestGranularity(String testGranularity) {
            this.testGranularity = testGranularity;
        }

        public java.util.Date getLastModified() {
            return lastModified;
        }

        public void setLastModified(java.util.Date lastModified) {
            this.lastModified = lastModified;
        }

        public String getLastModifiedBy() {
            return lastModifiedBy;
        }

        public void setLastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
        }

        public int getComplexity() {
            return complexity;
        }

        public void setComplexity(int complexity) {
            this.complexity = complexity;
        }
    }

    // Getter和Setter方法
    
    public Map<String, DeveloperReport> getDeveloperReports() {
        return developerReports;
    }

    public void setDeveloperReports(Map<String, DeveloperReport> developerReports) {
        this.developerReports = developerReports;
    }

    public Map<String, CommitReport> getCommitReports() {
        return commitReports;
    }

    public void setCommitReports(Map<String, CommitReport> commitReports) {
        this.commitReports = commitReports;
    }

    public Map<String, FileReport> getFileReports() {
        return fileReports;
    }

    public void setFileReports(Map<String, FileReport> fileReports) {
        this.fileReports = fileReports;
    }

    public Map<String, MethodReport> getMethodReports() {
        return methodReports;
    }

    public void setMethodReports(Map<String, MethodReport> methodReports) {
        this.methodReports = methodReports;
    }
}
