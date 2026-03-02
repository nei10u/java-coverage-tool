/**
 * 首页组件
 *
 * 提供项目选择、目录配置、报告管理和分析启动功能。
 * 这是用户的主要交互界面。
 */

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Button,
  Card,
  Steps,
  message,
  Tree,
  Spin,
  Alert,
  Row,
  Col,
  Table,
  Input,
  Tag,
  Popconfirm,
  Tooltip,
  Statistic,
  Progress,
  Space,
} from "antd";
import {
  FolderOpenOutlined,
  PlayCircleOutlined,
  SearchOutlined,
  SettingOutlined,
  ReloadOutlined,
  DeleteOutlined,
  EyeOutlined,
  SaveOutlined,
  HistoryOutlined,
  FileTextOutlined,
} from "@ant-design/icons";
import "./HomePage.css";

const { DirectoryTree } = Tree;

/**
 * HomePage组件
 */
function HomePage() {
  const navigate = useNavigate();

  // 项目分析相关状态
  const [currentStep, setCurrentStep] = useState(0);
  const [projectPath, setProjectPath] = useState("");
  const [projectStructure, setProjectStructure] = useState(null);
  const [selectedSourceDirs, setSelectedSourceDirs] = useState([]);
  const [selectedTestDirs, setSelectedTestDirs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [sourceTreeData, setSourceTreeData] = useState([]);
  const [testTreeData, setTestTreeData] = useState([]);
  const [sourceCheckedKeys, setSourceCheckedKeys] = useState([]);
  const [testCheckedKeys, setTestCheckedKeys] = useState([]);

  // 报告配置相关状态
  const [reportSavePath, setReportSavePath] = useState("");
  const [pathLoading, setPathLoading] = useState(false);
  const [pathSaving, setPathSaving] = useState(false);

  // 历史报告相关状态
  const [reportHistory, setReportHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(false);

  // 初始化：加载配置和历史
  useEffect(() => {
    loadReportConfig();
    loadReportHistory();
  }, []);

  // 加载报告配置
  const loadReportConfig = async () => {
    setPathLoading(true);
    try {
      const response = await window.electronAPI.getDefaultSavePath();
      setReportSavePath(response.path || "");
    } catch (error) {
      console.error("加载配置失败:", error);
    } finally {
      setPathLoading(false);
    }
  };

  // 加载报告历史
  const loadReportHistory = async () => {
    setHistoryLoading(true);
    try {
      const history = await window.electronAPI.getReportHistory();
      setReportHistory(history || []);
    } catch (error) {
      console.error("加载历史失败:", error);
    } finally {
      setHistoryLoading(false);
    }
  };

  // 选择报告保存目录
  const handleSelectReportPath = async () => {
    try {
      const path = await window.electronAPI.selectDirectory();
      if (path) {
        setReportSavePath(path);
      }
    } catch (error) {
      message.error("选择目录失败：" + error.message);
    }
  };

  // 保存报告配置
  const handleSaveReportConfig = async () => {
    if (!reportSavePath) {
      message.warning("请先选择报告保存目录");
      return;
    }
    setPathSaving(true);
    try {
      await window.electronAPI.setDefaultSavePath(reportSavePath);
      message.success("配置已保存");
    } catch (error) {
      message.error("保存配置失败：" + error.message);
    } finally {
      setPathSaving(false);
    }
  };

  // 查看历史报告
  const handleViewReport = async (record) => {
    if (record.savedPath) {
      try {
        await window.electronAPI.openFile(record.savedPath);
      } catch (error) {
        message.error("打开报告失败：" + error.message);
      }
    } else {
      message.warning("该报告未保存到本地");
    }
  };

  // 删除历史报告
  const handleDeleteReport = async (reportId) => {
    try {
      await window.electronAPI.deleteReport(reportId);
      message.success("报告已删除");
      loadReportHistory();
    } catch (error) {
      message.error("删除报告失败：" + error.message);
    }
  };

  // 选择项目目录
  const handleSelectProject = async () => {
    try {
      const selectedPath = await window.electronAPI.selectDirectory();
      if (selectedPath) {
        setProjectPath(selectedPath);
        message.success(`已选择项目：${selectedPath}`);
        setCurrentStep(1);
        await scanProject(selectedPath);
      }
    } catch (error) {
      message.error("选择目录失败：" + error.message);
    }
  };

  // 扫描项目
  const scanProject = async (path) => {
    setLoading(true);
    try {
      const structure = await window.electronAPI.scanProject(path);
      setProjectStructure(structure);

      if (structure.sourceTree) {
        setSourceTreeData([structure.sourceTree]);
      }
      if (structure.testTree) {
        setTestTreeData([structure.testTree]);
      }

      if (
        structure.sourceDirectories &&
        structure.sourceDirectories.length > 0
      ) {
        setSelectedSourceDirs(structure.sourceDirectories);
        setSourceCheckedKeys(structure.sourceDirectories);
      }
      if (structure.testDirectories && structure.testDirectories.length > 0) {
        setSelectedTestDirs(structure.testDirectories);
        setTestCheckedKeys(structure.testDirectories);
      }

      message.success("项目扫描完成");
      setCurrentStep(2);
    } catch (error) {
      message.error("扫描项目失败：" + error.message);
    } finally {
      setLoading(false);
    }
  };

  // 处理源码目录选择
  const handleSourceCheck = (checkedKeysValue) => {
    setSourceCheckedKeys(checkedKeysValue);
    const dirs = checkedKeysValue.filter((key) => {
      const node = findNodeByKey(sourceTreeData, key);
      return node && node.type !== "file";
    });
    setSelectedSourceDirs(dirs);
  };

  // 处理测试目录选择
  const handleTestCheck = (checkedKeysValue) => {
    setTestCheckedKeys(checkedKeysValue);
    const dirs = checkedKeysValue.filter((key) => {
      const node = findNodeByKey(testTreeData, key);
      return node && node.type !== "file";
    });
    setSelectedTestDirs(dirs);
  };

  // 根据key查找节点
  const findNodeByKey = (nodes, key) => {
    for (const node of nodes) {
      if (node.key === key) return node;
      if (node.children) {
        const found = findNodeByKey(node.children, key);
        if (found) return found;
      }
    }
    return null;
  };

  // 开始分析
  const handleStartAnalysis = async () => {
    if (selectedSourceDirs.length === 0) {
      message.warning("请至少选择一个源码目录");
      return;
    }
    if (selectedTestDirs.length === 0) {
      message.warning("请至少选择一个测试目录");
      return;
    }

    setLoading(true);
    try {
      const analysisRequest = {
        projectPath: projectPath,
        sourceDirectories: selectedSourceDirs,
        testDirectories: selectedTestDirs,
        gitOptions: { includeMergeCommits: false },
      };

      const response = await window.electronAPI.startAnalysis(analysisRequest);
      message.success("分析已启动");
      navigate(`/analysis?id=${response.analysisId}`);
    } catch (error) {
      message.error("启动分析失败：" + error.message);
    } finally {
      setLoading(false);
    }
  };

  // 历史报告表格列
  const historyColumns = [
    {
      title: "项目名称",
      dataIndex: "projectName",
      key: "projectName",
      width: 150,
      ellipsis: true,
      render: (text) => <Tooltip title={text}>{text}</Tooltip>,
    },
    {
      title: "生成时间",
      dataIndex: "generatedTime",
      key: "generatedTime",
      width: 160,
      render: (time) => (time ? new Date(time).toLocaleString() : "-"),
    },
    {
      title: "覆盖率",
      dataIndex: "overallCoverage",
      key: "overallCoverage",
      width: 120,
      render: (val) => (
        <Progress
          percent={val}
          size="small"
          status={val >= 80 ? "success" : val < 50 ? "exception" : "active"}
          format={(percent) => `${percent.toFixed(0)}%`}
        />
      ),
    },
    {
      title: "方法覆盖",
      key: "methodCoverage",
      width: 120,
      render: (_, record) => (
        <span>
          <Tag color="green">{record.coveredMethods}</Tag>
          <Tag>/</Tag>
          <Tag color="blue">{record.totalMethods}</Tag>
        </span>
      ),
    },
    {
      title: "操作",
      key: "actions",
      width: 120,
      render: (_, record) => (
        <Space>
          <Tooltip title="查看报告">
            <Button
              type="link"
              size="small"
              icon={<EyeOutlined />}
              onClick={() => handleViewReport(record)}
              disabled={!record.savedPath}
            />
          </Tooltip>
          <Popconfirm
            title="确定删除此报告？"
            onConfirm={() => handleDeleteReport(record.reportId)}
            okText="删除"
            cancelText="取消"
          >
            <Tooltip title="删除报告">
              <Button
                type="link"
                size="small"
                danger
                icon={<DeleteOutlined />}
              />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 步骤配置
  const steps = [
    { title: "选择项目", icon: <FolderOpenOutlined /> },
    { title: "扫描项目", icon: <SearchOutlined /> },
    { title: "配置目录", icon: <SettingOutlined /> },
    { title: "开始分析", icon: <PlayCircleOutlined /> },
  ];

  return (
    <div className="home-page">
      <div className="page-header">
        <h1>Java单元测试覆盖检测工具</h1>
        <p>分析Java项目的单元测试覆盖率，生成详细报告</p>
      </div>

      {/* 步骤指示器 */}
      <Card className="steps-card">
        <Steps current={currentStep} items={steps} />
      </Card>

      <div className="main-content">
        {/* 步骤0：项目选择 */}
        {currentStep === 0 && (
          <>
            {/* 项目选择卡片 */}
            <Card className="action-card" style={{ marginBottom: 16 }}>
              <div className="action-content">
                <FolderOpenOutlined className="action-icon" />
                <h2>选择Java项目</h2>
                <p>选择包含Java源码和测试代码的项目目录</p>
                <Button
                  type="primary"
                  size="large"
                  onClick={handleSelectProject}
                >
                  选择项目目录
                </Button>
              </div>
            </Card>

            {/* 报告配置和历史报告区域 */}
            <Row gutter={16}>
              {/* 报告目录配置 */}
              <Col span={8}>
                <Card
                  title={
                    <span>
                      <SettingOutlined /> 报告目录配置
                    </span>
                  }
                  size="small"
                >
                  <Input.Group compact style={{ marginBottom: 12 }}>
                    <Input
                      style={{ width: "calc(100% - 140px)" }}
                      value={reportSavePath}
                      onChange={(e) => setReportSavePath(e.target.value)}
                      placeholder="选择报告保存目录"
                      loading={pathLoading}
                    />
                    <Button onClick={handleSelectReportPath}>
                      <FolderOpenOutlined /> 选择
                    </Button>
                  </Input.Group>
                  <Button
                    type="primary"
                    block
                    icon={<SaveOutlined />}
                    loading={pathSaving}
                    onClick={handleSaveReportConfig}
                  >
                    保存配置
                  </Button>
                </Card>
              </Col>

              {/* 历史报告列表 */}
              <Col span={16}>
                <Card
                  title={
                    <span>
                      <HistoryOutlined /> 历史报告
                      <Tag color="blue" style={{ marginLeft: 8 }}>
                        {reportHistory.length}
                      </Tag>
                    </span>
                  }
                  extra={
                    <Button
                      type="link"
                      size="small"
                      icon={<ReloadOutlined />}
                      onClick={loadReportHistory}
                      loading={historyLoading}
                    >
                      刷新
                    </Button>
                  }
                  size="small"
                >
                  <Table
                    dataSource={reportHistory}
                    columns={historyColumns}
                    rowKey="reportId"
                    size="small"
                    pagination={
                      reportHistory.length > 5 ? { pageSize: 5 } : false
                    }
                    loading={historyLoading}
                    locale={{ emptyText: "暂无历史报告" }}
                  />
                </Card>
              </Col>
            </Row>
          </>
        )}

        {/* 步骤1：扫描中 */}
        {currentStep === 1 && (
          <Card className="action-card">
            <div className="action-content">
              <Spin size="large" />
              <h2>正在扫描项目...</h2>
              <p>分析项目结构，识别源码目录和测试目录</p>
            </div>
          </Card>
        )}

        {/* 步骤2：目录选择 */}
        {currentStep === 2 && (
          <div className="directory-selection">
            <Card title="项目信息" className="info-card">
              <Alert
                message={`项目类型：${projectStructure?.projectType || "Unknown"}`}
                type="info"
                style={{ marginBottom: 8 }}
              />
              {projectStructure?.isGitRepository ? (
                <Alert
                  message="Git仓库检测"
                  description={
                    <div>
                      ✓ 已检测到Git仓库
                      {projectStructure?.commitCount && (
                        <span style={{ marginLeft: 8 }}>
                          (共 {projectStructure.commitCount} 次提交)
                        </span>
                      )}
                    </div>
                  }
                  type="success"
                  showIcon
                />
              ) : (
                <Alert
                  message="Git仓库检测"
                  description="未检测到Git仓库。提交级统计功能将不可用，但其他功能正常工作。"
                  type="warning"
                  showIcon
                />
              )}
            </Card>

            <Row gutter={16} className="directory-row">
              <Col span={12}>
                <Card
                  title="源码目录 (src/main/java)"
                  className="tree-card source-card"
                  extra={
                    <span className="selected-count">
                      已选 {selectedSourceDirs.length} 个
                    </span>
                  }
                >
                  {sourceTreeData.length > 0 ? (
                    <DirectoryTree
                      checkable
                      checkedKeys={sourceCheckedKeys}
                      onCheck={handleSourceCheck}
                      treeData={sourceTreeData}
                      selectable={false}
                      style={{
                        minHeight: 300,
                        maxHeight: 500,
                        overflow: "auto",
                      }}
                    />
                  ) : (
                    <Alert message="未检测到标准源码目录结构" type="warning" />
                  )}
                </Card>
              </Col>

              <Col span={12}>
                <Card
                  title="测试目录 (src/test/java)"
                  className="tree-card test-card"
                  extra={
                    <span className="selected-count">
                      已选 {selectedTestDirs.length} 个
                    </span>
                  }
                >
                  {testTreeData.length > 0 ? (
                    <DirectoryTree
                      checkable
                      checkedKeys={testCheckedKeys}
                      onCheck={handleTestCheck}
                      treeData={testTreeData}
                      selectable={false}
                      style={{
                        minHeight: 300,
                        maxHeight: 500,
                        overflow: "auto",
                      }}
                    />
                  ) : (
                    <Alert message="未检测到标准测试目录结构" type="warning" />
                  )}
                </Card>
              </Col>
            </Row>

            <div className="action-buttons">
              <Button size="large" onClick={() => setCurrentStep(0)}>
                重新选择项目
              </Button>
              <Button
                type="primary"
                size="large"
                onClick={handleStartAnalysis}
                loading={loading}
                icon={<PlayCircleOutlined />}
              >
                开始分析
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default HomePage;
