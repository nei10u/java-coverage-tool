package com.coveragetool.model;

import java.util.List;

/**
 * 测试方法模型 - 表示单元测试类中的测试方法
 * 
 * 测试方法是实际执行测试的代码单元，通常使用@Test注解标记。
 * 每个测试方法应该验证业务方法的一个或多个场景。
 */
public class TestMethod {
    
    /**
     * 测试方法名称
     * 好的测试方法名应该描述测试的场景，如：testGetUserById_Success
     */
    private String methodName;
    
    /**
     * 测试方法所在的测试类名
     */
    private String belongingTestClass;
    
    /**
     * 该测试方法测试的业务方法
     * 通过方法名匹配或注释识别
     */
    private String testedBusinessMethod;
    
    /**
     * 测试方法中的断言数量
     * 断言数量反映了测试的验证程度
     */
    private int assertionCount;
    
    /**
     * 是否包含边界值测试
     * 边界值测试验证极端输入情况
     */
    private boolean hasBoundaryTests;
    
    /**
     * 是否包含异常测试
     * 异常测试验证错误处理逻辑
     */
    private boolean hasExceptionTests;
    
    /**
     * 是否使用了Mock对象
     * Mock用于隔离外部依赖，实现真正的单元测试
     */
    private boolean usesMocks;
    
    /**
     * 测试方法的代码行数
     * 过长的测试方法可能难以理解和维护
     */
    private int linesOfCode;
    
    /**
     * 测试方法中的注释列表
     * 注释有助于理解测试意图
     */
    private List<String> comments;

    // Getter和Setter方法
    
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getBelongingTestClass() {
        return belongingTestClass;
    }

    public void setBelongingTestClass(String belongingTestClass) {
        this.belongingTestClass = belongingTestClass;
    }

    public String getTestedBusinessMethod() {
        return testedBusinessMethod;
    }

    public void setTestedBusinessMethod(String testedBusinessMethod) {
        this.testedBusinessMethod = testedBusinessMethod;
    }

    public int getAssertionCount() {
        return assertionCount;
    }

    public void setAssertionCount(int assertionCount) {
        this.assertionCount = assertionCount;
    }

    public boolean isHasBoundaryTests() {
        return hasBoundaryTests;
    }

    public void setHasBoundaryTests(boolean hasBoundaryTests) {
        this.hasBoundaryTests = hasBoundaryTests;
    }

    public boolean isHasExceptionTests() {
        return hasExceptionTests;
    }

    public void setHasExceptionTests(boolean hasExceptionTests) {
        this.hasExceptionTests = hasExceptionTests;
    }

    public boolean isUsesMocks() {
        return usesMocks;
    }

    public void setUsesMocks(boolean usesMocks) {
        this.usesMocks = usesMocks;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
