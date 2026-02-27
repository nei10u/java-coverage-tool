/**
 * Electron主进程 - 应用入口
 *
 * 这个文件是Electron应用的主进程，负责：
 * 1. 创建和管理应用窗口
 * 2. 启动Java后端服务
 * 3. 处理系统级事件
 * 4. 管理应用生命周期
 */

const { app, BrowserWindow, ipcMain, dialog } = require("electron");
const path = require("path");
const { spawn } = require("child_process");
const fs = require("fs");
const axios = require("axios");

// 主窗口对象
let mainWindow = null;

// Java后端进程
let javaBackend = null;

// Java后端端口号
let backendPort = 4567;

/**
 * 创建主窗口
 *
 * 这个函数创建应用的主窗口，加载React前端界面。
 */
function createWindow() {
  // 创建浏览器窗口
  mainWindow = new BrowserWindow({
    width: 1400, // 窗口宽度
    height: 900, // 窗口高度
    minWidth: 1000, // 最小宽度
    minHeight: 700, // 最小高度
    webPreferences: {
      preload: path.join(__dirname, "preload.js"),
      nodeIntegration: false,
      contextIsolation: true,
    },
    // 窗口图标
    icon: path.join(__dirname, "assets", "icon.png"),
    // 窗口标题
    title: "Java Coverage Tool",
  });

  // 开发模式：加载React开发服务器
  if (process.env.NODE_ENV === "development") {
    mainWindow.loadURL("http://localhost:3000");
    // 打开开发者工具
    mainWindow.webContents.openDevTools();
  } else {
    // 生产模式：加载构建后的HTML文件
    // 判断是否为打包后的应用
    const isPackaged = app.isPackaged;
    let frontendPath;

    if (isPackaged) {
      // 打包后的路径 - extraResources会放在resourcesPath下
      frontendPath = path.join(
        process.resourcesPath,
        "app",
        "react-frontend",
        "build",
        "index.html",
      );
    } else {
      // 开发环境打包测试路径
      frontendPath = path.join(__dirname, "../react-frontend/build/index.html");
    }

    console.log("Loading frontend from:", frontendPath);
    mainWindow.loadFile(frontendPath);
  }

  // 窗口关闭事件
  mainWindow.on("closed", () => {
    mainWindow = null;
  });
}

/**
 * 启动Java后端服务
 *
 * 这个函数启动嵌入式Java后端服务，前端通过HTTP API与后端通信。
 */
function startJavaBackend() {
  return new Promise((resolve, reject) => {
    try {
      // 查找Java后端JAR文件的多个可能路径
      let jarFile = null;

      // 判断是否为打包后的应用
      const isPackaged = !process.env.NODE_ENV && app.isPackaged;

      const possiblePaths = isPackaged
        ? [
            // 打包后的路径 - extraResources会放在app同级目录
            path.join(process.resourcesPath, "java-backend.jar"),
            // macOS特定路径
            path.join(app.getAppPath(), "..", "java-backend.jar"),
          ]
        : [
            // 开发环境路径
            path.join(__dirname, "../build/java-backend.jar"),
            path.join(__dirname, "../../build/java-backend.jar"),
          ];

      // 尝试每个可能的路径
      for (const testPath of possiblePaths) {
        console.log("Checking JAR path:", testPath);
        if (fs.existsSync(testPath)) {
          jarFile = testPath;
          console.log("Found JAR at:", testPath);
          break;
        }
      }

      // 如果所有路径都找不到JAR文件
      if (!jarFile) {
        console.error("Java backend JAR not found in any location");
        console.error("Is packaged:", isPackaged);
        console.error("Checked paths:", possiblePaths);
        reject(
          new Error(
            "Java backend JAR file not found. Please build the Java backend first.",
          ),
        );
        return;
      }

      console.log("Starting Java backend from:", jarFile);

      // 使用spawn启动Java进程
      javaBackend = spawn("java", ["-jar", jarFile], {
        stdio: ["ignore", "pipe", "pipe"], // 忽略stdin，捕获stdout和stderr
        detached: false, // 不分离进程
      });

      // 监听Java进程的标准输出
      javaBackend.stdout.on("data", (data) => {
        const output = data.toString();
        console.log("Java Backend:", output);

        // 检测服务器是否启动成功
        if (output.includes("Server running on port")) {
          // 从输出中提取端口号
          const portMatch = output.match(/port:\s*(\d+)/);
          if (portMatch) {
            backendPort = parseInt(portMatch[1]);
          }
          resolve(backendPort);
        }
      });

      // 监听Java进程的错误输出
      javaBackend.stderr.on("data", (data) => {
        console.error("Java Backend Error:", data.toString());
      });

      // 监听Java进程退出事件
      javaBackend.on("close", (code) => {
        console.log("Java Backend exited with code:", code);
        javaBackend = null;
      });

      // 设置超时，如果5秒内没有启动成功则reject
      setTimeout(() => {
        if (!javaBackend) {
          reject(new Error("Java backend startup timeout"));
        }
      }, 5000);
    } catch (error) {
      reject(error);
    }
  });
}

/**
 * 停止Java后端服务
 */
function stopJavaBackend() {
  if (javaBackend) {
    console.log("Stopping Java backend...");
    javaBackend.kill();
    javaBackend = null;
  }
}

/**
 * 应用准备就绪事件
 *
 * 当Electron完成初始化时触发，这时可以创建窗口。
 */
app.whenReady().then(async () => {
  try {
    // 启动Java后端
    console.log("Starting Java backend...");
    const port = await startJavaBackend();
    console.log("Java backend started on port:", port);

    // 创建主窗口
    createWindow();

    // macOS特殊处理：当所有窗口关闭后重新激活应用
    app.on("activate", () => {
      if (BrowserWindow.getAllWindows().length === 0) {
        createWindow();
      }
    });
  } catch (error) {
    console.error("Failed to start application:", error);
    dialog.showErrorBox("启动失败", `无法启动应用：${error.message}`);
    app.quit();
  }
});

/**
 * 所有窗口关闭事件
 *
 * 在macOS上，通常应用不会退出，除非用户明确按Cmd+Q。
 * 在其他平台上，所有窗口关闭时应用会退出。
 */
app.on("window-all-closed", () => {
  // 停止Java后端
  stopJavaBackend();

  // 在macOS上，除非用户明确退出，否则应用保持活动状态
  if (process.platform !== "darwin") {
    app.quit();
  }
});

/**
 * 应用退出事件
 *
 * 确保在应用退出前清理所有资源。
 */
app.on("before-quit", () => {
  stopJavaBackend();
});

/**
 * IPC处理：选择目录
 *
 * 打开目录选择对话框，让用户选择项目目录。
 */
ipcMain.handle("select-directory", async () => {
  const result = await dialog.showOpenDialog(mainWindow, {
    properties: ["openDirectory"],
    title: "选择Java项目目录",
  });

  if (result.canceled) {
    return null;
  }

  return result.filePaths[0];
});

/**
 * IPC处理：调用后端API
 *
 * 提供一个通用的API调用接口，让渲染进程可以通过主进程访问后端。
 */
ipcMain.handle(
  "api-call",
  async (event, { method, endpoint, data, rawResponse }) => {
    try {
      const url = `http://localhost:${backendPort}${endpoint}`;

      let response;
      if (method === "GET") {
        response = await axios.get(url, {
          responseType: rawResponse ? "text" : "json",
        });
      } else if (method === "POST") {
        response = await axios.post(url, data);
      } else if (method === "DELETE") {
        response = await axios.delete(url);
      } else if (method === "PUT") {
        response = await axios.put(url, data);
      }

      return response.data;
    } catch (error) {
      console.error("API call failed:", error);
      throw error;
    }
  },
);
