package com.coveragetool.api.controller;

import com.coveragetool.api.dto.AnalysisRequest;
import com.coveragetool.api.service.AnalysisService;
import com.coveragetool.scanner.ProjectStructure;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

/**
 * 项目控制器 - 处理项目扫描相关的API请求
 */
public class ProjectController {
    
    private AnalysisService analysisService;
    private Gson gson;
    
    public ProjectController(AnalysisService analysisService) {
        this.analysisService = analysisService;
        this.gson = new Gson();
    }
    
    /**
     * 扫描项目
     * 
     * @param req HTTP请求
     * @param res HTTP响应
     * @return JSON响应
     */
    public Object scanProject(Request req, Response res) {
        try {
            // 解析请求体
            String body = req.body();
            ScanRequest scanRequest = gson.fromJson(body, ScanRequest.class);
            
            // 验证参数
            if (scanRequest.projectPath == null || scanRequest.projectPath.isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("项目路径不能为空"));
            }
            
            // 执行扫描
            ProjectStructure structure = analysisService.scanProject(scanRequest.projectPath);
            
            // 返回结果
            res.type("application/json");
            return gson.toJson(structure);
            
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("扫描失败: " + e.getMessage()));
        }
    }
    
    /**
     * 扫描请求内部类
     */
    private static class ScanRequest {
        String projectPath;
    }
    
    /**
     * 错误响应内部类
     */
    private static class ErrorResponse {
        String error;
        ErrorResponse(String error) { this.error = error; }
    }
}
