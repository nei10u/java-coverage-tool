package com.coveragetool;

import com.coveragetool.api.controller.AnalysisController;
import com.coveragetool.api.controller.ProjectController;
import com.coveragetool.api.controller.ReportController;
import com.coveragetool.api.service.AnalysisService;

import static spark.Spark.*;

/**
 * Java单元测试覆盖检测工具 - 主应用入口
 * 
 * 这是整个工具的启动类，负责初始化HTTP服务器和注册API路由。
 * 使用Spark Java框架提供轻量级的RESTful API服务。
 * 
 * 工作流程：
 * 1. 启动嵌入式HTTP服务器
 * 2. 配置跨域资源共享（CORS）
 * 3. 注册API路由
 * 4. 等待前端Electron应用的请求
 */
public class CoverageToolApplication {
    
    /**
     * 服务器端口号
     * 使用动态端口避免冲突，实际端口会被写入临时文件供前端读取
     */
    private static final int PORT = 4567;
    
    /**
     * 主方法 - 应用入口
     * 
     * @param args 命令行参数（暂未使用）
     */
    public static void main(String[] args) {
        // 设置服务器端口
        port(PORT);
        
        // 配置CORS（跨域资源共享）
        // 允许Electron前端从不同端口访问后端API
        configureCORS();
        
        // 创建服务实例
        AnalysisService analysisService = new AnalysisService();
        
        // 创建控制器实例
        ProjectController projectController = new ProjectController(analysisService);
        AnalysisController analysisController = new AnalysisController(analysisService);
        ReportController reportController = new ReportController(analysisService);
        
        // 根路径处理 - 返回服务状态
        get("/", (req, res) -> {
            res.type("application/json");
            return "{\"status\":\"running\",\"service\":\"Java Coverage Tool Backend\",\"version\":\"1.0.0\"}";
        });
        
        // 注册API路由
        // 项目相关API
        post("/api/project/scan", projectController::scanProject);
        
        // 分析相关API
        post("/api/analysis/start", analysisController::startAnalysis);
        get("/api/analysis/result/:id", analysisController::getAnalysisResult);
        get("/api/analysis/progress/:id", analysisController::getAnalysisProgress);
        get("/api/file/content", analysisController::readFileContent);
        get("/api/commit/diff", analysisController::getCommitDiff);
        
        // 报告相关API
        get("/api/report/export/:id", reportController::exportReport);
        post("/api/report/save/:id", reportController::saveReport);
        get("/api/report/history", reportController::getReportHistory);
        delete("/api/report/:id", reportController::deleteReport);
        get("/api/report/config/path", reportController::getDefaultSavePath);
        post("/api/report/config/path", reportController::setDefaultSavePath);
        
        // 异常处理
        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.body("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        });
        
        // 打印启动信息
        System.out.println("====================================");
        System.out.println("Java Coverage Tool Backend Started");
        System.out.println("Server running on port: " + PORT);
        System.out.println("API Base URL: http://localhost:" + PORT);
        System.out.println("====================================");
        
        // 将端口号写入临时文件，供Electron前端读取
        writePortToFile(PORT);
    }
    
    /**
     * 配置CORS（跨域资源共享）
     * 
     * 由于Electron前端和Java后端运行在不同的进程/端口，
     * 需要配置CORS以允许前端访问后端API。
     */
    private static void configureCORS() {
        // 在所有响应中添加CORS头
        after((req, res) -> {
            // 允许所有来源访问（开发环境）
            res.header("Access-Control-Allow-Origin", "*");
            // 允许的HTTP方法
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            // 允许的请求头
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            // 预检请求的缓存时间（秒）
            res.header("Access-Control-Max-Age", "86400");
        });
        
        // 处理OPTIONS预检请求
        options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            
            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            
            return "OK";
        });
    }
    
    /**
     * 将端口号写入临时文件
     * 
     * 这样Electron前端可以读取该文件获取后端服务的端口号。
     * 
     * @param port 端口号
     */
    private static void writePortToFile(int port) {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            java.io.File portFile = new java.io.File(tempDir, "java-coverage-tool-port.txt");
            java.io.FileWriter writer = new java.io.FileWriter(portFile);
            writer.write(String.valueOf(port));
            writer.close();
            System.out.println("Port number written to: " + portFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to write port file: " + e.getMessage());
        }
    }
}
