package com.coveragetool.analyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.coveragetool.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码分析器 - 分析Java源码和测试代码
 * 
 * 这个类使用JavaParser库解析Java源文件，提取类信息、方法信息等。
 * 主要功能：
 * 1. 识别业务类（Service、Controller、Repository等）
 * 2. 识别测试类（包含@Test注解的类）
 * 3. 提取方法信息（方法名、参数、返回值等）
 */
public class CodeAnalyzer {
    
    /**
     * 分析业务类列表
     * 
     * 遍历所有源码目录，找出所有的业务类并提取其信息。
     * 
     * @param sourceDirs 源码目录列表（相对路径）
     * @param projectPath 项目根路径
     * @return 业务类列表
     */
    public List<BusinessClass> analyzeBusinessClasses(List<String> sourceDirs, String projectPath) {
        List<BusinessClass> businessClasses = new ArrayList<>();
        
        // 遍历每个源码目录
        for (String sourceDir : sourceDirs) {
            // 构建完整的目录路径
            String fullPath = projectPath + java.io.File.separator + sourceDir;
            java.io.File dir = new java.io.File(fullPath);
            
            // 如果目录存在，扫描其中的Java文件
            if (dir.exists() && dir.isDirectory()) {
                scanBusinessClassesInDirectory(dir, projectPath, businessClasses);
            }
        }
        
        return businessClasses;
    }
    
    /**
     * 扫描目录中的业务类
     * 
     * 递归扫描目录，找出所有Java文件并解析为业务类。
     * 
     * @param directory 要扫描的目录
     * @param projectPath 项目根路径
     * @param businessClasses 用于收集业务类的列表
     */
    private void scanBusinessClassesInDirectory(java.io.File directory, String projectPath, 
                                                List<BusinessClass> businessClasses) {
        // 获取目录下的所有文件
        java.io.File[] files = directory.listFiles();
        
        if (files == null) {
            return;
        }
        
        // 遍历所有文件
        for (java.io.File file : files) {
            if (file.isDirectory()) {
                // 递归扫描子目录
                scanBusinessClassesInDirectory(file, projectPath, businessClasses);
            } else if (file.getName().endsWith(".java")) {
                // 解析Java文件
                BusinessClass businessClass = parseBusinessClass(file, projectPath);
                if (businessClass != null) {
                    businessClasses.add(businessClass);
                }
            }
        }
    }
    
    /**
     * 解析单个Java文件为业务类
     * 
     * 使用JavaParser解析Java文件，提取类名、包名、方法等信息。
     * 
     * @param javaFile Java文件
     * @param projectPath 项目根路径
     * @return 解析后的业务类对象，如果解析失败返回null
     */
    private BusinessClass parseBusinessClass(java.io.File javaFile, String projectPath) {
        try {
            // 使用JavaParser解析Java文件（新版本API）
            FileInputStream in = new FileInputStream(javaFile);
            JavaParser javaParser = new JavaParser();
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            in.close();
            
            // 检查解析是否成功
            if (!parseResult.isSuccessful()) {
                System.err.println("解析文件失败: " + javaFile.getAbsolutePath());
                return null;
            }
            
            CompilationUnit cu = parseResult.getResult().orElse(null);
            if (cu == null) {
                return null;
            }
            
            // 创建业务类对象
            BusinessClass businessClass = new BusinessClass();
            
            // 设置文件路径
            businessClass.setFilePath(javaFile.getAbsolutePath());
            
            // 提取简单类名（从文件名）
            String fileName = javaFile.getName();
            String className = fileName.substring(0, fileName.length() - 5); // 去掉.java后缀
            businessClass.setClassName(className);
            
            // 提取包名
            cu.getPackageDeclaration().ifPresent(pkg -> {
                businessClass.setPackageName(pkg.getNameAsString());
                // 构建完整限定名：包名.类名
                businessClass.setFullyQualifiedName(pkg.getNameAsString() + "." + className);
            });
            
            // 如果没有包名，完整限定名就是类名
            if (businessClass.getFullyQualifiedName() == null) {
                businessClass.setFullyQualifiedName(className);
            }
            
            // 识别类类型
            ClassType classType = ClassType.fromClassName(className);
            businessClass.setClassType(classType);
            
            // 推测对应的测试类名（按照命名约定）
            String testClassName = className + "Test";
            if (businessClass.getPackageName() != null) {
                businessClass.setCorrespondingTestClass(
                    businessClass.getPackageName() + "." + testClassName);
            } else {
                businessClass.setCorrespondingTestClass(testClassName);
            }
            
            // 提取所有公共方法
            List<Method> methods = extractMethods(cu, className);
            businessClass.setMethods(methods);
            
            return businessClass;
            
        } catch (Exception e) {
            // 解析失败，记录错误并返回null
            System.err.println("解析文件失败: " + javaFile.getAbsolutePath() + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 从编译单元中提取方法
     * 
     * 遍历类中的所有方法声明，提取方法信息。
     * 
     * @param cu 编译单元（解析后的Java文件）
     * @param className 类名
     * @return 方法列表
     */
    private List<Method> extractMethods(CompilationUnit cu, String className) {
        List<Method> methods = new ArrayList<>();
        
        // 使用访问者模式遍历所有方法声明
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration md, Void arg) {
                super.visit(md, arg);
                
                // 只提取公共方法（public）
                if (md.isPublic()) {
                    // 创建方法对象
                    Method method = new Method();
                    
                    // 设置方法名
                    method.setMethodName(md.getNameAsString());
                    
                    // 设置返回类型
                    // MethodDeclaration的getType()返回Type，需要检查是否存在
                    if (md.getType() != null) {
                        method.setReturnType(md.getType().toString());
                    } else {
                        method.setReturnType("void");
                    }
                    
                    // 设置参数列表
                    List<String> parameters = new ArrayList<>();
                    md.getParameters().forEach(param -> {
                        // 参数格式：类型
                        parameters.add(param.getTypeAsString());
                    });
                    method.setParameters(parameters);
                    
                    // 生成方法签名
                    method.setSignature(generateMethodSignature(md.getNameAsString(), parameters));
                    
                    // 设置所属类名
                    method.setBelongingClassName(className);
                    
                    // 设置行号信息
                    md.getBegin().ifPresent(begin -> method.setStartLineNumber(begin.line));
                    md.getEnd().ifPresent(end -> method.setEndLineNumber(end.line));
                    
                    // 计算方法复杂度（简单的圈复杂度估算）
                    int complexity = calculateComplexity(md);
                    method.setComplexity(complexity);
                    
                    methods.add(method);
                }
            }
        }, null);
        
        return methods;
    }
    
    /**
     * 生成方法签名
     * 
     * @param methodName 方法名
     * @param parameters 参数类型列表
     * @return 方法签名字符串
     */
    private String generateMethodSignature(String methodName, List<String> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(methodName).append("(");
        
        if (parameters != null && !parameters.isEmpty()) {
            sb.append(String.join(", ", parameters));
        }
        
        sb.append(")");
        return sb.toString();
    }
    
    /**
     * 计算方法的圈复杂度
     * 
     * 圈复杂度是衡量代码复杂度的指标，通过计算独立路径数量得出。
     * 这里使用简化的计算方法：初始值为1，每个if/for/while/case增加1。
     * 
     * @param md 方法声明
     * @return 圈复杂度值
     */
    private int calculateComplexity(MethodDeclaration md) {
        // 初始复杂度为1（方法本身）
        final int[] complexity = {1};
        
        // 遍历方法体中的所有语句
        md.getBody().ifPresent(body -> {
            body.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(com.github.javaparser.ast.stmt.IfStmt n, Void arg) {
                    super.visit(n, arg);
                    complexity[0]++; // if语句增加复杂度
                }
                
                @Override
                public void visit(com.github.javaparser.ast.stmt.ForStmt n, Void arg) {
                    super.visit(n, arg);
                    complexity[0]++; // for循环增加复杂度
                }
                
                @Override
                public void visit(com.github.javaparser.ast.stmt.WhileStmt n, Void arg) {
                    super.visit(n, arg);
                    complexity[0]++; // while循环增加复杂度
                }
                
                @Override
                public void visit(com.github.javaparser.ast.stmt.SwitchStmt n, Void arg) {
                    super.visit(n, arg);
                    // switch语句的每个case增加复杂度
                    complexity[0] += n.getEntries().size();
                }
                
                @Override
                public void visit(com.github.javaparser.ast.stmt.DoStmt n, Void arg) {
                    super.visit(n, arg);
                    complexity[0]++; // do-while循环增加复杂度
                }
                
                @Override
                public void visit(com.github.javaparser.ast.expr.ConditionalExpr n, Void arg) {
                    super.visit(n, arg);
                    complexity[0]++; // 三元运算符增加复杂度
                }
                
                @Override
                public void visit(com.github.javaparser.ast.stmt.CatchClause n, Void arg) {
                    super.visit(n, arg);
                    complexity[0]++; // catch块增加复杂度
                }
            }, null);
        });
        
        return complexity[0];
    }
    
    /**
     * 分析测试类列表
     * 
     * 遍历所有测试目录，找出所有的测试类并提取其信息。
     * 
     * @param testDirs 测试目录列表（相对路径）
     * @param projectPath 项目根路径
     * @return 测试类列表
     */
    public List<TestClass> analyzeTestClasses(List<String> testDirs, String projectPath) {
        List<TestClass> testClasses = new ArrayList<>();
        
        // 遍历每个测试目录
        for (String testDir : testDirs) {
            String fullPath = projectPath + java.io.File.separator + testDir;
            java.io.File dir = new java.io.File(fullPath);
            
            if (dir.exists() && dir.isDirectory()) {
                scanTestClassesInDirectory(dir, projectPath, testClasses);
            }
        }
        
        return testClasses;
    }
    
    /**
     * 扫描目录中的测试类
     * 
     * @param directory 要扫描的目录
     * @param projectPath 项目根路径
     * @param testClasses 用于收集测试类的列表
     */
    private void scanTestClassesInDirectory(java.io.File directory, String projectPath,
                                           List<TestClass> testClasses) {
        java.io.File[] files = directory.listFiles();
        
        if (files == null) {
            return;
        }
        
        for (java.io.File file : files) {
            if (file.isDirectory()) {
                scanTestClassesInDirectory(file, projectPath, testClasses);
            } else if (file.getName().endsWith(".java")) {
                TestClass testClass = parseTestClass(file, projectPath);
                if (testClass != null) {
                    testClasses.add(testClass);
                }
            }
        }
    }
    
    /**
     * 解析单个Java文件为测试类
     * 
     * @param javaFile Java文件
     * @param projectPath 项目根路径
     * @return 解析后的测试类对象，如果不是测试文件则返回null
     */
    private TestClass parseTestClass(java.io.File javaFile, String projectPath) {
        try {
            FileInputStream in = new FileInputStream(javaFile);
            JavaParser javaParser = new JavaParser();
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            in.close();
            
            // 检查解析是否成功
            if (!parseResult.isSuccessful()) {
                return null;
            }
            
            CompilationUnit cu = parseResult.getResult().orElse(null);
            if (cu == null) {
                return null;
            }
            
            // 检查是否包含测试方法（通过注解判断）
            TestFramework framework = detectTestFramework(cu);
            if (framework == TestFramework.UNKNOWN) {
                return null; // 不是测试文件
            }
            
            // 创建测试类对象
            TestClass testClass = new TestClass();
            
            // 设置文件路径
            testClass.setFilePath(javaFile.getAbsolutePath());
            
            // 提取类名
            String fileName = javaFile.getName();
            String className = fileName.substring(0, fileName.length() - 5);
            testClass.setClassName(className);
            
            // 提取包名
            cu.getPackageDeclaration().ifPresent(pkg -> {
                testClass.setPackageName(pkg.getNameAsString());
                testClass.setFullyQualifiedName(pkg.getNameAsString() + "." + className);
            });
            
            if (testClass.getFullyQualifiedName() == null) {
                testClass.setFullyQualifiedName(className);
            }
            
            // 设置测试框架
            testClass.setTestFramework(framework);
            
            // 推测对应的业务类名
            String businessClassName = className;
            if (className.endsWith("Test")) {
                businessClassName = className.substring(0, className.length() - 4);
            }
            if (testClass.getPackageName() != null) {
                testClass.setCorrespondingBusinessClass(
                    testClass.getPackageName() + "." + businessClassName);
            } else {
                testClass.setCorrespondingBusinessClass(businessClassName);
            }
            
            // 提取所有测试方法
            List<TestMethod> testMethods = extractTestMethods(cu, className, framework);
            testClass.setTestMethods(testMethods);
            
            return testClass;
            
        } catch (Exception e) {
            System.err.println("解析测试文件失败: " + javaFile.getAbsolutePath() + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 检测测试框架类型
     * 
     * 通过检查导入的包来判断使用的测试框架。
     * 
     * @param cu 编译单元
     * @return 测试框架类型
     */
    private TestFramework detectTestFramework(CompilationUnit cu) {
        // 检查导入语句
        for (com.github.javaparser.ast.ImportDeclaration imp : cu.getImports()) {
            String importName = imp.getNameAsString();
            
            // JUnit 5
            if (importName.startsWith("org.junit.jupiter.api")) {
                return TestFramework.JUNIT5;
            }
            
            // JUnit 4
            if (importName.startsWith("org.junit.") && !importName.startsWith("org.junit.jupiter")) {
                return TestFramework.JUNIT4;
            }
            
            // TestNG
            if (importName.startsWith("org.testng")) {
                return TestFramework.TESTNG;
            }
        }
        
        return TestFramework.UNKNOWN;
    }
    
    /**
     * 提取测试方法
     * 
     * @param cu 编译单元
     * @param className 类名
     * @param framework 测试框架
     * @return 测试方法列表
     */
    private List<TestMethod> extractTestMethods(CompilationUnit cu, String className,
                                                TestFramework framework) {
        List<TestMethod> testMethods = new ArrayList<>();
        
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration md, Void arg) {
                super.visit(md, arg);
                
                // 检查是否有@Test注解
                boolean isTestMethod = md.getAnnotations().stream()
                    .anyMatch(annotation -> {
                        String annotationName = annotation.getNameAsString();
                        return annotationName.equals("Test") || annotationName.equals("org.junit.Test");
                    });
                
                if (isTestMethod) {
                    TestMethod testMethod = new TestMethod();
                    
                    // 设置方法名
                    testMethod.setMethodName(md.getNameAsString());
                    
                    // 设置所属测试类
                    testMethod.setBelongingTestClass(className);
                    
                    // 推测测试的业务方法名
                    String testedMethod = extractTestedMethodName(md.getNameAsString());
                    testMethod.setTestedBusinessMethod(testedMethod);
                    
                    // 统计断言数量
                    int assertionCount = countAssertions(md);
                    testMethod.setAssertionCount(assertionCount);
                    
                    // 检查是否有边界值测试（通过方法名判断）
                    boolean hasBoundary = md.getNameAsString().toLowerCase().contains("boundary") ||
                                         md.getNameAsString().toLowerCase().contains("edge") ||
                                         md.getNameAsString().toLowerCase().contains("limit");
                    testMethod.setHasBoundaryTests(hasBoundary);
                    
                    // 检查是否有异常测试（通过方法名或异常注解判断）
                    boolean hasException = md.getNameAsString().toLowerCase().contains("exception") ||
                                          md.getNameAsString().toLowerCase().contains("error") ||
                                          md.getAnnotations().stream()
                                              .anyMatch(a -> a.getNameAsString().equals("ExpectedException"));
                    testMethod.setHasExceptionTests(hasException);
                    
                    // 检查是否使用Mock（通过方法体中的mock调用判断）
                    boolean usesMocks = checkForMocks(md);
                    testMethod.setUsesMocks(usesMocks);
                    
                    // 计算代码行数
                    int lines = md.toString().split("\n").length;
                    testMethod.setLinesOfCode(lines);
                    
                    testMethods.add(testMethod);
                }
            }
        }, null);
        
        return testMethods;
    }
    
    /**
     * 从测试方法名中提取被测试的业务方法名
     * 
     * 常见的测试方法命名模式：
     * - testMethodName
     * - testMethodName_Success
     * - methodName_Success
     * - should_DoSomething_When_Condition
     * 
     * @param testMethodName 测试方法名
     * @return 推测的业务方法名
     */
    private String extractTestedMethodName(String testMethodName) {
        String methodName = testMethodName;
        
        // 如果以test开头，去掉test前缀
        if (methodName.startsWith("test")) {
            methodName = methodName.substring(4);
        }
        
        // 如果包含下划线，取下划线前的部分
        int underscoreIndex = methodName.indexOf("_");
        if (underscoreIndex > 0) {
            methodName = methodName.substring(0, underscoreIndex);
        }
        
        // 如果包含should/when，返回空（无法准确推测）
        if (methodName.toLowerCase().contains("should") || 
            methodName.toLowerCase().contains("when")) {
            return "";
        }
        
        // 首字母小写（Java方法命名规范）
        if (methodName.length() > 0) {
            methodName = Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
        }
        
        return methodName;
    }
    
    /**
     * 统计测试方法中的断言数量
     * 
     * 通过查找常见的断言方法调用来统计。
     * 
     * @param md 方法声明
     * @return 断言数量
     */
    private int countAssertions(MethodDeclaration md) {
        final int[] count = {0};
        
        md.getBody().ifPresent(body -> {
            body.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(com.github.javaparser.ast.expr.MethodCallExpr n, Void arg) {
                    super.visit(n, arg);
                    
                    String methodName = n.getNameAsString();
                    // 常见的断言方法
                    if (methodName.startsWith("assert") || 
                        methodName.equals("assertTrue") ||
                        methodName.equals("assertFalse") ||
                        methodName.equals("assertNull") ||
                        methodName.equals("assertNotNull") ||
                        methodName.equals("assertEquals") ||
                        methodName.equals("assertNotEquals") ||
                        methodName.startsWith("verify")) {
                        count[0]++;
                    }
                }
            }, null);
        });
        
        return count[0];
    }
    
    /**
     * 检查测试方法是否使用了Mock
     * 
     * @param md 方法声明
     * @return 是否使用Mock
     */
    private boolean checkForMocks(MethodDeclaration md) {
        final boolean[] usesMocks = {false};
        
        // 检查方法上的注解
        for (com.github.javaparser.ast.expr.AnnotationExpr annotation : md.getAnnotations()) {
            String annotationName = annotation.getNameAsString();
            if (annotationName.equals("Mock") ||
                annotationName.equals("MockBean") ||
                annotationName.equals("InjectMocks")) {
                usesMocks[0] = true;
                break;
            }
        }
        
        // 检查方法体中的mock调用
        if (!usesMocks[0]) {
            md.getBody().ifPresent(body -> {
                body.accept(new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(com.github.javaparser.ast.expr.MethodCallExpr n, Void arg) {
                        super.visit(n, arg);
                        
                        String methodCallName = n.getNameAsString();
                        // 常见的Mock方法
                        if (methodCallName.equals("mock") ||
                            methodCallName.equals("when") ||
                            methodCallName.equals("given") ||
                            methodCallName.equals("doReturn") ||
                            methodCallName.equals("doThrow")) {
                            usesMocks[0] = true;
                        }
                    }
                }, null);
            });
        }
        
        return usesMocks[0];
    }
}
