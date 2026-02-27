/**
 * 分析页面组件
 *
 * 显示分析进度和实时状态，分析完成后跳转到报告页面。
 */

import React, { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { Card, Progress, Button, message, Result } from "antd";
import {
  LoadingOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
} from "@ant-design/icons";
import "./AnalysisPage.css";

function AnalysisPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const analysisId = searchParams.get("id");

  const [progress, setProgress] = useState(0);
  const [stage, setStage] = useState("");
  const [message1, setMessage] = useState("初始化中...");
  const [status, setStatus] = useState("running");

  useEffect(() => {
    if (!analysisId) {
      message.error("缺少分析ID");
      navigate("/");
      return;
    }

    // 轮询获取进度
    const pollInterval = setInterval(async () => {
      try {
        const progressData =
          await window.electronAPI.getAnalysisProgress(analysisId);

        setProgress(progressData.progress);
        setStage(progressData.stage);
        setMessage(progressData.message);
        setStatus(
          progressData.stage === "COMPLETED"
            ? "completed"
            : progressData.stage === "ERROR"
              ? "error"
              : "running",
        );

        if (
          progressData.stage === "COMPLETED" ||
          progressData.stage === "ERROR"
        ) {
          clearInterval(pollInterval);

          if (progressData.stage === "COMPLETED") {
            setTimeout(() => {
              navigate(`/report/${analysisId}`);
            }, 2000);
          }
        }
      } catch (error) {
        console.error("Failed to get progress:", error);
      }
    }, 1000);

    return () => clearInterval(pollInterval);
  }, [analysisId, navigate]);

  const getIcon = () => {
    if (status === "completed")
      return <CheckCircleOutlined style={{ color: "#52c41a" }} />;
    if (status === "error")
      return <CloseCircleOutlined style={{ color: "#ff4d4f" }} />;
    return <LoadingOutlined style={{ color: "#1890ff" }} />;
  };

  const getStageText = (stage) => {
    const stageMap = {
      INITIALIZING: "初始化",
      SCANNING: "扫描项目",
      ANALYZING_BUSINESS: "分析业务类",
      ANALYZING_TESTS: "分析测试类",
      ANALYZING_GIT: "分析Git历史",
      ANALYZING_COVERAGE: "分析覆盖率",
      GENERATING_REPORT: "生成报告",
      COMPLETED: "完成",
      ERROR: "错误",
    };
    return stageMap[stage] || stage;
  };

  return (
    <div className="analysis-page">
      <Card className="progress-card">
        <div className="progress-content">
          {getIcon()}
          <h2>正在分析项目...</h2>

          <Progress
            percent={progress}
            status={
              status === "error"
                ? "exception"
                : status === "completed"
                  ? "success"
                  : "active"
            }
            strokeColor={{
              "0%": "#108ee9",
              "100%": "#87d068",
            }}
          />

          <div className="stage-info">
            <p>
              <strong>当前阶段：</strong>
              {getStageText(stage)}
            </p>
            <p>
              <strong>详细信息：</strong>
              {message1}
            </p>
          </div>

          {status === "completed" && (
            <Result
              status="success"
              title="分析完成"
              subTitle="即将跳转到报告页面..."
            />
          )}

          {status === "error" && (
            <Result
              status="error"
              title="分析失败"
              subTitle={message1}
              extra={[
                <Button type="primary" onClick={() => navigate("/")}>
                  返回首页
                </Button>,
              ]}
            />
          )}
        </div>
      </Card>
    </div>
  );
}

export default AnalysisPage;
