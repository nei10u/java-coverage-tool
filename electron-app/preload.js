/**
 * Electron预加载脚本
 *
 * 这个脚本在渲染进程加载前执行，用于安全地暴露Electron API给前端。
 * 通过contextBridge，我们可以选择性地暴露功能给渲染进程。
 */

const { contextBridge, ipcRenderer } = require("electron");

/**
 * 向渲染进程暴露API
 *
 * 使用contextBridge.exposeInMainWorld方法，将一组API安全地暴露给前端。
 * 前端可以通过window.electronAPI访问这些方法。
 */
contextBridge.exposeInMainWorld("electronAPI", {
  /**
   * 选择目录
   *
   * 打开目录选择对话框，返回用户选择的目录路径。
   * @returns {Promise<string|null>} 选中的目录路径，或null（如果取消）
   */
  selectDirectory: () => ipcRenderer.invoke("select-directory"),

  /**
   * 调用后端API
   *
   * 向Java后端发送HTTP请求。
   * @param {string} method - HTTP方法（GET或POST）
   * @param {string} endpoint - API端点（如/api/project/scan）
   * @param {object} data - 请求数据（POST请求时使用）
   * @returns {Promise<object>} API响应数据
   */
  apiCall: (method, endpoint, data) =>
    ipcRenderer.invoke("api-call", { method, endpoint, data }),

  /**
   * 扫描项目
   *
   * 扫描指定路径的Java项目，返回项目结构信息。
   * @param {string} projectPath - 项目根路径
   * @returns {Promise<object>} 项目结构信息
   */
  scanProject: async (projectPath) => {
    return await ipcRenderer.invoke("api-call", {
      method: "POST",
      endpoint: "/api/project/scan",
      data: { projectPath },
    });
  },

  /**
   * 开始分析
   *
   * 启动项目的测试覆盖分析。
   * @param {object} analysisRequest - 分析请求参数
   * @returns {Promise<object>} 包含analysisId的响应
   */
  startAnalysis: async (analysisRequest) => {
    return await ipcRenderer.invoke("api-call", {
      method: "POST",
      endpoint: "/api/analysis/start",
      data: analysisRequest,
    });
  },

  /**
   * 获取分析结果
   *
   * 根据分析ID获取完整的分析结果。
   * @param {string} analysisId - 分析任务ID
   * @returns {Promise<object>} 分析结果
   */
  getAnalysisResult: async (analysisId) => {
    return await ipcRenderer.invoke("api-call", {
      method: "GET",
      endpoint: `/api/analysis/result/${analysisId}`,
    });
  },

  /**
   * 获取分析进度
   *
   * 获取指定分析任务的当前进度。
   * @param {string} analysisId - 分析任务ID
   * @returns {Promise<object>} 进度信息（包含stage、progress、message等）
   */
  getAnalysisProgress: async (analysisId) => {
    return await ipcRenderer.invoke("api-call", {
      method: "GET",
      endpoint: `/api/analysis/progress/${analysisId}`,
    });
  },

  /**
   * 导出报告
   *
   * 导出指定分析任务的HTML报告。
   * @param {string} analysisId - 分析任务ID
   * @param {string} format - 报告格式（默认html）
   * @returns {Promise<string>} HTML报告内容
   */
  exportReport: async (analysisId, format = "html") => {
    return await ipcRenderer.invoke("api-call", {
      method: "GET",
      endpoint: `/api/report/export/${analysisId}?format=${format}`,
    });
  },

  /**
   * 保存报告到本地
   *
   * @param {string} analysisId - 分析任务ID
   * @param {string} savePath - 保存路径（可选）
   * @returns {Promise<object>} 保存结果
   */
  saveReport: async (analysisId, savePath = null) => {
    return await ipcRenderer.invoke("api-call", {
      method: "POST",
      endpoint: `/api/report/save/${analysisId}`,
      data: { savePath },
    });
  },

  /**
   * 获取报告历史列表
   *
   * @returns {Promise<Array>} 报告历史列表
   */
  getReportHistory: async () => {
    return await ipcRenderer.invoke("api-call", {
      method: "GET",
      endpoint: "/api/report/history",
    });
  },

  /**
   * 删除报告
   *
   * @param {string} reportId - 报告ID
   * @returns {Promise<object>} 删除结果
   */
  deleteReport: async (reportId) => {
    return await ipcRenderer.invoke("api-call", {
      method: "DELETE",
      endpoint: `/api/report/${reportId}`,
    });
  },

  /**
   * 获取默认报告保存路径
   *
   * @returns {Promise<string>} 默认保存路径
   */
  getDefaultSavePath: async () => {
    return await ipcRenderer.invoke("api-call", {
      method: "GET",
      endpoint: "/api/report/config/path",
    });
  },

  /**
   * 设置默认报告保存路径
   *
   * @param {string} path - 新的默认路径
   * @returns {Promise<object>} 设置结果
   */
  setDefaultSavePath: async (path) => {
    return await ipcRenderer.invoke("api-call", {
      method: "POST",
      endpoint: "/api/report/config/path",
      data: { path },
    });
  },

  /**
   * 读取文件内容
   *
   * 读取指定文件的代码内容，用于在前端显示。
   * @param {string} filePath - 文件路径
   * @returns {Promise<object>} 文件内容对象，包含行号、内容等信息
   */
  readFileContent: async (filePath) => {
    return await ipcRenderer.invoke("api-call", {
      method: "GET",
      endpoint: `/api/file/content?path=${encodeURIComponent(filePath)}`,
    });
  },

  /**
   * 获取提交差异内容
   *
   * 获取指定提交的代码变更详情（diff格式）。
   * @param {string} commitHash - 提交哈希值
   * @returns {Promise<string>} diff格式的代码变更内容
   */
  getCommitDiff: async (commitHash) => {
    return await ipcRenderer.invoke("api-call", {
      method: "GET",
      endpoint: `/api/commit/diff?hash=${commitHash}`,
      rawResponse: true,
    });
  },
});

/**
 * 页面加载完成后的初始化
 *
 * 当DOM加载完成时，可以执行一些初始化操作。
 */
window.addEventListener("DOMContentLoaded", () => {
  console.log("Preload script loaded successfully");
  console.log("Electron API exposed to window.electronAPI");
});
