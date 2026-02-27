package com.coveragetool.model;

import java.util.List;

/**
 * 测试类模型 - 表示单元测试类
 * 
 * 测试类是包含测试方法的Java类，通常以Test结尾命名。
 * 这个类存储测试类的信息及其包含的所有测试方法。
 * 测试类用于验证业务类的功能是否正确。
 */
public class TestClass {
    
    /**
     * 测试类的完整限定名
     * 如：com.example.service.UserServiceTest
     */
    private String fullyQualifiedName;
    
    /**
     * 测试类的简单名称
     * 如：UserServiceTest
     */
    private String className;
    
    /**
     * 测试文件的绝对路径
     */
    private String filePath;
    
    /**
     * 包名
     */
    private String packageName;
    
    /**
     * 测试类中的所有测试方法
     * 每个测试方法使用@Test注解标记
     */
    private List<TestMethod> testMethods;
    
    /**
     * 该测试类对应的业务类名称
     * 根据测试类名推测的业务类名，如UserServiceTest对应UserService
     */
    private String correspondingBusinessClass;
    
    /**
     * 测试框架类型
     * 如：JUNIT4、JUNIT5、TESTNG
     */
    private TestFramework testFramework;

    // Getter和Setter方法
    
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<TestMethod> getTestMethods() {
        return testMethods;
    }

    public void setTestMethods(List<TestMethod> testMethods) {
        this.testMethods = testMethods;
    }

    public String getCorrespondingBusinessClass() {
        return correspondingBusinessClass;
    }

    public void setCorrespondingBusinessClass(String correspondingBusinessClass) {
        this.correspondingBusinessClass = correspondingBusinessClass;
    }

    public TestFramework getTestFramework() {
        return testFramework;
    }

    public void setTestFramework(TestFramework testFramework) {
        this.testFramework = testFramework;
    }
}
