package com.coveragetool.model;

import java.util.List;

/**
 * 业务类模型 - 表示被测试覆盖分析的业务类
 * 
 * 业务类是指包含业务逻辑的Java类，如Service、Controller、Repository等。
 * 这个类存储了业务类的完整信息，包括类名、路径、包含的方法等，
 * 是覆盖率分析的主要目标对象。
 */
public class BusinessClass {
    
    /**
     * 类的完整限定名
     * 包含包路径的完整类名，如com.example.service.UserService
     * 这个名称在Java中是唯一的，用于精确识别类
     */
    private String fullyQualifiedName;
    
    /**
     * 类的简单名称
     * 不包含包路径的类名，如UserService
     * 用于在报告中简洁地显示类名
     */
    private String className;
    
    /**
     * 源文件的绝对路径
     * 类对应的.java文件在文件系统中的完整路径
     * 用于代码分析和Git历史查询
     */
    private String filePath;
    
    /**
     * 包名
     * 类所属的Java包，如com.example.service
     * 包名有助于理解类的组织结构和职责
     */
    private String packageName;
    
    /**
     * 类的类型
     * 根据命名约定识别的类类型，如SERVICE、CONTROLLER、REPOSITORY等
     * 不同类型的类可能有不同的测试覆盖要求
     */
    private ClassType classType;
    
    /**
     * 类中定义的所有公共方法
     * 这些方法是需要被单元测试覆盖的目标
     * 列表中的每个方法都会被检查是否有对应的测试
     */
    private List<Method> methods;
    
    /**
     * 该类对应的测试类名称
     * 根据测试命名约定推测的测试类名，如UserService对应UserServiceTest
     * 用于快速定位相关的测试类
     */
    private String correspondingTestClass;
    
    /**
     * 该类的测试覆盖率百分比
     * 计算公式：(已覆盖的方法数 / 总方法数) * 100
     * 0表示完全没有测试，100表示所有方法都有测试覆盖
     */
    private double coverageRate;

    /**
     * 默认构造函数
     */
    public BusinessClass() {
    }

    /**
     * 带基本参数的构造函数
     * 
     * @param fullyQualifiedName 类的完整限定名
     * @param className 类的简单名称
     * @param filePath 源文件路径
     */
    public BusinessClass(String fullyQualifiedName, String className, String filePath) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.className = className;
        this.filePath = filePath;
    }

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

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public String getCorrespondingTestClass() {
        return correspondingTestClass;
    }

    public void setCorrespondingTestClass(String correspondingTestClass) {
        this.correspondingTestClass = correspondingTestClass;
    }

    public double getCoverageRate() {
        return coverageRate;
    }

    public void setCoverageRate(double coverageRate) {
        this.coverageRate = coverageRate;
    }
}
