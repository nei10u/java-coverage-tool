package com.coveragetool.api.controller;

import com.coveragetool.api.dto.AnalysisRequest;
import com.coveragetool.api.service.AnalysisService;
import com.coveragetool.model.AnalysisResult;
import com.coveragetool.model.FileContent;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

/**
 * 分析控制器 - 处理分析相关的API请求
 */
public class AnalysisController {
    
    private AnalysisService analysisService;
    private Gson gson;
    
    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
        this.gson = new Gson();
    }
    
    /**
     * 开始分析
     */
    public Object startAnalysis(Request req, Response res) {
        try {
            AnalysisRequest request = gson.fromJson(req.body(), AnalysisRequest.class);
            String analysisId = analysisService.startAnalysis(request);
            res.type("application/json");
            return gson.toJson(new AnalysisResponse(analysisId, "started"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("启动分析失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取分析结果
     */
    public Object getAnalysisResult(Request req, Response res) {
        try {
            String analysisId = req.params(":id");
            AnalysisResult result = analysisService.getAnalysisResult(analysisId);
            
            if (result == null) {
                res.status(404);
                return gson.toJson(new ErrorResponse("分析结果不存在"));
            }
            
            res.type("application/json");
            return gson.toJson(result);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("获取结果失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取分析进度
     */
    public Object getAnalysisProgress(Request req, Response res) {
        try {
            String analysisId = req.params(":id");
            AnalysisService.AnalysisProgress progress = 
                analysisService.getAnalysisProgress(analysisId);
            
            if (progress == null) {
                res.status(404);
                return gson.toJson(new ErrorResponse("进度信息不存在"));
            }
            
            res.type("application/json");
            return gson.toJson(progress);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("获取进度失败: " + e.getMessage()));
        }
    }
    
    /**
     * 读取文件内容
     * 
     * 用于在前端显示文件的具体代码内容
     */
    public Object readFileContent(Request req, Response res) {
        try {
            // 从查询参数中获取文件路径
            String filePath = req.queryParams("path");
            
            if (filePath == null || filePath.isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("文件路径不能为空"));
            }
            
            // 调用服务层读取文件
            FileContent fileContent = analysisService.readFileContent(filePath);
            
            res.type("application/json");
            return gson.toJson(fileContent);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("读取文件失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取提交差异内容
     * 
     * 用于在前端显示提交的代码变更详情
     */
    public Object getCommitDiff(Request req, Response res) {
        try {
            // 从查询参数中获取提交哈希
            String commitHash = req.queryParams("hash");
            
            if (commitHash == null || commitHash.isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("提交哈希不能为空"));
            }
            
            // 调用服务层获取差异
            String diff = analysisService.getCommitDiff(commitHash);
            
            res.type("text/plain");
            return diff;
        } catch (Exception e) {
            res.status(500);
            return "获取提交差异失败: " + e.getMessage();
        }
    }
    
    private static class AnalysisResponse {
        String analysisId;
        String status;
        AnalysisResponse(String analysisId, String status) {
            this.analysisId = analysisId;
            this.status = status;
        }
    }
    
    private static class ErrorResponse {
        String error;
        ErrorResponse(String error) { this.error = error; }
    }
}
