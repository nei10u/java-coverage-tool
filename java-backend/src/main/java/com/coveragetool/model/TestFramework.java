package com.coveragetool.model;

/**
 * 测试框架枚举 - 支持的单元测试框架类型
 * 
 * Java生态中有多种测试框架，最常见的有JUnit和TestNG。
 * 不同的框架有不同的注解和特性，系统需要识别框架类型以正确解析测试代码。
 */
public enum TestFramework {
    
    /**
     * JUnit 4 - 经典的单元测试框架
     * 
     * 特征：
     * - 使用@Test注解标记测试方法
     * - 使用org.junit.Assert类进行断言
     * - 使用@RunWith注解自定义运行器
     * - 使用@Before、@After等生命周期注解
     * 
     * JUnit 4是最广泛使用的测试框架之一
     */
    JUNIT4("JUnit 4", "org.junit.Test"),
    
    /**
     * JUnit 5 - 新一代测试框架
     * 
     * 特征：
     * - 使用@Test注解（来自org.junit.jupiter.api包）
     * - 使用Assertions类进行断言
     * - 支持参数化测试、嵌套测试等高级特性
     * - 使用@BeforeEach、@AfterEach等生命周期注解
     * 
     * JUnit 5提供了更强大和灵活的测试能力
     */
    JUNIT5("JUnit 5", "org.junit.jupiter.api.Test"),
    
    /**
     * TestNG - 功能强大的测试框架
     * 
     * 特征：
     * - 使用@Test注解（来自org.testng包）
     * - 使用Assert类进行断言
     * - 支持依赖测试、并行测试等高级功能
     * - 使用@BeforeMethod、@AfterMethod等注解
     * 
     * TestNG适合复杂的测试场景，特别是集成测试
     */
    TESTNG("TestNG", "org.testng.annotations.Test"),
    
    /**
     * 未知框架
     * 当无法识别测试框架类型时使用
     */
    UNKNOWN("Unknown", "");

    /**
     * 框架的显示名称
     */
    private final String displayName;
    
    /**
     * 框架的核心注解类
     * 用于识别测试框架类型
     */
    private final String testAnnotation;

    /**
     * 枚举构造函数
     */
    TestFramework(String displayName, String testAnnotation) {
        this.displayName = displayName;
        this.testAnnotation = testAnnotation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTestAnnotation() {
        return testAnnotation;
    }
}
