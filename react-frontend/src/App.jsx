/**
 * React应用入口组件
 *
 * 这是React应用的主组件，负责设置路由和全局布局。
 */

import React from "react";
import { HashRouter as Router, Routes, Route } from "react-router-dom";
import { ConfigProvider } from "antd";
import zhCN from "antd/locale/zh_CN"; // Ant Design中文语言包
import HomePage from "./pages/HomePage";
import AnalysisPage from "./pages/AnalysisPage";
import ReportPage from "./pages/ReportPage";
import "./App.css";

/**
 * App组件
 *
 * 应用的根组件，配置路由和全局设置。
 */
function App() {
  return (
    // ConfigProvider: Ant Design的全局配置组件
    <ConfigProvider
      locale={zhCN} // 设置中文语言
      theme={{
        token: {
          colorPrimary: "#4CAF50", // 主题色：绿色
          borderRadius: 8, // 全局圆角大小
        },
      }}
    >
      {/* Router: React路由组件，管理页面导航 */}
      <Router>
        {/* Routes: 路由配置容器 */}
        <Routes>
          {/* 首页路由 */}
          <Route path="/" element={<HomePage />} />

          {/* 分析页路由 */}
          <Route path="/analysis" element={<AnalysisPage />} />

          {/* 报告页路由，支持通过URL参数传递分析ID */}
          <Route path="/report/:analysisId" element={<ReportPage />} />
        </Routes>
      </Router>
    </ConfigProvider>
  );
}

export default App;
