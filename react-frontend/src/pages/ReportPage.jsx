/**
 * 报告页面组件
 *
 * 显示测试覆盖率分析结果，支持多维度查看（开发者、提交、文件、方法）。
 */

import React, { useState, useEffect, useCallback, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Card,
  Tabs,
  Table,
  Statistic,
  Row,
  Col,
  Progress,
  Button,
  Spin,
  message,
  Tag,
  Tooltip,
  Modal,
  Popconfirm,
  Alert,
  Collapse,
  Space,
  Input,
} from "antd";
import {
  DownloadOutlined,
  UserOutlined,
  HistoryOutlined,
  FileOutlined,
  FunctionOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ArrowLeftOutlined,
  FolderOpenOutlined,
  SaveOutlined,
  HistoryOutlined as HistoryIcon,
  InfoCircleOutlined,
  SettingOutlined,
} from "@ant-design/icons";
import ReactECharts from "echarts-for-react";
import "./ReportPage.css";

const { Panel } = Collapse;

function ReportPage() {
  const { analysisId } = useParams();
  const navigate = useNavigate();

  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [reportHistory, setReportHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyModalVisible, setHistoryModalVisible] = useState(false);
  const [configModalVisible, setConfigModalVisible] = useState(false);
  const [newSavePath, setNewSavePath] = useState("");

  // 代码查看器相关状态
  const [codeViewerVisible, setCodeViewerVisible] = useState(false);
  const [currentFileContent, setCurrentFileContent] = useState(null);
  const [codeLoading, setCodeLoading] = useState(false);
  const [highlightLines, setHighlightLines] = useState(null);
  const codeViewerRef = useRef(null);

  // Commit Diff 查看器相关状态
  const [diffModalVisible, setDiffModalVisible] = useState(false);
  const [currentCommitDiff, setCurrentCommitDiff] = useState("");
  const [currentCommitInfo, setCurrentCommitInfo] = useState(null);
  const [diffLoading, setDiffLoading] = useState(false);

  const loadAnalysisResult = useCallback(async () => {
    try {
      const data = await window.electronAPI.getAnalysisResult(analysisId);
      setResult(data);
      setLoading(false);
    } catch (error) {
      message.error("加载分析结果失败：" + error.message);
      setLoading(false);
    }
  }, [analysisId]);

  const loadDefaultSavePath = useCallback(async () => {
    try {
      const response = await window.electronAPI.getDefaultSavePath();
      setNewSavePath(response.path || "");
    } catch (error) {
      console.error("加载默认路径失败:", error);
    }
  }, []);

  useEffect(() => {
    if (analysisId) {
      loadAnalysisResult();
    }
    loadDefaultSavePath();
  }, [analysisId, loadAnalysisResult, loadDefaultSavePath]);

  const handleGoBack = () => {
    navigate("/");
  };

  const handleExportReport = async () => {
    try {
      const html = await window.electronAPI.exportReport(analysisId);

      const blob = new Blob([html], { type: "text/html" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `coverage-report-${analysisId}.html`;
      a.click();
      URL.revokeObjectURL(url);

      message.success("报告已导出");
    } catch (error) {
      message.error("导出报告失败：" + error.message);
    }
  };

  const handleSaveReport = async () => {
    try {
      const response = await window.electronAPI.saveReport(analysisId);
      if (response.success) {
        message.success(`报告已保存: ${response.filePath}`);
      }
    } catch (error) {
      message.error("保存报告失败：" + error.message);
    }
  };

  const loadReportHistory = async () => {
    setHistoryLoading(true);
    try {
      const history = await window.electronAPI.getReportHistory();
      setReportHistory(history || []);
    } catch (error) {
      message.error("加载报告历史失败：" + error.message);
    } finally {
      setHistoryLoading(false);
    }
  };

  const handleDeleteReport = async (reportId) => {
    try {
      await window.electronAPI.deleteReport(reportId);
      message.success("报告已删除");
      loadReportHistory();
    } catch (error) {
      message.error("删除报告失败：" + error.message);
    }
  };

  const handleViewHistory = () => {
    setHistoryModalVisible(true);
    loadReportHistory();
  };

  const handleSelectSavePath = async () => {
    const path = await window.electronAPI.selectDirectory();
    if (path) {
      setNewSavePath(path);
    }
  };

  const handleSaveConfig = async () => {
    try {
      await window.electronAPI.setDefaultSavePath(newSavePath);
      setConfigModalVisible(false);
      message.success("设置已保存");
    } catch (error) {
      message.error("保存设置失败：" + error.message);
    }
  };

  // 查看文件内容
  const handleViewFile = async (filePath, startLine = null, endLine = null) => {
    try {
      setCodeLoading(true);
      setCodeViewerVisible(true);
      setHighlightLines(
        startLine ? { start: startLine, end: endLine || startLine } : null,
      );

      const fileContent = await window.electronAPI.readFileContent(filePath);
      setCurrentFileContent(fileContent);
    } catch (error) {
      message.error("读取文件失败：" + error.message);
      setCodeViewerVisible(false);
    } finally {
      setCodeLoading(false);
    }
  };

  // 滚动到高亮行
  useEffect(() => {
    if (codeViewerVisible && highlightLines && codeViewerRef.current) {
      setTimeout(() => {
        const lineElement = codeViewerRef.current?.querySelector(
          `[data-line-number="${highlightLines.start}"]`,
        );
        if (lineElement) {
          lineElement.scrollIntoView({ behavior: "smooth", block: "center" });
        }
      }, 100);
    }
  }, [codeViewerVisible, highlightLines, currentFileContent]);

  // 查看提交差异
  const handleViewCommitDiff = async (commit) => {
    try {
      setDiffLoading(true);
      setDiffModalVisible(true);
      setCurrentCommitInfo(commit);

      const diff = await window.electronAPI.getCommitDiff(commit.commitHash);
      setCurrentCommitDiff(diff || "无变更内容");
    } catch (error) {
      message.error("获取提交差异失败：" + error.message);
      setDiffModalVisible(false);
    } finally {
      setDiffLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <Spin size="large" />
        <p>加载分析结果...</p>
      </div>
    );
  }

  if (!result) {
    return <div>未找到分析结果</div>;
  }

  const coverageReport = result.coverageReport;
  const gitStatistics = result.gitStatistics;

  // 复杂度说明组件
  const renderComplexityHelp = () => (
    <Collapse ghost style={{ marginBottom: 16 }}>
      <Panel
        header={
          <span>
            <InfoCircleOutlined /> 复杂度说明
          </span>
        }
        key="1"
      >
        <Alert
          type="info"
          showIcon
          message="圈复杂度（Cyclomatic Complexity）说明"
          description={
            <div>
              <p>
                <strong>定义</strong>
                ：圈复杂度是衡量代码复杂度的指标，表示代码中独立执行路径的数量。
              </p>
              <p>
                <strong>计算规则</strong>：
              </p>
              <ul style={{ paddingLeft: 20 }}>
                <li>初始值为 1（方法本身）</li>
                <li>
                  每个 <code>if</code> 语句 +1
                </li>
                <li>
                  每个 <code>for</code> / <code>while</code> /{" "}
                  <code>do-while</code> 循环 +1
                </li>
                <li>
                  每个 <code>case</code> 分支 +1
                </li>
                <li>
                  每个 <code>catch</code> 块 +1
                </li>
                <li>
                  每个三元运算符 <code>?:</code> +1
                </li>
              </ul>
              <p>
                <strong>复杂度等级</strong>：
              </p>
              <ul style={{ paddingLeft: 20 }}>
                <li>
                  <Tag color="green">1-5</Tag> 简单 - 代码清晰易读
                </li>
                <li>
                  <Tag color="orange">6-10</Tag> 中等 - 建议简化
                </li>
                <li>
                  <Tag color="red">&gt;10</Tag> 复杂 - 强烈建议重构
                </li>
              </ul>
              <p>
                <strong>建议</strong>
                ：复杂度越高的方法需要越多的测试用例来覆盖所有执行路径。
              </p>
            </div>
          }
        />
      </Panel>
    </Collapse>
  );

  // 覆盖率统计卡片
  const renderStatistics = () => (
    <Card title="覆盖率统计">
      <Row gutter={16}>
        <Col span={6}>
          <Statistic
            title="总体覆盖率"
            value={coverageReport.overallCoverage.toFixed(2)}
            suffix="%"
            valueStyle={{ color: "#3f8600" }}
          />
          <Progress percent={coverageReport.overallCoverage} showInfo={false} />
        </Col>
        <Col span={6}>
          <Statistic
            title="业务类"
            value={coverageReport.totalBusinessClasses}
          />
        </Col>
        <Col span={6}>
          <Statistic title="测试类" value={coverageReport.totalTestClasses} />
        </Col>
        <Col span={6}>
          <Statistic title="测试方法" value={coverageReport.totalTestMethods} />
        </Col>
      </Row>
      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={6}>
          <Statistic
            title="已覆盖方法"
            value={coverageReport.coveredMethods}
            suffix={`/ ${coverageReport.totalMethods}`}
          />
        </Col>
        <Col span={6}>
          <Statistic
            title="未覆盖方法"
            value={coverageReport.uncoveredMethods}
            valueStyle={{ color: "#cf1322" }}
          />
        </Col>
        <Col span={6}>
          <Statistic
            title="平均测试粒度"
            value={coverageReport.averageGranularityScore.toFixed(1)}
            suffix="分"
          />
        </Col>
        <Col span={6}>
          <Statistic
            title="文件覆盖数"
            value={coverageReport.coveredBusinessClasses}
            suffix={`/ ${coverageReport.totalBusinessClasses}`}
          />
        </Col>
      </Row>
    </Card>
  );

  // 方法统计表格（所有方法）
  const renderMethodStatistics = () => {
    const allMethods = coverageReport.allMethodsList || [];

    // 按复杂度倒序排列
    const sortedAllMethods = [...allMethods].sort((a, b) => {
      const complexityA = a.complexity || 0;
      const complexityB = b.complexity || 0;
      return complexityB - complexityA;
    });

    const columns = [
      {
        title: "类名",
        dataIndex: "className",
        key: "className",
        width: 180,
        ellipsis: true,
        render: (text) => {
          const parts = text.split(".");
          return <Tooltip title={text}>{parts[parts.length - 1]}</Tooltip>;
        },
      },
      {
        title: "完整方法签名",
        dataIndex: "fullSignature",
        key: "fullSignature",
        width: 350,
        ellipsis: true,
        render: (text) => (
          <Tooltip title={text}>
            <span style={{ fontFamily: "monospace" }}>{text}</span>
          </Tooltip>
        ),
      },
      {
        title: "行号",
        key: "lineNumbers",
        width: 100,
        render: (_, record) => (
          <span style={{ fontFamily: "monospace" }}>
            {record.startLineNumber}-{record.endLineNumber}
          </span>
        ),
      },
      {
        title: "代码行数",
        dataIndex: "linesOfCode",
        key: "linesOfCode",
        width: 90,
        sorter: (a, b) => a.linesOfCode - b.linesOfCode,
      },
      {
        title: "复杂度",
        dataIndex: "complexity",
        key: "complexity",
        width: 80,
        sorter: (a, b) => a.complexity - b.complexity,
        render: (val) => {
          let color = "green";
          if (val > 10) color = "red";
          else if (val > 5) color = "orange";
          return <Tag color={color}>{val}</Tag>;
        },
      },
      {
        title: "覆盖状态",
        dataIndex: "isCovered",
        key: "isCovered",
        width: 100,
        filters: [
          { text: "已覆盖", value: true },
          { text: "未覆盖", value: false },
        ],
        onFilter: (value, record) => record.isCovered === value,
        render: (covered) =>
          covered ? (
            <Tag icon={<CheckCircleOutlined />} color="success">
              已覆盖
            </Tag>
          ) : (
            <Tag icon={<CloseCircleOutlined />} color="error">
              未覆盖
            </Tag>
          ),
      },
      {
        title: "测试方法数",
        dataIndex: "testMethodCount",
        key: "testMethodCount",
        width: 100,
        sorter: (a, b) => a.testMethodCount - b.testMethodCount,
      },
      {
        title: "测试粒度",
        dataIndex: "granularityLevel",
        key: "granularityLevel",
        width: 100,
        filters: [
          { text: "优秀", value: "EXCELLENT" },
          { text: "良好", value: "GOOD" },
          { text: "可接受", value: "ACCEPTABLE" },
          { text: "较差", value: "POOR" },
        ],
        onFilter: (value, record) => record.granularityLevel === value,
        render: (level) => {
          const colorMap = {
            EXCELLENT: "green",
            GOOD: "blue",
            ACCEPTABLE: "orange",
            POOR: "red",
          };
          const textMap = {
            EXCELLENT: "优秀",
            GOOD: "良好",
            ACCEPTABLE: "可接受",
            POOR: "较差",
          };
          return <Tag color={colorMap[level]}>{textMap[level]}</Tag>;
        },
      },
    ];

    return (
      <>
        {renderComplexityHelp()}
        <Table
          dataSource={sortedAllMethods}
          columns={columns}
          rowKey={(record, index) =>
            `${record.className}-${record.signature}-${index}`
          }
          pagination={{
            pageSize: 20,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
          scroll={{ x: 1200 }}
          size="small"
        />
      </>
    );
  };

  // 未覆盖方法表格
  const renderUncoveredMethods = () => {
    const uncoveredList = coverageReport.uncoveredMethodList || [];

    // 按复杂度倒序排列（复杂度高的在前）
    const sortedUncoveredList = [...uncoveredList].sort((a, b) => {
      const complexityA = a.complexity || 0;
      const complexityB = b.complexity || 0;
      return complexityB - complexityA;
    });

    const columns = [
      {
        title: "类名",
        dataIndex: "className",
        key: "className",
        width: 180,
        ellipsis: true,
        render: (text) => {
          const parts = text.split(".");
          return <Tooltip title={text}>{parts[parts.length - 1]}</Tooltip>;
        },
      },
      {
        title: "完整方法签名",
        dataIndex: "fullSignature",
        key: "fullSignature",
        width: 400,
        ellipsis: true,
        render: (text) => (
          <Tooltip title={text}>
            <span style={{ fontFamily: "monospace" }}>{text}</span>
          </Tooltip>
        ),
      },
      {
        title: "行号范围",
        key: "lineNumbers",
        width: 120,
        render: (_, record) => (
          <span style={{ fontFamily: "monospace" }}>
            L{record.startLineNumber} - L{record.endLineNumber}
          </span>
        ),
      },
      {
        title: "代码行数",
        dataIndex: "linesOfCode",
        key: "linesOfCode",
        width: 100,
        sorter: (a, b) => a.linesOfCode - b.linesOfCode,
        render: (val) => (
          <Tag color={val > 20 ? "orange" : "blue"}>{val} 行</Tag>
        ),
      },
      {
        title: "复杂度",
        dataIndex: "complexity",
        key: "complexity",
        width: 90,
        sorter: (a, b) => a.complexity - b.complexity,
        render: (val) => {
          let color = "green";
          if (val > 10) color = "red";
          else if (val > 5) color = "orange";
          return <Tag color={color}>{val}</Tag>;
        },
      },
      {
        title: "文件路径",
        dataIndex: "filePath",
        key: "filePath",
        width: 250,
        ellipsis: true,
        render: (text) => <Tooltip title={text}>{text}</Tooltip>,
      },
    ];

    return (
      <>
        {renderComplexityHelp()}
        <Table
          dataSource={sortedUncoveredList}
          columns={columns}
          rowKey={(record, index) =>
            `${record.className}-${record.signature}-${index}`
          }
          pagination={{
            pageSize: 20,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条未覆盖方法`,
          }}
          scroll={{ x: 1300 }}
          size="small"
        />
      </>
    );
  };

  // 文件统计表格（三层嵌套：文件 -> 类 -> 方法）
  const renderFileStatistics = () => {
    const fileList = coverageReport.fileStatisticsList || [];

    // 先按 fullyQualifiedName 去重（防止重复数据）
    const uniqueFileList = [];
    const seenClasses = new Set();
    fileList.forEach((file) => {
      const classKey = file.fullyQualifiedName || file.filePath;
      if (!seenClasses.has(classKey)) {
        seenClasses.add(classKey);
        uniqueFileList.push(file);
      }
    });

    // 按文件路径分组
    const fileGroups = {};
    uniqueFileList.forEach((file) => {
      const filePath = file.filePath || "Unknown";
      if (!fileGroups[filePath]) {
        fileGroups[filePath] = {
          filePath: filePath,
          fileName: filePath.split("/").pop() || filePath,
          classes: [],
          totalMethods: 0,
          coveredMethods: 0,
          uncoveredMethods: 0,
        };
      }
      fileGroups[filePath].classes.push(file);
      fileGroups[filePath].totalMethods += file.totalMethods || 0;
      fileGroups[filePath].coveredMethods += file.coveredMethods || 0;
      fileGroups[filePath].uncoveredMethods += file.uncoveredMethods || 0;
    });

    // 转换为数组并计算覆盖率
    const fileGroupList = Object.values(fileGroups).map((group) => ({
      ...group,
      coverageRate:
        group.totalMethods > 0
          ? ((group.coveredMethods / group.totalMethods) * 100).toFixed(1)
          : 0,
      avgComplexity:
        group.classes.reduce((sum, c) => {
          const avgC =
            c.methods && c.methods.length > 0
              ? c.methods.reduce((s, m) => s + (m.complexity || 0), 0) /
                c.methods.length
              : 0;
          return sum + avgC;
        }, 0) / group.classes.length,
    }));

    // 按平均复杂度倒序排列
    const sortedFileGroups = [...fileGroupList].sort(
      (a, b) => b.avgComplexity - a.avgComplexity,
    );

    // 第一层：文件维度列定义
    const fileColumns = [
      {
        title: "文件名",
        dataIndex: "fileName",
        key: "fileName",
        width: 200,
        ellipsis: true,
        render: (text, record) => (
          <Tooltip title={record.filePath}>
            <button
              type="button"
              onClick={() => handleViewFile(record.filePath)}
              style={{
                color: "#1890ff",
                cursor: "pointer",
                background: "none",
                border: "none",
                padding: 0,
                font: "inherit",
                textDecoration: "underline",
                display: "inline",
              }}
            >
              📄 {text}
            </button>
          </Tooltip>
        ),
      },
      {
        title: "类数量",
        dataIndex: "classes",
        key: "classCount",
        width: 80,
        render: (classes) => <Tag color="blue">{classes.length}</Tag>,
      },
      {
        title: "总方法数",
        dataIndex: "totalMethods",
        key: "totalMethods",
        width: 90,
        sorter: (a, b) => a.totalMethods - b.totalMethods,
      },
      {
        title: "已覆盖",
        dataIndex: "coveredMethods",
        key: "coveredMethods",
        width: 80,
        sorter: (a, b) => a.coveredMethods - b.coveredMethods,
      },
      {
        title: "未覆盖",
        dataIndex: "uncoveredMethods",
        key: "uncoveredMethods",
        width: 80,
        sorter: (a, b) => a.uncoveredMethods - b.uncoveredMethods,
        render: (val) =>
          val > 0 ? <span style={{ color: "#cf1322" }}>{val}</span> : val,
      },
      {
        title: "覆盖率",
        dataIndex: "coverageRate",
        key: "coverageRate",
        width: 140,
        sorter: (a, b) =>
          parseFloat(a.coverageRate) - parseFloat(b.coverageRate),
        render: (rate) => (
          <Progress
            percent={parseFloat(rate)}
            size="small"
            status={
              parseFloat(rate) === 100
                ? "success"
                : parseFloat(rate) < 50
                  ? "exception"
                  : "active"
            }
            format={(percent) => `${percent}%`}
          />
        ),
      },
      {
        title: "平均复杂度",
        dataIndex: "avgComplexity",
        key: "avgComplexity",
        width: 100,
        sorter: (a, b) => a.avgComplexity - b.avgComplexity,
        render: (val) => {
          let color = "green";
          if (val > 10) color = "red";
          else if (val > 5) color = "orange";
          return <Tag color={color}>{val.toFixed(1)}</Tag>;
        },
      },
    ];

    // 第二层：类维度列定义
    const classColumns = [
      {
        title: "类名",
        dataIndex: "className",
        key: "className",
        width: 180,
        ellipsis: true,
        render: (text, record) => (
          <Tooltip title={record.fullyQualifiedName}>
            <span style={{ fontWeight: 500 }}>📦 {text}</span>
          </Tooltip>
        ),
      },
      {
        title: "类类型",
        dataIndex: "classType",
        key: "classType",
        width: 100,
        filters: [
          { text: "Service", value: "SERVICE" },
          { text: "Controller", value: "CONTROLLER" },
          { text: "Repository", value: "REPOSITORY" },
          { text: "Component", value: "COMPONENT" },
          { text: "其他", value: "OTHER" },
        ],
        onFilter: (value, record) => record.classType === value,
        render: (type) => {
          const colorMap = {
            SERVICE: "blue",
            CONTROLLER: "green",
            REPOSITORY: "purple",
            COMPONENT: "cyan",
            OTHER: "default",
          };
          return <Tag color={colorMap[type] || "default"}>{type}</Tag>;
        },
      },
      {
        title: "方法数",
        dataIndex: "totalMethods",
        key: "totalMethods",
        width: 70,
      },
      {
        title: "已覆盖",
        dataIndex: "coveredMethods",
        key: "coveredMethods",
        width: 70,
      },
      {
        title: "未覆盖",
        dataIndex: "uncoveredMethods",
        key: "uncoveredMethods",
        width: 70,
        render: (val) =>
          val > 0 ? <span style={{ color: "#cf1322" }}>{val}</span> : val,
      },
      {
        title: "覆盖率",
        dataIndex: "coverageRate",
        key: "coverageRate",
        width: 120,
        render: (rate) => (
          <Progress
            percent={rate}
            size="small"
            status={
              rate === 100 ? "success" : rate < 50 ? "exception" : "active"
            }
            format={(percent) => `${percent.toFixed(1)}%`}
          />
        ),
      },
      {
        title: "测试类",
        dataIndex: "correspondingTestClass",
        key: "correspondingTestClass",
        width: 150,
        ellipsis: true,
        render: (text, record) => (
          <span>
            {record.hasTestClass ? (
              <Tag color="success">存在</Tag>
            ) : (
              <Tag color="error">缺失</Tag>
            )}
            {text && <Tooltip title={text}>{text.split(".").pop()}</Tooltip>}
          </span>
        ),
      },
    ];

    // 第三层：方法维度列定义
    const methodColumns = (filePath) => [
      {
        title: "方法名",
        dataIndex: "methodName",
        key: "methodName",
        width: 200,
        ellipsis: true,
        render: (text, record) => (
          <Tooltip title={`${record.signature} (点击查看代码)`}>
            <button
              type="button"
              onClick={() =>
                handleViewFile(
                  filePath,
                  record.startLineNumber,
                  record.endLineNumber,
                )
              }
              style={{
                color: "#1890ff",
                cursor: "pointer",
                background: "none",
                border: "none",
                padding: 0,
                font: "inherit",
                textDecoration: "underline",
                display: "inline",
                fontFamily: "monospace",
                fontSize: 13,
              }}
            >
              ⚙️ {text}
            </button>
          </Tooltip>
        ),
      },
      {
        title: "覆盖状态",
        dataIndex: "covered",
        key: "covered",
        width: 90,
        render: (covered) =>
          covered ? (
            <Tag icon={<CheckCircleOutlined />} color="success">
              已覆盖
            </Tag>
          ) : (
            <Tag icon={<CloseCircleOutlined />} color="error">
              未覆盖
            </Tag>
          ),
      },
      {
        title: "行号范围",
        key: "lineNumbers",
        width: 110,
        render: (_, record) => (
          <span style={{ fontFamily: "monospace", fontSize: 12 }}>
            L{record.startLineNumber} - L{record.endLineNumber}
          </span>
        ),
      },
      {
        title: "代码行数",
        dataIndex: "linesOfCode",
        key: "linesOfCode",
        width: 80,
        render: (val) => (
          <Tag color={val > 20 ? "orange" : "blue"}>{val} 行</Tag>
        ),
      },
      {
        title: "复杂度",
        dataIndex: "complexity",
        key: "complexity",
        width: 70,
        render: (val) => {
          let color = "green";
          if (val > 10) color = "red";
          else if (val > 5) color = "orange";
          return <Tag color={color}>{val}</Tag>;
        },
      },
      {
        title: "测试方法数",
        dataIndex: "testMethodCount",
        key: "testMethodCount",
        width: 90,
        render: (val) => (val > 0 ? <Tag color="blue">{val}</Tag> : "-"),
      },
      {
        title: "测试粒度",
        dataIndex: "granularityLevel",
        key: "granularityLevel",
        width: 90,
        render: (level) => {
          const colorMap = {
            EXCELLENT: "green",
            GOOD: "blue",
            ACCEPTABLE: "orange",
            POOR: "red",
          };
          const textMap = {
            EXCELLENT: "优秀",
            GOOD: "良好",
            ACCEPTABLE: "可接受",
            POOR: "较差",
          };
          return level ? (
            <Tag color={colorMap[level]}>{textMap[level]}</Tag>
          ) : (
            "-"
          );
        },
      },
    ];

    // 第三层：方法列表渲染
    const renderMethods = (methods, filePath) => {
      if (!methods || methods.length === 0) {
        return (
          <div style={{ padding: "12px 16px 12px 48px", color: "#999" }}>
            暂无方法数据
          </div>
        );
      }

      // 按复杂度倒序排列方法
      const sortedMethods = [...methods].sort(
        (a, b) => (b.complexity || 0) - (a.complexity || 0),
      );

      return (
        <div style={{ padding: "12px 16px 12px 48px" }}>
          <div
            style={{
              marginBottom: 8,
              color: "#1890ff",
              fontSize: 13,
              fontWeight: 500,
            }}
          >
            <FunctionOutlined /> 方法列表 ({sortedMethods.length} 个)
          </div>
          <Table
            dataSource={sortedMethods}
            columns={methodColumns(filePath)}
            rowKey={(method, index) => `method-${index}`}
            pagination={false}
            size="small"
            scroll={{ x: 900 }}
          />
        </div>
      );
    };

    // 第二层：类列表渲染
    const renderClasses = (classes) => {
      if (!classes || classes.length === 0) {
        return (
          <div style={{ padding: "12px 16px 12px 32px", color: "#999" }}>
            暂无类数据
          </div>
        );
      }

      // 按平均复杂度倒序排列类
      const sortedClasses = [...classes].sort((a, b) => {
        const avgA =
          a.methods && a.methods.length > 0
            ? a.methods.reduce((sum, m) => sum + (m.complexity || 0), 0) /
              a.methods.length
            : 0;
        const avgB =
          b.methods && b.methods.length > 0
            ? b.methods.reduce((sum, m) => sum + (m.complexity || 0), 0) /
              b.methods.length
            : 0;
        return avgB - avgA;
      });

      return (
        <div style={{ padding: "12px 16px 12px 32px" }}>
          <div
            style={{
              marginBottom: 8,
              color: "#1890ff",
              fontSize: 13,
              fontWeight: 500,
            }}
          >
            📦 类列表 ({sortedClasses.length} 个)
          </div>
          <Table
            dataSource={sortedClasses}
            columns={classColumns}
            rowKey="fullyQualifiedName"
            expandable={{
              expandedRowRender: (record) =>
                renderMethods(record.methods, record.filePath),
              rowExpandable: (record) =>
                record.methods && record.methods.length > 0,
              expandRowByClick: true,
              expandIcon: ({ expanded, onExpand, record }) =>
                record.methods && record.methods.length > 0 ? (
                  <Tooltip
                    title={
                      expanded
                        ? "收起方法列表"
                        : `展开 ${record.methods.length} 个方法`
                    }
                  >
                    <span
                      style={{ cursor: "pointer", marginRight: 8 }}
                      onClick={(e) => onExpand(record, e)}
                    >
                      {expanded ? "▼" : "▶"}
                    </span>
                  </Tooltip>
                ) : null,
            }}
            pagination={false}
            size="small"
            scroll={{ x: 900 }}
          />
        </div>
      );
    };

    return (
      <>
        {renderComplexityHelp()}
        <Card title="文件统计" style={{ marginBottom: 16 }}>
          <Alert
            message="提示：点击文件展开类列表，点击类展开方法列表"
            type="info"
            showIcon
            style={{ marginBottom: 16 }}
          />
          <Table
            dataSource={sortedFileGroups}
            columns={fileColumns}
            rowKey="filePath"
            expandable={{
              expandedRowRender: (record) => renderClasses(record.classes),
              rowExpandable: (record) =>
                record.classes && record.classes.length > 0,
              expandRowByClick: true,
              expandIcon: ({ expanded, onExpand, record }) =>
                record.classes && record.classes.length > 0 ? (
                  <Tooltip
                    title={
                      expanded
                        ? "收起类列表"
                        : `展开 ${record.classes.length} 个类`
                    }
                  >
                    <span
                      style={{ cursor: "pointer", marginRight: 8 }}
                      onClick={(e) => onExpand(record, e)}
                    >
                      {expanded ? "▼" : "▶"}
                    </span>
                  </Tooltip>
                ) : null,
            }}
            pagination={{
              pageSize: 15,
              showSizeChanger: true,
              showTotal: (total) => `共 ${total} 个文件`,
            }}
            scroll={{ x: 1000 }}
            size="small"
          />
        </Card>
      </>
    );
  };

  // 提交统计表格（带图形化可视化）
  const renderCommitStatistics = () => {
    const commitList = coverageReport.commitStatisticsList || [];

    if (commitList.length === 0) {
      return (
        <Card>
          <p>暂无提交级统计数据，请确保项目为Git仓库。</p>
        </Card>
      );
    }

    // 按时间倒序排序（最新的在前）
    const sortedCommits = [...commitList].sort(
      (a, b) => new Date(b.commitDate) - new Date(a.commitDate),
    );

    // 图表1: 提交时间线图（代码变更趋势）
    const timelineOption = {
      title: {
        text: "提交代码变更趋势",
        left: "center",
        textStyle: { fontSize: 16, fontWeight: "bold" },
      },
      tooltip: {
        trigger: "axis",
        axisPointer: { type: "cross" },
      },
      legend: {
        data: ["新增行数", "删除行数", "净增行数"],
        top: 30,
      },
      grid: {
        left: "3%",
        right: "4%",
        bottom: "3%",
        top: 80,
        containLabel: true,
      },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: sortedCommits.map((c) => {
          const date = new Date(c.commitDate);
          return `${date.getMonth() + 1}/${date.getDate()}`;
        }),
        axisLabel: { rotate: 45, fontSize: 10 },
      },
      yAxis: {
        type: "value",
        name: "行数",
      },
      series: [
        {
          name: "新增行数",
          type: "line",
          data: sortedCommits.map((c) => c.linesAdded || 0),
          itemStyle: { color: "#52c41a" },
          areaStyle: { opacity: 0.3 },
        },
        {
          name: "删除行数",
          type: "line",
          data: sortedCommits.map((c) => c.linesDeleted || 0),
          itemStyle: { color: "#ff4d4f" },
          areaStyle: { opacity: 0.3 },
        },
        {
          name: "净增行数",
          type: "line",
          data: sortedCommits.map(
            (c) => (c.linesAdded || 0) - (c.linesDeleted || 0),
          ),
          itemStyle: { color: "#1890ff" },
          areaStyle: { opacity: 0.3 },
        },
      ],
    };

    // 图表2: 提交覆盖率柱状图
    const coverageOption = {
      title: {
        text: "提交新增代码覆盖率",
        left: "center",
        textStyle: { fontSize: 16, fontWeight: "bold" },
      },
      tooltip: {
        trigger: "axis",
        axisPointer: { type: "shadow" },
        formatter: (params) => {
          const data = params[0];
          const commit = sortedCommits[data.dataIndex];
          return `
            <div style="padding: 8px;">
              <div><strong>提交:</strong> ${commit.shortHash}</div>
              <div><strong>作者:</strong> ${commit.authorName}</div>
              <div><strong>覆盖率:</strong> ${data.value.toFixed(1)}%</div>
              <div><strong>提交信息:</strong> ${commit.commitMessage}</div>
            </div>
          `;
        },
      },
      grid: {
        left: "3%",
        right: "4%",
        bottom: "15%",
        top: 80,
        containLabel: true,
      },
      xAxis: {
        type: "category",
        data: sortedCommits.map((c) => c.shortHash),
        axisLabel: { rotate: 45, fontSize: 10 },
      },
      yAxis: {
        type: "value",
        name: "覆盖率 (%)",
        max: 100,
      },
      series: [
        {
          name: "覆盖率",
          type: "bar",
          data: sortedCommits.map((c) => c.addedCodeCoverage || 0),
          itemStyle: {
            color: (params) => {
              const value = params.value;
              if (value >= 80) return "#52c41a";
              if (value >= 50) return "#faad14";
              return "#ff4d4f";
            },
          },
          label: {
            show: true,
            position: "top",
            formatter: (params) => `${params.value.toFixed(0)}%`,
            fontSize: 10,
          },
        },
      ],
    };

    // 图表3: 开发者贡献统计（按作者分组）
    const developerContrib = {};
    sortedCommits.forEach((commit) => {
      const author = commit.authorName;
      if (!developerContrib[author]) {
        developerContrib[author] = {
          commits: 0,
          linesAdded: 0,
          linesDeleted: 0,
          methodsAdded: 0,
        };
      }
      developerContrib[author].commits++;
      developerContrib[author].linesAdded += commit.linesAdded || 0;
      developerContrib[author].linesDeleted += commit.linesDeleted || 0;
      developerContrib[author].methodsAdded += commit.methodsAdded || 0;
    });

    const developerOption = {
      title: {
        text: "开发者贡献统计",
        left: "center",
        textStyle: { fontSize: 16, fontWeight: "bold" },
      },
      tooltip: {
        trigger: "item",
        formatter: (params) => {
          const dev = developerContrib[params.name];
          return `
            <div style="padding: 8px;">
              <div><strong>${params.name}</strong></div>
              <div>提交次数: ${dev.commits}</div>
              <div>新增行数: +${dev.linesAdded}</div>
              <div>删除行数: -${dev.linesDeleted}</div>
              <div>新增方法: ${dev.methodsAdded}</div>
              <div>占比: ${params.percent.toFixed(1)}%</div>
            </div>
          `;
        },
      },
      legend: {
        orient: "vertical",
        left: "left",
        top: 60,
      },
      series: [
        {
          name: "代码贡献",
          type: "pie",
          radius: ["40%", "70%"],
          center: ["60%", "55%"],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: "#fff",
            borderWidth: 2,
          },
          label: {
            show: true,
            formatter: "{b}: {d}%",
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 14,
              fontWeight: "bold",
            },
          },
          data: Object.entries(developerContrib).map(([name, data]) => ({
            value: data.linesAdded + data.linesDeleted,
            name: name,
          })),
        },
      ],
    };

    const columns = [
      {
        title: "提交",
        dataIndex: "shortHash",
        key: "shortHash",
        width: 80,
        render: (text, record) => (
          <Tooltip title={record.commitHash}>
            <Tag color="blue">{text}</Tag>
          </Tooltip>
        ),
      },
      {
        title: "作者",
        dataIndex: "authorName",
        key: "authorName",
        width: 120,
        ellipsis: true,
      },
      {
        title: "提交时间",
        dataIndex: "commitDate",
        key: "commitDate",
        width: 150,
        render: (date) => (date ? new Date(date).toLocaleString() : "-"),
        sorter: (a, b) => new Date(a.commitDate) - new Date(b.commitDate),
      },
      {
        title: "提交信息",
        dataIndex: "commitMessage",
        key: "commitMessage",
        width: 250,
        ellipsis: true,
        render: (text) => <Tooltip title={text}>{text}</Tooltip>,
      },
      {
        title: "新增行数",
        dataIndex: "linesAdded",
        key: "linesAdded",
        width: 90,
        sorter: (a, b) => a.linesAdded - b.linesAdded,
        render: (val) => <span style={{ color: "#3f8600" }}>+{val}</span>,
      },
      {
        title: "删除行数",
        dataIndex: "linesDeleted",
        key: "linesDeleted",
        width: 90,
        sorter: (a, b) => a.linesDeleted - b.linesDeleted,
        render: (val) => <span style={{ color: "#cf1322" }}>-{val}</span>,
      },
      {
        title: "新增方法",
        dataIndex: "methodsAdded",
        key: "methodsAdded",
        width: 90,
        sorter: (a, b) => a.methodsAdded - b.methodsAdded,
      },
      {
        title: "新增覆盖率",
        dataIndex: "addedCodeCoverage",
        key: "addedCodeCoverage",
        width: 120,
        sorter: (a, b) => a.addedCodeCoverage - b.addedCodeCoverage,
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
        title: "操作",
        key: "actions",
        width: 100,
        render: (_, record) => (
          <Button
            type="link"
            size="small"
            icon={<HistoryOutlined />}
            onClick={() => handleViewCommitDiff(record)}
          >
            查看变更
          </Button>
        ),
      },
    ];

    return (
      <div>
        {/* 图表可视化区域 */}
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={24}>
            <Card>
              <ReactECharts
                option={timelineOption}
                style={{ height: 300 }}
                opts={{ renderer: "svg" }}
              />
            </Card>
          </Col>
          <Col span={12}>
            <Card>
              <ReactECharts
                option={coverageOption}
                style={{ height: 300 }}
                opts={{ renderer: "svg" }}
              />
            </Card>
          </Col>
          <Col span={12}>
            <Card>
              <ReactECharts
                option={developerOption}
                style={{ height: 300 }}
                opts={{ renderer: "svg" }}
              />
            </Card>
          </Col>
        </Row>

        {/* 详细数据表格 */}
        <Card title="提交详细数据">
          <Table
            dataSource={commitList}
            columns={columns}
            rowKey="commitHash"
            pagination={{
              pageSize: 15,
              showSizeChanger: true,
              showTotal: (total) => `共 ${total} 次提交`,
            }}
            scroll={{ x: 1100 }}
            size="small"
          />
        </Card>
      </div>
    );
  };

  // 开发者统计表格
  const renderDeveloperStats = () => {
    if (!gitStatistics) return <p>无Git统计信息</p>;

    const columns = [
      {
        title: "开发者",
        dataIndex: "developerName",
        key: "developerName",
        width: 150,
      },
      {
        title: "提交次数",
        dataIndex: "totalCommits",
        key: "totalCommits",
        width: 100,
        sorter: (a, b) => a.totalCommits - b.totalCommits,
      },
      {
        title: "新增行数",
        dataIndex: "totalLinesAdded",
        key: "totalLinesAdded",
        width: 100,
        sorter: (a, b) => a.totalLinesAdded - b.totalLinesAdded,
        render: (val) => <span style={{ color: "#3f8600" }}>+{val}</span>,
      },
      {
        title: "删除行数",
        dataIndex: "totalLinesDeleted",
        key: "totalLinesDeleted",
        width: 100,
        sorter: (a, b) => a.totalLinesDeleted - b.totalLinesDeleted,
        render: (val) => <span style={{ color: "#cf1322" }}>-{val}</span>,
      },
      {
        title: "平均覆盖率",
        dataIndex: "averageCoverageRate",
        key: "averageCoverageRate",
        width: 150,
        sorter: (a, b) =>
          (a.averageCoverageRate || 0) - (b.averageCoverageRate || 0),
        render: (val) =>
          val ? (
            <Progress
              percent={val}
              size="small"
              status={val >= 80 ? "success" : val < 50 ? "exception" : "active"}
              format={(percent) => `${percent.toFixed(1)}%`}
            />
          ) : (
            "N/A"
          ),
      },
    ];

    const dataSource = Object.values(gitStatistics.developerStats || {});

    return (
      <Table
        dataSource={dataSource}
        columns={columns}
        rowKey="developerEmail"
        pagination={{ pageSize: 15, showSizeChanger: true }}
        size="small"
      />
    );
  };

  // 报告历史模态框
  const renderHistoryModal = () => (
    <Modal
      title="报告历史"
      open={historyModalVisible}
      onCancel={() => setHistoryModalVisible(false)}
      footer={null}
      width={900}
    >
      <Table
        dataSource={reportHistory}
        loading={historyLoading}
        rowKey="reportId"
        pagination={{ pageSize: 10 }}
        size="small"
        columns={[
          {
            title: "项目名称",
            dataIndex: "projectName",
            key: "projectName",
            width: 150,
            ellipsis: true,
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
            width: 100,
            render: (val) => `${val.toFixed(1)}%`,
          },
          {
            title: "方法覆盖",
            key: "methodCoverage",
            width: 120,
            render: (_, record) =>
              `${record.coveredMethods}/${record.totalMethods}`,
          },
          {
            title: "保存路径",
            dataIndex: "savedPath",
            key: "savedPath",
            ellipsis: true,
            render: (text) =>
              text ? <Tooltip title={text}>{text}</Tooltip> : "-",
          },
          {
            title: "操作",
            key: "actions",
            width: 150,
            render: (_, record) => (
              <Space>
                {record.savedPath ? (
                  <Button
                    type="link"
                    size="small"
                    onClick={async () => {
                      try {
                        await window.electronAPI.openFile(record.savedPath);
                      } catch (error) {
                        message.error("打开文件失败：" + error.message);
                      }
                    }}
                  >
                    查看
                  </Button>
                ) : (
                  <Button
                    type="link"
                    size="small"
                    onClick={() => {
                      navigate(`/report/${record.reportId}`);
                      setHistoryModalVisible(false);
                    }}
                  >
                    查看
                  </Button>
                )}
                <Popconfirm
                  title="确定删除此报告？"
                  onConfirm={() => handleDeleteReport(record.reportId)}
                >
                  <Button type="link" size="small" danger>
                    删除
                  </Button>
                </Popconfirm>
              </Space>
            ),
          },
        ]}
      />
    </Modal>
  );

  // 配置模态框
  const renderConfigModal = () => (
    <Modal
      title="报告设置"
      open={configModalVisible}
      onCancel={() => setConfigModalVisible(false)}
      onOk={handleSaveConfig}
      okText="保存"
      cancelText="取消"
    >
      <div style={{ marginBottom: 16 }}>
        <label>默认保存路径：</label>
        <Input.Group compact style={{ marginTop: 8 }}>
          <Input
            style={{ width: "calc(100% - 100px)" }}
            value={newSavePath}
            onChange={(e) => setNewSavePath(e.target.value)}
            placeholder="选择或输入保存路径"
          />
          <Button onClick={handleSelectSavePath}>
            <FolderOpenOutlined /> 选择
          </Button>
        </Input.Group>
      </div>
    </Modal>
  );

  // 代码查看器模态框
  const renderCodeViewerModal = () => (
    <Modal
      title={
        <span>
          <FileOutlined /> {currentFileContent?.fileName || "代码查看"}
        </span>
      }
      open={codeViewerVisible}
      onCancel={() => {
        setCodeViewerVisible(false);
        setCurrentFileContent(null);
        setHighlightLines(null);
      }}
      footer={null}
      width={1000}
      style={{ top: 20 }}
    >
      {codeLoading ? (
        <div style={{ textAlign: "center", padding: "50px" }}>
          <Spin size="large" />
          <p style={{ marginTop: 16 }}>加载文件内容...</p>
        </div>
      ) : currentFileContent ? (
        <div>
          <div style={{ marginBottom: 16 }}>
            <Tag color="blue">{currentFileContent.totalLines} 行</Tag>
            <Tooltip title={currentFileContent.filePath}>
              <Tag>{currentFileContent.filePath}</Tag>
            </Tooltip>
            {highlightLines && (
              <Tag color="orange">
                定位: L{highlightLines.start} - L{highlightLines.end}
              </Tag>
            )}
          </div>
          <div
            ref={codeViewerRef}
            style={{
              backgroundColor: "#f5f5f5",
              borderRadius: 4,
              padding: 16,
              maxHeight: "70vh",
              overflow: "auto",
              fontFamily: "Monaco, Menlo, 'Ubuntu Mono', monospace",
              fontSize: 13,
              lineHeight: 1.6,
            }}
          >
            {currentFileContent.lines?.map((line) => {
              const isHighlighted =
                highlightLines &&
                line.lineNumber >= highlightLines.start &&
                line.lineNumber <= highlightLines.end;
              return (
                <div
                  key={line.lineNumber}
                  data-line-number={line.lineNumber}
                  style={{
                    display: "flex",
                    backgroundColor: isHighlighted ? "#fff3cd" : "transparent",
                    borderLeft: isHighlighted
                      ? "3px solid #faad14"
                      : "3px solid transparent",
                    paddingLeft: isHighlighted ? 13 : 16,
                    marginLeft: -16,
                    marginRight: -16,
                    paddingRight: 16,
                  }}
                >
                  <div
                    style={{
                      minWidth: 50,
                      paddingRight: 16,
                      textAlign: "right",
                      color: isHighlighted ? "#faad14" : "#999",
                      userSelect: "none",
                      fontWeight: isHighlighted ? "bold" : "normal",
                    }}
                  >
                    {line.lineNumber}
                  </div>
                  <div style={{ flex: 1, whiteSpace: "pre" }}>
                    {line.content || " "}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      ) : null}
    </Modal>
  );

  // Commit Diff 查看器模态框
  const renderDiffModal = () => (
    <Modal
      title={
        <span>
          <HistoryOutlined /> 提交变更详情
          {currentCommitInfo && (
            <Tag color="blue" style={{ marginLeft: 8 }}>
              {currentCommitInfo.shortHash}
            </Tag>
          )}
        </span>
      }
      open={diffModalVisible}
      onCancel={() => {
        setDiffModalVisible(false);
        setCurrentCommitDiff("");
        setCurrentCommitInfo(null);
      }}
      footer={null}
      width={1000}
      style={{ top: 20 }}
    >
      {diffLoading ? (
        <div style={{ textAlign: "center", padding: "50px" }}>
          <Spin size="large" />
          <p style={{ marginTop: 16 }}>加载提交变更...</p>
        </div>
      ) : (
        <div>
          {currentCommitInfo && (
            <div style={{ marginBottom: 16 }}>
              <Space wrap>
                <Tag color="blue">作者: {currentCommitInfo.authorName}</Tag>
                <Tag>
                  时间:{" "}
                  {new Date(currentCommitInfo.commitDate).toLocaleString()}
                </Tag>
                <Tag color="green">+{currentCommitInfo.linesAdded} 行</Tag>
                <Tag color="red">-{currentCommitInfo.linesDeleted} 行</Tag>
              </Space>
              {currentCommitInfo.commitMessage && (
                <Alert
                  message="提交信息"
                  description={currentCommitInfo.commitMessage}
                  type="info"
                  style={{ marginTop: 12 }}
                  showIcon
                />
              )}
            </div>
          )}
          <div
            style={{
              backgroundColor: "#1e1e1e",
              borderRadius: 4,
              padding: 16,
              maxHeight: "60vh",
              overflow: "auto",
              fontFamily: "Monaco, Menlo, 'Ubuntu Mono', monospace",
              fontSize: 12,
              lineHeight: 1.5,
              color: "#d4d4d4",
            }}
          >
            {currentCommitDiff.split("\n").map((line, index) => {
              let lineColor = "inherit";
              let bgColor = "transparent";
              if (line.startsWith("+") && !line.startsWith("+++")) {
                lineColor = "#4caf50";
                bgColor = "rgba(76, 175, 80, 0.1)";
              } else if (line.startsWith("-") && !line.startsWith("---")) {
                lineColor = "#f44336";
                bgColor = "rgba(244, 67, 54, 0.1)";
              } else if (line.startsWith("@@")) {
                lineColor = "#2196f3";
                bgColor = "rgba(33, 150, 243, 0.1)";
              } else if (line.startsWith("diff --git")) {
                lineColor = "#ff9800";
              }
              return (
                <div
                  key={index}
                  style={{
                    color: lineColor,
                    backgroundColor: bgColor,
                    whiteSpace: "pre",
                    padding: "1px 0",
                  }}
                >
                  {line || " "}
                </div>
              );
            })}
          </div>
        </div>
      )}
    </Modal>
  );

  // Tab项
  const tabItems = [
    {
      key: "overview",
      label: "概览",
      icon: <FileOutlined />,
      children: (
        <div>
          {renderStatistics()}
          <Card title="未覆盖的方法" style={{ marginTop: 16 }}>
            {renderUncoveredMethods()}
          </Card>
        </div>
      ),
    },
    {
      key: "files",
      label: "文件统计",
      icon: <FileOutlined />,
      children: renderFileStatistics(),
    },
    {
      key: "methods",
      label: "方法统计",
      icon: <FunctionOutlined />,
      children: renderMethodStatistics(),
    },
    {
      key: "commits",
      label: "提交统计",
      icon: <HistoryOutlined />,
      children: (
        <div>
          {renderDeveloperStats()}
          <div style={{ marginTop: 16 }}>{renderCommitStatistics()}</div>
        </div>
      ),
    },
  ];

  return (
    <div className="report-page">
      <div className="report-header">
        <div style={{ display: "flex", alignItems: "center", gap: 16 }}>
          <Button icon={<ArrowLeftOutlined />} onClick={handleGoBack}>
            返回首页
          </Button>
          <h1 style={{ margin: 0 }}>测试覆盖分析报告</h1>
        </div>
        <div className="header-info">
          <p>
            <strong>项目：</strong>
            {result.projectInfo?.projectName || "Unknown"}
          </p>
          <p>
            <strong>分析时间：</strong>
            {new Date(result.analysisTime).toLocaleString()}
          </p>
        </div>
        <Space>
          <Button icon={<HistoryIcon />} onClick={handleViewHistory}>
            历史报告
          </Button>
          <Button
            icon={<SettingOutlined />}
            onClick={() => setConfigModalVisible(true)}
          >
            设置
          </Button>
          <Button icon={<SaveOutlined />} onClick={handleSaveReport}>
            保存报告
          </Button>
          <Button
            type="primary"
            icon={<DownloadOutlined />}
            onClick={handleExportReport}
          >
            导出HTML
          </Button>
        </Space>
      </div>

      <Tabs items={tabItems} />

      {renderHistoryModal()}
      {renderConfigModal()}
      {renderCodeViewerModal()}
      {renderDiffModal()}
    </div>
  );
}

export default ReportPage;
