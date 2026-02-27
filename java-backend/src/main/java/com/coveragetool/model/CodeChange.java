package com.coveragetool.model;

import java.util.List;

/**
 * 代码变更模型 - 表示Git提交中的一个文件变更
 * 
 * 每次Git提交可能包含多个文件的变更，这个类记录单个文件的变更详情，
 * 包括变更类型、变更的行号等，用于关联测试覆盖分析。
 */
public class CodeChange {
    
    /**
     * 变更的文件路径
     * 相对于项目根目录的路径
     */
    private String filePath;
    
    /**
     * 变更类型
     * ADD（新增）、MODIFY（修改）、DELETE（删除）、RENAME（重命名）
     */
    private ChangeType changeType;
    
    /**
     * 新增的行号列表
     * 本次提交中新增的代码行号
     */
    private List<Integer> addedLines;
    
    /**
     * 修改的行号列表
     * 本次提交中修改的代码行号
     */
    private List<Integer> modifiedLines;
    
    /**
     * 删除的行号列表
     * 本次提交中删除的代码行号
     */
    private List<Integer> deletedLines;
    
    /**
     * 新增的代码行数
     */
    private int linesAdded;
    
    /**
     * 删除的代码行数
     */
    private int linesDeleted;
    
    /**
     * 该文件变更的测试覆盖率
     * 如果是业务文件，记录其测试覆盖情况
     */
    private double coverageRate;

    /**
     * 变更类型枚举
     */
    public enum ChangeType {
        /**
         * 新增文件 - 之前不存在的新文件
         */
        ADD("新增"),
        
        /**
         * 修改文件 - 已存在文件的修改
         */
        MODIFY("修改"),
        
        /**
         * 删除文件 - 文件被删除
         */
        DELETE("删除"),
        
        /**
         * 重命名 - 文件被重命名或移动
         */
        RENAME("重命名");

        private final String displayName;

        ChangeType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Getter和Setter方法
    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public List<Integer> getAddedLines() {
        return addedLines;
    }

    public void setAddedLines(List<Integer> addedLines) {
        this.addedLines = addedLines;
        this.linesAdded = addedLines != null ? addedLines.size() : 0;
    }

    public List<Integer> getModifiedLines() {
        return modifiedLines;
    }

    public void setModifiedLines(List<Integer> modifiedLines) {
        this.modifiedLines = modifiedLines;
    }

    public List<Integer> getDeletedLines() {
        return deletedLines;
    }

    public void setDeletedLines(List<Integer> deletedLines) {
        this.deletedLines = deletedLines;
        this.linesDeleted = deletedLines != null ? deletedLines.size() : 0;
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

    public double getCoverageRate() {
        return coverageRate;
    }

    public void setCoverageRate(double coverageRate) {
        this.coverageRate = coverageRate;
    }
}
