package com.coveragetool.model;

/**
 * 报告状态枚举 - 表示报告的当前状态
 */
public enum ReportStatus {
    /**
     * 已生成 - 报告已生成但未保存到文件
     */
    GENERATED("已生成"),
    
    /**
     * 已保存 - 报告已保存到本地文件
     */
    SAVED("已保存"),
    
    /**
     * 已删除 - 报告已被删除
     */
    DELETED("已删除");
    
    private final String displayName;
    
    ReportStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
