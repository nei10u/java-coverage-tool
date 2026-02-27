package com.coveragetool.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 目录树节点模型 - 用于构建项目的目录树结构
 * 
 * 这个类表示目录树中的一个节点，可以是文件夹或文件。
 * 用于在前端展示可展开的目录树，让用户选择要分析的目录。
 */
public class DirectoryNode {
    
    /**
     * 节点标题
     * 显示在树形控件中的文本，通常是目录或文件名
     */
    private String title;
    
    /**
     * 节点唯一标识
     * 使用相对路径作为key，确保唯一性
     */
    private String key;
    
    /**
     * 节点值
     * 用于提交分析时使用的路径值
     */
    private String value;
    
    /**
     * 是否为叶子节点
     * true表示文件或空目录，false表示有子目录的目录
     */
    private Boolean isLeaf;
    
    /**
     * 子节点列表
     * 仅当isLeaf为false时有值
     */
    private List<DirectoryNode> children;
    
    /**
     * 节点类型
     * source: 源码目录
     * test: 测试目录
     * folder: 普通文件夹
     * file: 文件
     */
    private String type;
    
    /**
     * 是否可选中
     * 文件节点不可选中，只有目录可以选中
     */
    private Boolean selectable;
    
    /**
     * 是否可选（checkable）
     * 用于控制是否显示复选框
     */
    private Boolean checkable;

    /**
     * 无参构造函数
     */
    public DirectoryNode() {
        this.children = new ArrayList<>();
        this.isLeaf = false;
        this.selectable = true;
        this.checkable = true;
    }

    /**
     * 带参构造函数
     * 
     * @param title 节点标题
     * @param key 节点key
     * @param value 节点值
     * @param isLeaf 是否叶子节点
     */
    public DirectoryNode(String title, String key, String value, boolean isLeaf) {
        this.title = title;
        this.key = key;
        this.value = value;
        this.isLeaf = isLeaf;
        this.children = new ArrayList<>();
        this.selectable = true;
        this.checkable = true;
    }

    // Getter和Setter方法

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public List<DirectoryNode> getChildren() {
        return children;
    }

    public void setChildren(List<DirectoryNode> children) {
        this.children = children;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSelectable() {
        return selectable;
    }

    public void setSelectable(Boolean selectable) {
        this.selectable = selectable;
    }

    public Boolean getCheckable() {
        return checkable;
    }

    public void setCheckable(Boolean checkable) {
        this.checkable = checkable;
    }
}
