package com.coveragetool.model;

import java.util.List;

/**
 * 文件内容模型
 * 
 * 用于存储和传输文件的完整内容，包括行号信息。
 * 这个类主要用于在前端显示文件的具体代码内容。
 */
public class FileContent {
    
    /**
     * 文件的完整路径
     */
    private String filePath;
    
    /**
     * 文件名（不包含路径）
     */
    private String fileName;
    
    /**
     * 文件总行数
     */
    private int totalLines;
    
    /**
     * 带行号的代码行列表
     */
    private List<Line> lines;
    
    /**
     * 原始文件内容（不带行号的完整字符串）
     */
    private String rawContent;
    
    /**
     * 代码行内部类
     * 
     * 用于表示单行代码及其行号
     */
    public static class Line {
        /**
         * 行号（从1开始）
         */
        private int lineNumber;
        
        /**
         * 代码内容
         */
        private String content;
        
        /**
         * 构造函数
         * 
         * @param lineNumber 行号
         * @param content 代码内容
         */
        public Line(int lineNumber, String content) {
            this.lineNumber = lineNumber;
            this.content = content;
        }
        
        // Getter和Setter方法
        public int getLineNumber() {
            return lineNumber;
        }
        
        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    // Getter和Setter方法
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public int getTotalLines() {
        return totalLines;
    }
    
    public void setTotalLines(int totalLines) {
        this.totalLines = totalLines;
    }
    
    public List<Line> getLines() {
        return lines;
    }
    
    public void setLines(List<Line> lines) {
        this.lines = lines;
    }
    
    public String getRawContent() {
        return rawContent;
    }
    
    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }
}
