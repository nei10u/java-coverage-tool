package com.coveragetool.model;

/**
 * 类类型枚举 - 根据Java类命名约定识别的类类型
 * 
 * 在Java项目中，不同的类承担不同的职责：
 * - Service类：包含业务逻辑
 * - Controller类：处理HTTP请求
 * - Repository类：数据访问
 * - Component类：通用组件
 * 
 * 识别类类型有助于：
 * 1. 应用不同的测试覆盖标准
 * 2. 在报告中分类展示
 * 3. 评估测试粒度是否符合该类型类的要求
 */
public enum ClassType {
    
    /**
     * Service层 - 业务逻辑层
     * 命名特征：类名以Service结尾
     * 职责：实现核心业务逻辑，协调多个组件完成业务功能
     * 测试要求：需要完整的单元测试，覆盖各种业务场景和边界条件
     */
    SERVICE("Service", "业务逻辑层，实现核心业务功能"),
    
    /**
     * Controller层 - 控制器层
     * 命名特征：类名以Controller结尾
     * 职责：处理HTTP请求，调用Service层，返回响应
     * 测试要求：需要测试请求映射、参数验证、异常处理等
     */
    CONTROLLER("Controller", "控制器层，处理HTTP请求和响应"),
    
    /**
     * Repository/DAO层 - 数据访问层
     * 命名特征：类名以Repository或Dao结尾
     * 职责：封装数据库操作，提供数据访问接口
     * 测试要求：可以使用集成测试或Mock数据库进行测试
     */
    REPOSITORY("Repository", "数据访问层，封装数据库操作"),
    
    /**
     * Component - 通用组件
     * 命名特征：类名以Component结尾，或被@Component注解标记
     * 职责：提供可重用的功能组件
     * 测试要求：根据组件功能确定测试策略
     */
    COMPONENT("Component", "通用组件，提供可重用功能"),
    
    /**
     * Util/Helper - 工具类
     * 命名特征：类名以Util、Utils、Helper结尾
     * 职责：提供静态工具方法，通常无状态
     * 测试要求：需要覆盖各种输入参数组合
     */
    UTIL("Util", "工具类，提供静态工具方法"),
    
    /**
     * Model/Entity - 模型或实体类
     * 命名特征：通常在model、entity、dto包下
     * 职责：表示数据模型，通常是简单的POJO
     * 测试要求：一般不需要单元测试，除非包含复杂逻辑
     */
    MODEL("Model", "模型类，表示数据结构"),
    
    /**
     * 未识别的类类型
     * 当类不符合任何已知命名约定时使用
     * 仍会进行测试覆盖分析，但可能需要手动确认
     */
    UNKNOWN("Unknown", "未识别的类类型");

    /**
     * 类型的显示名称
     * 用于在UI和报告中显示
     */
    private final String displayName;
    
    /**
     * 类型的详细描述
     * 说明该类型类的职责和测试要求
     */
    private final String description;

    /**
     * 枚举构造函数
     * 
     * @param displayName 类型的显示名称
     * @param description 类型的详细描述
     */
    ClassType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 获取显示名称
     * @return 类型的显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取详细描述
     * @return 类型的详细描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据类名识别类类型
     * 
     * 通过分析类名的命名约定来判断类的类型。
     * 这是一种启发式方法，基于Java社区通用的命名规范。
     * 
     * @param className 类的简单名称（不包含包名）
     * @return 识别出的类类型，如果无法识别则返回UNKNOWN
     */
    public static ClassType fromClassName(String className) {
        // 转换为大写进行比较，避免大小写问题
        String upperName = className.toUpperCase();
        
        // 按照命名约定判断类类型
        // 优先级从高到低，避免误判（如UserServiceImpl应识别为SERVICE而非IMPL）
        if (upperName.contains("SERVICE")) {
            return SERVICE;
        } else if (upperName.contains("CONTROLLER")) {
            return CONTROLLER;
        } else if (upperName.contains("REPOSITORY") || upperName.contains("DAO")) {
            return REPOSITORY;
        } else if (upperName.contains("COMPONENT")) {
            return COMPONENT;
        } else if (upperName.contains("UTIL") || upperName.contains("HELPER")) {
            return UTIL;
        } else if (upperName.contains("ENTITY") || upperName.contains("MODEL") || 
                   upperName.contains("DTO") || upperName.contains("VO")) {
            return MODEL;
        }
        
        // 如果都不匹配，返回UNKNOWN类型
        return UNKNOWN;
    }
}
