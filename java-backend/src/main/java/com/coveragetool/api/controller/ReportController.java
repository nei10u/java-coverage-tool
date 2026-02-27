package com.coveragetool.api.controller;

import com.coveragetool.api.service.AnalysisService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

/**
 * 报告控制器 - 处理报告导出和管理相关的API请求
 */
public class ReportController {
    
    private AnalysisService analysisService;
    private Gson gson;
    
    public ReportController(AnalysisService analysisService) {
        this.analysisService = analysisService;
        this.gson = new Gson();
    }
    
    /**
     * 导出报告（直接下载）
     */
    public Object exportReport(Request req, Response res) {
        try {
            String analysisId = req.params(":id");
            String format = req.queryParams("format");
            
            if (format == null || format.isEmpty()) {
                format = "html";
            }
            
            String report = analysisService.exportReport(analysisId);
            
            if ("html".equals(format)) {
                res.type("text/html");
                res.header("Content-Disposition", 
                    "attachment; filename=coverage-report.html");
            } else {
                res.type("application/json");
            }
            
            return report;
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("导出报告失败: " + e.getMessage()));
        }
    }
    
    /**
     * 保存报告到本地
     */
    public Object saveReport(Request req, Response res) {
        try {
            String analysisId = req.params(":id");
            
            // 解析请求体获取保存路径
            SaveReportRequest request = gson.fromJson(req.body(), SaveReportRequest.class);
            String savePath = request != null ? request.savePath : null;
            
            // 保存报告
            String filePath = analysisService.saveReport(analysisId, savePath);
            
            res.type("application/json");
            return gson.toJson(new SaveReportResponse(true, filePath, "报告已保存"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("保存报告失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取报告历史列表
     */
    public Object getReportHistory(Request req, Response res) {
        try {
            res.type("application/json");
            return gson.toJson(analysisService.getReportHistory());
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("获取报告历史失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除报告
     */
    public Object deleteReport(Request req, Response res) {
        try {
            String reportId = req.params(":id");
            
            boolean success = analysisService.deleteReport(reportId);
            
            res.type("application/json");
            if (success) {
                return gson.toJson(new SuccessResponse("报告已删除"));
            } else {
                res.status(404);
                return gson.toJson(new ErrorResponse("报告不存在"));
            }
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("删除报告失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取默认报告保存路径
     */
    public Object getDefaultSavePath(Request req, Response res) {
        try {
            res.type("application/json");
            return gson.toJson(new DefaultPathResponse(analysisService.getDefaultReportPath()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("获取默认路径失败: " + e.getMessage()));
        }
    }
    
    /**
     * 设置默认报告保存路径
     */
    public Object setDefaultSavePath(Request req, Response res) {
        try {
            DefaultPathRequest request = gson.fromJson(req.body(), DefaultPathRequest.class);
            
            if (request == null || request.path == null || request.path.isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("路径不能为空"));
            }
            
            analysisService.setDefaultReportPath(request.path);
            
            res.type("application/json");
            return gson.toJson(new SuccessResponse("默认路径已更新"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("设置默认路径失败: " + e.getMessage()));
        }
    }
    
    // 请求/响应模型类
    
    private static class ErrorResponse {
        String error;
        ErrorResponse(String error) { this.error = error; }
    }
    
    private static class SuccessResponse {
        String message;
        SuccessResponse(String message) { this.message = message; }
    }
    
    private static class SaveReportRequest {
        String savePath;
    }
    
    private static class SaveReportResponse {
        boolean success;
        String filePath;
        String message;
        SaveReportResponse(boolean success, String filePath, String message) {
            this.success = success;
            this.filePath = filePath;
            this.message = message;
        }
    }
    
    private static class DefaultPathResponse {
        String path;
        DefaultPathResponse(String path) { this.path = path; }
    }
    
    private static class DefaultPathRequest {
        String path;
    }
}
