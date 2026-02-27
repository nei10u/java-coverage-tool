package com.coveragetool.model;

/**
 * 测试粒度等级枚举 - 评估测试方法的测试质量
 * 
 * 测试粒度评估的是测试方法的全面性和深度。
 * 一个好的测试方法不仅应该覆盖正常流程，还应该测试边界值、异常情况等。
 * 这个枚举定义了四个粒度等级，帮助开发者了解测试质量。
 */
public enum GranularityLevel {
    
    /**
     * 优秀 - 测试覆盖全面，质量高
     * 
     * 评分标准（>= 80分）：
     * - 测试方法命名规范，能清晰表达测试意图
     * - 包含多个断言（>=3个），验证不同场景
     * - 覆盖边界值测试
     * - 覆盖异常情况测试
     * - 合理使用Mock对象隔离依赖
     * 
     * 这类测试能够有效保障代码质量
     */
    EXCELLENT("优秀", 80, "测试覆盖全面，包含边界值和异常测试"),
    
    /**
     * 良好 - 测试覆盖较全面，质量较好
     * 
     * 评分标准（>= 60分）：
     * - 测试方法命名较规范
     * - 包含一定的断言（>=1个）
     * - 覆盖了主要的业务场景
     * - 可能缺少部分边界值或异常测试
     * 
     * 这类测试能够覆盖大部分场景，但仍有改进空间
     */
    GOOD("良好", 60, "测试覆盖较全面，质量较好"),
    
    /**
     * 可接受 - 测试覆盖基本场景，质量一般
     * 
     * 评分标准（>= 40分）：
     * - 测试方法能覆盖基本功能
     * - 至少有一个断言
     * - 主要测试正常流程
     * - 缺少边界值和异常测试
     * 
     * 这类测试能提供基本的覆盖，但风险较高
     */
    ACCEPTABLE("可接受", 40, "测试覆盖基本场景，质量一般"),
    
    /**
     * 较差 - 测试覆盖不足，质量较差
     * 
     * 评分标准（< 40分）：
     * - 测试方法命名不规范
     * - 缺少有效的断言
     * - 只做了简单的方法调用
     * - 没有验证实际结果
     * 
     * 这类测试几乎不能提供有效的覆盖保障
     */
    POOR("较差", 0, "测试覆盖不足，质量较差");

    /**
     * 等级的显示名称
     */
    private final String displayName;
    
    /**
     * 等级的最低分数阈值
     * 用于判断测试粒度属于哪个等级
     */
    private final int minScore;
    
    /**
     * 等级的详细描述
     */
    private final String description;

    /**
     * 枚举构造函数
     * 
     * @param displayName 显示名称
     * @param minScore 最低分数阈值
     * @param description 详细描述
     */
    GranularityLevel(String displayName, int minScore, String description) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinScore() {
        return minScore;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据评分获取对应的粒度等级
     * 
     * @param score 测试粒度评分（0-100）
     * @return 对应的粒度等级
     */
    public static GranularityLevel fromScore(int score) {
        // 从高到低判断，确保返回最高匹配的等级
        if (score >= EXCELLENT.minScore) {
            return EXCELLENT;
        } else if (score >= GOOD.minScore) {
            return GOOD;
        } else if (score >= ACCEPTABLE.minScore) {
            return ACCEPTABLE;
        } else {
            return POOR;
        }
    }
}
