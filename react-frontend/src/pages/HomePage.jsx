/**
 * 首页组件
 *
 * 提供项目选择、目录配置和分析启动功能。
 * 这是用户的主要交互界面。
 */

import React, { useState } from "react";
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
} from "antd";
import {
  FolderOpenOutlined,
  PlayCircleOutlined,
  SearchOutlined,
  SettingOutlined,
} from "@ant-design/icons";
import "./HomePage.css";

const { DirectoryTree } = Tree;

/**
 * HomePage组件
 */
function HomePage() {
  // 路由导航钩子，用于跳转页面
  const navigate = useNavigate();

  // 状态管理
  const [currentStep, setCurrentStep] = useState(0); // 当前步骤
  const [projectPath, setProjectPath] = useState(""); // 项目路径
  const [projectStructure, setProjectStructure] = useState(null); // 项目结构
  const [selectedSourceDirs, setSelectedSourceDirs] = useState([]); // 选中的源码目录
  const [selectedTestDirs, setSelectedTestDirs] = useState([]); // 选中的测试目录
  const [loading, setLoading] = useState(false); // 加载状态
  const [sourceTreeData, setSourceTreeData] = useState([]); // 源码目录树
  const [testTreeData, setTestTreeData] = useState([]); // 测试目录树
  const [sourceCheckedKeys, setSourceCheckedKeys] = useState([]); // 源码选中的key
  const [testCheckedKeys, setTestCheckedKeys] = useState([]); // 测试选中的key

  /**
   * 步骤1：选择项目目录
   *
   * 打开文件选择对话框，让用户选择Java项目根目录。
   */
  const handleSelectProject = async () => {
    try {
      // 调用Electron API打开目录选择对话框
      const selectedPath = await window.electronAPI.selectDirectory();

      if (selectedPath) {
        setProjectPath(selectedPath);
        message.success(`已选择项目：${selectedPath}`);
        setCurrentStep(1); // 进入下一步

        // 自动扫描项目
        await scanProject(selectedPath);
      }
    } catch (error) {
      message.error("选择目录失败：" + error.message);
    }
  };

  /**
   * 扫描项目
   *
   * 调用后端API扫描项目结构，识别源码目录和测试目录。
   */
  const scanProject = async (path) => {
    setLoading(true);
    try {
      // 调用后端扫描API
      const structure = await window.electronAPI.scanProject(path);
      setProjectStructure(structure);

      // 设置源码目录树
      if (structure.sourceTree) {
        setSourceTreeData([structure.sourceTree]);
      }

      // 设置测试目录树
      if (structure.testTree) {
        setTestTreeData([structure.testTree]);
      }

      // 默认选中标准目录
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
      setCurrentStep(2); // 进入目录选择步骤
    } catch (error) {
      message.error("扫描项目失败：" + error.message);
    } finally {
      setLoading(false);
    }
  };

  /**
   * 处理源码目录选择
   */
  const handleSourceCheck = (checkedKeysValue, info) => {
    setSourceCheckedKeys(checkedKeysValue);
    // 过滤出目录路径（排除文件）
    const dirs = checkedKeysValue.filter((key) => {
      const node = findNodeByKey(sourceTreeData, key);
      return node && node.type !== "file";
    });
    setSelectedSourceDirs(dirs);
  };

  /**
   * 处理测试目录选择
   */
  const handleTestCheck = (checkedKeysValue, info) => {
    setTestCheckedKeys(checkedKeysValue);
    // 过滤出目录路径（排除文件）
    const dirs = checkedKeysValue.filter((key) => {
      const node = findNodeByKey(testTreeData, key);
      return node && node.type !== "file";
    });
    setSelectedTestDirs(dirs);
  };

  /**
   * 根据key查找节点
   */
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

  /**
   * 步骤3：开始分析
   *
   * 提交分析请求，启动测试覆盖分析。
   */
  const handleStartAnalysis = async () => {
    // 验证是否选择了目录
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
      // 构建分析请求
      const analysisRequest = {
        projectPath: projectPath,
        sourceDirectories: selectedSourceDirs,
        testDirectories: selectedTestDirs,
        gitOptions: {
          includeMergeCommits: false,
        },
      };

      // 调用后端API启动分析
      const response = await window.electronAPI.startAnalysis(analysisRequest);

      message.success("分析已启动");

      // 跳转到分析页面，传递分析ID
      navigate(`/analysis?id=${response.analysisId}`);
    } catch (error) {
      message.error("启动分析失败：" + error.message);
    } finally {
      setLoading(false);
    }
  };

  /**
   * 步骤配置
   */
  const steps = [
    {
      title: "选择项目",
      icon: <FolderOpenOutlined />,
    },
    {
      title: "扫描项目",
      icon: <SearchOutlined />,
    },
    {
      title: "配置目录",
      icon: <SettingOutlined />,
    },
    {
      title: "开始分析",
      icon: <PlayCircleOutlined />,
    },
  ];

  return (
    <div className="home-page">
      {/* 页面标题 */}
      <div className="page-header">
        <h1>Java单元测试覆盖检测工具</h1>
        <p>分析Java项目的单元测试覆盖率，生成详细报告</p>
      </div>

      {/* 步骤指示器 */}
      <Card className="steps-card">
        <Steps current={currentStep} items={steps} />
      </Card>

      {/* 主要内容区域 */}
      <div className="main-content">
        {/* 步骤0：项目选择 */}
        {currentStep === 0 && (
          <Card className="action-card">
            <div className="action-content">
              <FolderOpenOutlined className="action-icon" />
              <h2>选择Java项目</h2>
              <p>选择包含Java源码和测试代码的项目目录</p>
              <Button type="primary" size="large" onClick={handleSelectProject}>
                选择项目目录
              </Button>
            </div>
          </Card>
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

            {/* 左右两栏目录选择 */}
            <Row gutter={16} className="directory-row">
              {/* 左侧：源码目录 */}
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

              {/* 右侧：测试目录 */}
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
