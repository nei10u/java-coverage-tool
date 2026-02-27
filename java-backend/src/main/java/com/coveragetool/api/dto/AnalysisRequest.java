package com.coveragetool.api.dto;

import java.util.Date;
import java.util.List;

/**
 * 分析请求DTO - 前端发送的分析请求参数
 */
public class AnalysisRequest {
    
    /**
     * 项目根路径
     */
    private String projectPath;
    
    /**
     * 选择的源码目录列表
     */
    private List<String> sourceDirectories;
    
    /**
     * 选择的测试目录列表
     */
    private List<String> testDirectories;
    
    /**
     * Git分析选项
     */
    private GitOptions gitOptions;
    
    // Getter和Setter方法
    
    public String getProjectPath() {
        return projectPath;
    }
    
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
    
    public List<String> getSourceDirectories() {
        return sourceDirectories;
    }
    
    public void setSourceDirectories(List<String> sourceDirectories) {
        this.sourceDirectories = sourceDirectories;
    }
    
    public List<String> getTestDirectories() {
        return testDirectories;
    }
    
    public void setTestDirectories(List<String> testDirectories) {
        this.testDirectories = testDirectories;
    }
    
    public GitOptions getGitOptions() {
        return gitOptions;
    }
    
    public void setGitOptions(GitOptions gitOptions) {
        this.gitOptions = gitOptions;
    }
    
    /**
     * Git分析选项内部类
     */
    public static class GitOptions {
        private Date since;
        private Date until;
        private boolean includeMergeCommits;
        
        public Date getSince() {
            return since;
        }
        
        public void setSince(Date since) {
            this.since = since;
        }
        
        public Date getUntil() {
            return until;
        }
        
        public void setUntil(Date until) {
            this.until = until;
        }
        
        public boolean isIncludeMergeCommits() {
            return includeMergeCommits;
        }
        
        public void setIncludeMergeCommits(boolean includeMergeCommits) {
            this.includeMergeCommits = includeMergeCommits;
        }
    }
}
