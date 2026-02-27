package com.coveragetool.model;

import java.util.Date;
import java.util.List;

/**
 * 提交统计模型 - 存储单个Git提交的测试覆盖统计
 * 
 * 用于提交级报告，展示每个提交的代码变更覆盖情况。
 */
public class CommitStatistics {
    
    /**
     * 提交哈希值
     */
    private String commitHash;
    
    /**
     * 短哈希值
     */
    private String shortHash;
    
    /**
     * 提交者姓名
     */
    private String authorName;
    
    /**
     * 提交者邮箱
     */
    private String authorEmail;
    
    /**
     * 提交时间
     */
    private Date commitDate;
    
    /**
     * 提交消息
     */
    private String commitMessage;
    
    /**
     * 新增代码行数
     */
    private int linesAdded;
    
    /**
     * 删除代码行数
     */
    private int linesDeleted;
    
    /**
     * 新增方法数
     */
    private int methodsAdded;
    
    /**
     * 修改方法数
     */
    private int methodsModified;
    
    /**
     * 新增方法中已覆盖的数量
     */
    private int addedMethodsCovered;
    
    /**
     * 修改方法中已覆盖的数量
     */
    private int modifiedMethodsCovered;
    
    /**
     * 新增代码的覆盖率
     */
    private double addedCodeCoverage;
    
    /**
     * 修改代码的覆盖率
     */
    private double modifiedCodeCoverage;
    
    /**
     * 本次提交涉及的文件列表
     */
    private List<String> affectedFiles;
    
    /**
     * 本次提交新增/修改的方法列表
     */
    private List<MethodCoverage> affectedMethods;

    // Getter和Setter方法
    
    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
        if (commitHash != null && commitHash.length() >= 7) {
            this.shortHash = commitHash.substring(0, 7);
        }
    }

    public String getShortHash() {
        return shortHash;
    }

    public void setShortHash(String shortHash) {
        this.shortHash = shortHash;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public int getLinesAdded() {
        return linesAdded;
    }

    public void setLinesAdded(int linesAdded) {
        this.linesAdded = linesAdded;
    }

    public int getLinesDeleted() {
        return linesDeleted;
    }

    public void setLinesDeleted(int linesDeleted) {
        this.linesDeleted = linesDeleted;
    }

    public int getMethodsAdded() {
        return methodsAdded;
    }

    public void setMethodsAdded(int methodsAdded) {
        this.methodsAdded = methodsAdded;
    }

    public int getMethodsModified() {
        return methodsModified;
    }

    public void setMethodsModified(int methodsModified) {
        this.methodsModified = methodsModified;
    }

    public int getAddedMethodsCovered() {
        return addedMethodsCovered;
    }

    public void setAddedMethodsCovered(int addedMethodsCovered) {
        this.addedMethodsCovered = addedMethodsCovered;
    }

    public int getModifiedMethodsCovered() {
        return modifiedMethodsCovered;
    }

    public void setModifiedMethodsCovered(int modifiedMethodsCovered) {
        this.modifiedMethodsCovered = modifiedMethodsCovered;
    }

    public double getAddedCodeCoverage() {
        return addedCodeCoverage;
    }

    public void setAddedCodeCoverage(double addedCodeCoverage) {
        this.addedCodeCoverage = addedCodeCoverage;
    }

    public double getModifiedCodeCoverage() {
        return modifiedCodeCoverage;
    }

    public void setModifiedCodeCoverage(double modifiedCodeCoverage) {
        this.modifiedCodeCoverage = modifiedCodeCoverage;
    }

    public List<String> getAffectedFiles() {
        return affectedFiles;
    }

    public void setAffectedFiles(List<String> affectedFiles) {
        this.affectedFiles = affectedFiles;
    }

    public List<MethodCoverage> getAffectedMethods() {
        return affectedMethods;
    }

    public void setAffectedMethods(List<MethodCoverage> affectedMethods) {
        this.affectedMethods = affectedMethods;
    }
}
