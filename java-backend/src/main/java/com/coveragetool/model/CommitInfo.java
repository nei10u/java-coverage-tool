package com.coveragetool.model;

import java.util.Date;
import java.util.List;

/**
 * 提交信息模型 - 表示Git的一个提交记录
 * 
 * 每个Git提交记录包含提交者、时间、消息等信息，以及该提交中的代码变更。
 * 通过分析提交记录，可以关联代码变更与测试覆盖情况。
 */
public class CommitInfo {
    
    /**
     * 提交的哈希值（完整）
     * Git中的唯一标识符，如：a1b2c3d4e5f6...
     */
    private String commitHash;
    
    /**
     * 提交的短哈希值
     * 通常取前7位，如：a1b2c3d
     * 用于在UI中简洁显示
     */
    private String shortHash;
    
    /**
     * 提交者姓名
     * 执行git commit的开发者
     */
    private String author;
    
    /**
     * 提交者邮箱
     * 用于唯一标识开发者
     */
    private String authorEmail;
    
    /**
     * 提交时间
     */
    private Date commitDate;
    
    /**
     * 提交消息
     * 描述本次提交的内容
     */
    private String message;
    
    /**
     * 本次提交的代码变更列表
     * 包含所有修改、新增、删除的文件
     */
    private List<CodeChange> changes;
    
    /**
     * 新增代码的测试覆盖率
     * 本次提交新增代码的测试覆盖百分比
     */
    private double addedCodeCoverage;
    
    /**
     * 修改代码的测试覆盖率
     * 本次提交修改代码的测试覆盖百分比
     */
    private double modifiedCodeCoverage;
    
    /**
     * 新增的代码行数
     */
    private int linesAdded;
    
    /**
     * 删除的代码行数
     */
    private int linesDeleted;

    // Getter和Setter方法
    
    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
        // 自动生成短哈希
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CodeChange> getChanges() {
        return changes;
    }

    public void setChanges(List<CodeChange> changes) {
        this.changes = changes;
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
}
