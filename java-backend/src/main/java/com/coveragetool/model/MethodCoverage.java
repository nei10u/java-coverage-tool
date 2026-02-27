package com.coveragetool.model;

/**
 * 方法覆盖信息模型 - 记录单个方法的测试覆盖详情
 * 
 * 这个类用于在报告中展示每个方法的覆盖情况，包括方法基本信息
 * 和测试覆盖状态。
 */
public class MethodCoverage {
    
    /**
     * 方法所属的类名（完整限定名）
     */
    private String className;
    
    /**
     * 方法名称
     */
    private String methodName;
    
    /**
     * 方法签名
     * 包含方法名和参数类型
     */
    private String signature;
    
    /**
     * 是否被测试覆盖
     */
    private boolean isCovered;
    
    /**
     * 覆盖该方法的测试方法数量
     */
    private int testMethodCount;
    
    /**
     * 测试粒度等级
     */
    private GranularityLevel granularityLevel;
    
    /**
     * 最后修改该方法的Git提交哈希
     */
    private String lastModifiedCommit;
    
    /**
     * 最后修改该方法的开发者
     */
    private String lastModifiedBy;
    
    /**
     * 方法复杂度
     */
    private int complexity;

    /**
     * 方法起始行号
     */
    private int startLineNumber;

    /**
     * 方法结束行号
     */
    private int endLineNumber;

    /**
     * 方法代码行数
     */
    private int linesOfCode;

    /**
     * 完整方法签名（包含返回类型）
     * 格式：返回类型 方法名(参数列表)
     */
    private String fullSignature;

    /**
     * 方法所属文件的相对路径
     */
    private String filePath;

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

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        isCovered = covered;
    }

    public int getTestMethodCount() {
        return testMethodCount;
    }

    public void setTestMethodCount(int testMethodCount) {
        this.testMethodCount = testMethodCount;
    }

    public GranularityLevel getGranularityLevel() {
        return granularityLevel;
    }

    public void setGranularityLevel(GranularityLevel granularityLevel) {
        this.granularityLevel = granularityLevel;
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

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public int getStartLineNumber() {
        return startLineNumber;
    }

    public void setStartLineNumber(int startLineNumber) {
        this.startLineNumber = startLineNumber;
    }

    public int getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(int endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public String getFullSignature() {
        return fullSignature;
    }

    public void setFullSignature(String fullSignature) {
        this.fullSignature = fullSignature;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
