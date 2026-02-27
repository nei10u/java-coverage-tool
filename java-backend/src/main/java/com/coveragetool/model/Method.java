package com.coveragetool.model;

import java.util.List;

/**
 * 方法模型 - 表示Java类中的方法
 * 
 * 方法是测试覆盖分析的最小单元。这个类存储了方法的完整信息，
 * 包括方法签名、参数、返回值等，以及该方法的测试覆盖状态。
 * 覆盖率分析的最终目标是确定每个业务方法是否有对应的测试。
 */
public class Method {
    
    /**
     * 方法名称
     * 如：getUserById、saveUser等
     * 方法名是方法的核心标识
     */
    private String methodName;
    
    /**
     * 方法的返回类型
     * 如：User、void、List<User>等
     * 返回类型有助于理解方法的功能
     */
    private String returnType;
    
    /**
     * 方法参数列表
     * 按顺序存储每个参数的类型，如：[Long, String]
     * 参数列表是方法签名的重要组成部分
     */
    private List<String> parameters;
    
    /**
     * 方法签名
     * 完整的方法签名，包括方法名和参数类型
     * 如：getUserById(Long)
     * 用于唯一标识一个方法（考虑重载情况）
     */
    private String signature;
    
    /**
     * 方法所在的类名
     * 该方法所属的类的完整限定名
     * 用于建立方法与类的关联关系
     */
    private String belongingClassName;
    
    /**
     * 方法是否被测试覆盖
     * true：存在对应的测试方法
     * false：没有对应的测试方法
     */
    private boolean isCovered;
    
    /**
     * 覆盖该方法的测试方法列表
     * 存储所有测试此业务方法的测试方法
     * 一个业务方法可能被多个测试方法测试（正常流程、异常流程等）
     */
    private List<TestMethod> coveringTestMethods;
    
    /**
     * 测试粒度评估结果
     * 评估测试方法的测试质量，包括是否覆盖边界值、异常情况等
     * 取值：EXCELLENT（优秀）、GOOD（良好）、ACCEPTABLE（可接受）、POOR（较差）
     */
    private GranularityLevel testGranularity;
    
    /**
     * 方法复杂度
     * 基于圈复杂度（Cyclomatic Complexity）计算
     * 复杂度越高，需要越多的测试用例来充分测试
     */
    private int complexity;
    
    /**
     * 该方法最后修改的Git提交信息
     * 记录最近一次修改此方法的提交，用于关联Git历史
     */
    private String lastModifiedCommit;

    /**
     * 方法起始行号
     * 方法在源文件中的起始行位置，用于定位代码
     */
    private int startLineNumber;

    /**
     * 方法结束行号
     * 方法在源文件中的结束行位置
     */
    private int endLineNumber;

    /**
     * 方法的代码行数
     * 计算方式：endLineNumber - startLineNumber + 1
     */
    private int linesOfCode;

    /**
     * 默认构造函数
     */
    public Method() {
    }

    /**
     * 带基本参数的构造函数
     * 
     * @param methodName 方法名称
     * @param returnType 返回类型
     * @param parameters 参数列表
     */
    public Method(String methodName, String returnType, List<String> parameters) {
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameters = parameters;
        // 自动生成方法签名
        this.signature = generateSignature(methodName, parameters);
    }

    /**
     * 生成方法签名
     * 
     * 方法签名用于唯一标识一个方法，格式为：方法名(参数类型列表)
     * 例如：getUserById(Long) 或 saveUser(User)
     * 
     * @param methodName 方法名称
     * @param parameters 参数类型列表
     * @return 方法签名字符串
     */
    private String generateSignature(String methodName, List<String> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(methodName).append("(");
        
        // 如果有参数，将参数类型用逗号连接
        if (parameters != null && !parameters.isEmpty()) {
            sb.append(String.join(", ", parameters));
        }
        
        sb.append(")");
        return sb.toString();
    }

    // Getter和Setter方法
    
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBelongingClassName() {
        return belongingClassName;
    }

    public void setBelongingClassName(String belongingClassName) {
        this.belongingClassName = belongingClassName;
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean covered) {
        isCovered = covered;
    }

    public List<TestMethod> getCoveringTestMethods() {
        return coveringTestMethods;
    }

    public void setCoveringTestMethods(List<TestMethod> coveringTestMethods) {
        this.coveringTestMethods = coveringTestMethods;
    }

    public GranularityLevel getTestGranularity() {
        return testGranularity;
    }

    public void setTestGranularity(GranularityLevel testGranularity) {
        this.testGranularity = testGranularity;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public String getLastModifiedCommit() {
        return lastModifiedCommit;
    }

    public void setLastModifiedCommit(String lastModifiedCommit) {
        this.lastModifiedCommit = lastModifiedCommit;
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
        // 自动计算代码行数
        this.linesOfCode = endLineNumber - startLineNumber + 1;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }
}
