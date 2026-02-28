package com.coveragetool.coverage;

import com.coveragetool.model.*;
import java.util.*;

/**
 * 覆盖度分析器 - 分析业务方法的测试覆盖情况
 * 
 * 这个类负责匹配业务方法和测试方法，计算覆盖率，评估测试粒度。
 * 主要功能：
 * 1. 匹配业务方法与测试方法
 * 2. 计算覆盖率
 * 3. 识别未覆盖的方法
 * 4. 评估测试粒度
 */
public class CoverageAnalyzer {
    
    /**
     * 测试粒度评估器
     */
    private TestGranularityEvaluator granularityEvaluator;
    
    /**
     * 构造函数
     */
    public CoverageAnalyzer() {
        this.granularityEvaluator = new TestGranularityEvaluator();
    }
    
    /**
     * 分析覆盖率
     * 
     * 这是主要的分析方法，接收业务类和测试类列表，返回覆盖率报告。
     * 
     * @param businessClasses 业务类列表
     * @param testClasses 测试类列表
     * @return 覆盖率报告
     */
    public CoverageReport analyzeCoverage(List<BusinessClass> businessClasses,
                                         List<TestClass> testClasses) {
        CoverageReport report = new CoverageReport();
        
        // 统计总方法数
        int totalMethods = 0;
        int coveredMethods = 0;
        
        // 存储未覆盖的方法
        List<MethodCoverage> uncoveredMethodList = new ArrayList<>();
        
        // 存储所有方法（用于方法级报告）
        List<MethodCoverage> allMethodsList = new ArrayList<>();
        
        // 存储文件级统计
        List<FileStatistics> fileStatisticsList = new ArrayList<>();
        
        // 粒度分布统计
        Map<GranularityLevel, Integer> granularityDistribution = new HashMap<>();
        granularityDistribution.put(GranularityLevel.EXCELLENT, 0);
        granularityDistribution.put(GranularityLevel.GOOD, 0);
        granularityDistribution.put(GranularityLevel.ACCEPTABLE, 0);
        granularityDistribution.put(GranularityLevel.POOR, 0);
        
        int totalGranularityScore = 0;
        int evaluatedMethodCount = 0;
        
        // 遍历所有业务类
        for (BusinessClass businessClass : businessClasses) {
            // 找到对应的测试类
            TestClass correspondingTestClass = findCorrespondingTestClass(
                businessClass, testClasses);
            
            int classCoveredMethods = 0;
            
            // 创建文件统计对象
            FileStatistics fileStats = createFileStatistics(businessClass, correspondingTestClass);
            
            // 当前类的方法列表（每个类单独维护）
            List<MethodCoverage> currentClassMethods = new ArrayList<>();
            
            // 遍历业务类中的所有方法
            for (Method method : businessClass.getMethods()) {
                totalMethods++;
                
                // 查找覆盖该方法的测试方法
                List<TestMethod> coveringTests = findCoveringTestMethods(
                    method, correspondingTestClass);
                
                // 创建方法覆盖信息对象
                MethodCoverage methodCoverage = createMethodCoverage(method, businessClass);
                
                if (!coveringTests.isEmpty()) {
                    // 方法被覆盖
                    method.setCovered(true);
                    method.setCoveringTestMethods(coveringTests);
                    coveredMethods++;
                    classCoveredMethods++;
                    
                    methodCoverage.setCovered(true);
                    methodCoverage.setTestMethodCount(coveringTests.size());
                    
                    // 评估测试粒度
                    GranularityLevel granularity = evaluateTestGranularity(method, coveringTests);
                    method.setTestGranularity(granularity);
                    methodCoverage.setGranularityLevel(granularity);
                    
                    // 更新粒度分布
                    granularityDistribution.put(granularity,
                        granularityDistribution.get(granularity) + 1);
                    
                    // 计算粒度分数
                    int score = granularityEvaluator.calculateScore(method, coveringTests);
                    totalGranularityScore += score;
                    evaluatedMethodCount++;
                    
                } else {
                    // 方法未被覆盖
                    method.setCovered(false);
                    method.setTestGranularity(GranularityLevel.POOR);
                    
                    methodCoverage.setCovered(false);
                    methodCoverage.setTestMethodCount(0);
                    methodCoverage.setGranularityLevel(GranularityLevel.POOR);
                    
                    // 添加到未覆盖列表
                    uncoveredMethodList.add(methodCoverage);
                }
                
                // 添加到所有方法列表
                allMethodsList.add(methodCoverage);
                
                // 添加到当前类的方法列表
                currentClassMethods.add(methodCoverage);
            }
            
            // 更新文件统计
            fileStats.setTotalMethods(businessClass.getMethods().size());
            fileStats.setCoveredMethods(classCoveredMethods);
            fileStats.setUncoveredMethods(businessClass.getMethods().size() - classCoveredMethods);
            double fileCoverageRate = businessClass.getMethods().isEmpty() ? 0 :
                (double) classCoveredMethods / businessClass.getMethods().size() * 100;
            fileStats.setCoverageRate(fileCoverageRate);
            
            // 设置当前类的方法列表
            fileStats.setMethods(currentClassMethods);
            
            fileStatisticsList.add(fileStats);
            
            // 计算类的覆盖率
            double classCoverageRate = businessClass.getMethods().isEmpty() ? 0 :
                (double) classCoveredMethods / businessClass.getMethods().size() * 100;
            businessClass.setCoverageRate(classCoverageRate);
        }
        
        // 计算总体覆盖率
        double overallCoverage = totalMethods == 0 ? 0 :
            (double) coveredMethods / totalMethods * 100;
        report.setOverallCoverage(overallCoverage);
        
        // 设置统计信息
        report.setTotalBusinessClasses(businessClasses.size());
        report.setCoveredBusinessClasses((int) businessClasses.stream()
            .filter(bc -> bc.getCoverageRate() > 0)
            .count());
        report.setTotalTestClasses(testClasses.size());
        report.setTotalTestMethods(testClasses.stream()
            .mapToInt(tc -> tc.getTestMethods().size())
            .sum());
        report.setTotalMethods(totalMethods);
        report.setCoveredMethods(coveredMethods);
        report.setUncoveredMethods(totalMethods - coveredMethods);
        report.setUncoveredMethodList(uncoveredMethodList);
        report.setAllMethodsList(allMethodsList);
        report.setFileStatisticsList(fileStatisticsList);
        report.setGranularityDistribution(granularityDistribution);
        
        // 计算平均粒度分数
        double averageGranularityScore = evaluatedMethodCount == 0 ? 0 :
            (double) totalGranularityScore / evaluatedMethodCount;
        report.setAverageGranularityScore(averageGranularityScore);
        
        return report;
    }
    
    /**
     * 查找对应的测试类
     * 
     * @param businessClass 业务类
     * @param testClasses 测试类列表
     * @return 对应的测试类，如果未找到返回null
     */
    private TestClass findCorrespondingTestClass(BusinessClass businessClass,
                                                 List<TestClass> testClasses) {
        String expectedTestClassName = businessClass.getCorrespondingTestClass();
        
        return testClasses.stream()
            .filter(tc -> tc.getFullyQualifiedName().equals(expectedTestClassName))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 查找覆盖指定方法的测试方法
     * 
     * @param businessMethod 业务方法
     * @param testClass 测试类
     * @return 覆盖该方法的测试方法列表
     */
    private List<TestMethod> findCoveringTestMethods(Method businessMethod, TestClass testClass) {
        List<TestMethod> coveringTests = new ArrayList<>();
        
        if (testClass == null || testClass.getTestMethods() == null) {
            return coveringTests;
        }
        
        String businessMethodName = businessMethod.getMethodName().toLowerCase();
        
        // 遍历测试方法
        for (TestMethod testMethod : testClass.getTestMethods()) {
            // 检查测试方法是否测试该业务方法
            if (isTestMethodCoveringBusinessMethod(testMethod, businessMethodName)) {
                coveringTests.add(testMethod);
            }
        }
        
        return coveringTests;
    }
    
    /**
     * 判断测试方法是否覆盖业务方法
     * 
     * 通过方法名匹配来判断。匹配规则：
     * 1. 测试方法名包含业务方法名
     * 2. 测试方法的testedBusinessMethod属性等于业务方法名
     * 
     * @param testMethod 测试方法
     * @param businessMethodName 业务方法名（小写）
     * @return 是否覆盖
     */
    private boolean isTestMethodCoveringBusinessMethod(TestMethod testMethod,
                                                       String businessMethodName) {
        String testMethodName = testMethod.getMethodName().toLowerCase();
        String testedMethod = testMethod.getTestedBusinessMethod();
        
        // 规则1：测试方法名包含业务方法名
        if (testMethodName.contains(businessMethodName)) {
            return true;
        }
        
        // 规则2：testedBusinessMethod匹配
        if (testedMethod != null && !testedMethod.isEmpty()) {
            return testedMethod.toLowerCase().equals(businessMethodName);
        }
        
        return false;
    }
    
    /**
     * 评估测试粒度
     * 
     * @param businessMethod 业务方法
     * @param testMethods 覆盖该方法的测试方法列表
     * @return 粒度等级
     */
    private GranularityLevel evaluateTestGranularity(Method businessMethod,
                                                     List<TestMethod> testMethods) {
        // 使用测试粒度评估器计算分数
        int totalScore = 0;
        
        for (TestMethod testMethod : testMethods) {
            int score = granularityEvaluator.calculateScore(businessMethod, testMethod);
            totalScore += score;
        }
        
        // 计算平均分数
        int averageScore = testMethods.isEmpty() ? 0 : totalScore / testMethods.size();
        
        // 根据分数返回粒度等级
        return GranularityLevel.fromScore(averageScore);
    }
    
    /**
     * 创建方法覆盖信息对象
     * 
     * @param method 业务方法
     * @param businessClass 方法所属的业务类
     * @return 方法覆盖信息对象
     */
    private MethodCoverage createMethodCoverage(Method method, BusinessClass businessClass) {
        MethodCoverage coverage = new MethodCoverage();
        coverage.setClassName(method.getBelongingClassName());
        coverage.setMethodName(method.getMethodName());
        coverage.setSignature(method.getSignature());
        coverage.setComplexity(method.getComplexity());
        coverage.setLastModifiedCommit(method.getLastModifiedCommit());
        
        // 设置行号信息
        coverage.setStartLineNumber(method.getStartLineNumber());
        coverage.setEndLineNumber(method.getEndLineNumber());
        coverage.setLinesOfCode(method.getLinesOfCode());
        
        // 设置完整签名（包含返回类型）
        String fullSignature = method.getReturnType() + " " + method.getSignature();
        coverage.setFullSignature(fullSignature);
        
        // 设置文件路径
        coverage.setFilePath(businessClass.getFilePath());
        
        return coverage;
    }
    
    /**
     * 创建文件统计对象
     * 
     * @param businessClass 业务类
     * @param correspondingTestClass 对应的测试类
     * @return 文件统计对象
     */
    private FileStatistics createFileStatistics(BusinessClass businessClass, TestClass correspondingTestClass) {
        FileStatistics stats = new FileStatistics();
        stats.setFilePath(businessClass.getFilePath());
        stats.setClassName(businessClass.getClassName());
        stats.setFullyQualifiedName(businessClass.getFullyQualifiedName());
        stats.setPackageName(businessClass.getPackageName());
        stats.setClassType(businessClass.getClassType());
        stats.setCorrespondingTestClass(businessClass.getCorrespondingTestClass());
        stats.setHasTestClass(correspondingTestClass != null);
        
        return stats;
    }
}

/**
 * 测试粒度评估器 - 评估测试方法的测试质量
 */
class TestGranularityEvaluator {
    
    /**
     * 计算单个测试方法的粒度分数
     * 
     * @param businessMethod 业务方法
     * @param testMethod 单个测试方法
     * @return 粒度分数（0-100）
     */
    public int calculateScore(Method businessMethod, TestMethod testMethod) {
        return calculateSingleScore(businessMethod, testMethod);
    }
    
    /**
     * 计算测试方法的粒度分数（针对多个测试方法）
     * 
     * 评分维度：
     * 1. 命名规范（20分）
     * 2. 断言数量（30分）
     * 3. 边界值测试（25分）
     * 4. 异常测试（15分）
     * 5. Mock使用（10分）
     * 
     * @param businessMethod 业务方法
     * @param testMethods 测试方法列表
     * @return 粒度分数（0-100）
     */
    public int calculateScore(Method businessMethod, List<TestMethod> testMethods) {
        if (testMethods == null || testMethods.isEmpty()) {
            return 0;
        }
        
        int totalScore = 0;
        for (TestMethod testMethod : testMethods) {
            totalScore += calculateSingleScore(businessMethod, testMethod);
        }
        
        return totalScore / testMethods.size();
    }
    
    /**
     * 计算单个测试方法的粒度分数
     */
    private int calculateSingleScore(Method businessMethod, TestMethod testMethod) {
        int score = 0;
        
        // 1. 命名规范（20分）
        // 测试方法名应该包含业务方法名
        if (testMethod.getMethodName().toLowerCase()
            .contains(businessMethod.getMethodName().toLowerCase())) {
            score += 20;
        } else if (testMethod.getTestedBusinessMethod() != null &&
                   testMethod.getTestedBusinessMethod()
                       .equalsIgnoreCase(businessMethod.getMethodName())) {
            score += 15;
        }
        
        // 2. 断言数量（30分）
        // 至少需要3个断言才能获得满分
        int assertionCount = testMethod.getAssertionCount();
        if (assertionCount >= 3) {
            score += 30;
        } else if (assertionCount >= 2) {
            score += 20;
        } else if (assertionCount >= 1) {
            score += 10;
        }
        
        // 3. 边界值测试（25分）
        if (testMethod.isHasBoundaryTests()) {
            score += 25;
        }
        
        // 4. 异常测试（15分）
        if (testMethod.isHasExceptionTests()) {
            score += 15;
        }
        
        // 5. Mock使用（10分）
        // 如果方法复杂度高，使用Mock是好的实践
        if (testMethod.isUsesMocks() && businessMethod.getComplexity() > 3) {
            score += 10;
        } else if (testMethod.isUsesMocks()) {
            score += 5;
        }
        
        return score;
    }
}
