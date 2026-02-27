/**
 * React应用入口文件
 */

import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";

// 创建React根节点并渲染应用
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
