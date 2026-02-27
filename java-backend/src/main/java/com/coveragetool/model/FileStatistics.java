package com.coveragetool.model;

import java.util.List;

/**
 * 文件统计模型 - 存储单个文件的测试覆盖统计
 * 
 * 用于文件级报告，展示每个源文件的覆盖情况。
 */
public class FileStatistics {
    
    /**
     * 文件路径（相对路径）
     */
    private String filePath;
    
    /**
     * 类名（简单名称）
     */
    private String className;
    
    /**
     * 类的完整限定名
     */
    private String fullyQualifiedName;
    
    /**
     * 包名
     */
    private String packageName;
    
    /**
     * 类类型（Service、Controller等）
     */
    private ClassType classType;
    
    /**
     * 该文件中的方法总数
     */
    private int totalMethods;
    
    /**
     * 该文件中已覆盖的方法数
     */
    private int coveredMethods;
    
    /**
     * 该文件中未覆盖的方法数
     */
    private int uncoveredMethods;
    
    /**
     * 该文件的覆盖率百分比
     */
    private double coverageRate;
    
    /**
     * 对应的测试类名
     */
    private String correspondingTestClass;
    
    /**
     * 该文件对应的测试类是否存在
     */
    private boolean hasTestClass;
    
    /**
     * 最后修改该文件的Git提交哈希
     */
    private String lastModifiedCommit;
    
    /**
     * 最后修改该文件的开发者
     */
    private String lastModifiedBy;
    
    /**
     * 代码行数
     */
    private int linesOfCode;
    
    /**
     * 该文件中的方法覆盖详情列表
     * 用于下钻展示每个方法的覆盖情况
     */
    private List<MethodCoverage> methods;

    // Getter和Setter方法
    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
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

    public String getCorrespondingTestClass() {
        return correspondingTestClass;
    }

    public void setCorrespondingTestClass(String correspondingTestClass) {
        this.correspondingTestClass = correspondingTestClass;
    }

    public boolean isHasTestClass() {
        return hasTestClass;
    }

    public void setHasTestClass(boolean hasTestClass) {
        this.hasTestClass = hasTestClass;
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

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }
    
    public List<MethodCoverage> getMethods() {
        return methods;
    }
    
    public void setMethods(List<MethodCoverage> methods) {
        this.methods = methods;
    }
}
