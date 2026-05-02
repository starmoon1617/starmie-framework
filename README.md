# starmie-framework

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-25-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.starmoon1617/starmie)](https://central.sonatype.com/search?q=io.github.starmoon1617)

一个基于 Java 25 + Spring Boot 4 + MyBatis 的轻量级业务开发脚手架框架，提供：

1. **core** : 基于泛型的 Base Model / Mapper / Service / Manager 层定义，灵活的查询条件组装（`BaseCriteria`），以及常用工具类。
2. **utils** : 通用的 Excel 导入/导出（Apache POI）、PDF 导出（PDFBox）工具。
3. **boot** : 基于 Spring Boot AutoConfiguration 的线程池（普通 & 虚拟线程）自动注册，以及启动 Banner 配置。
4. **app** : Web 层基础控制器，提供开箱即用的增/删/改/查 + Excel 导入导出 REST 接口。
5. **generator** : 基于 MyBatis Generator + 模板引擎（Thymeleaf / FreeMarker）的代码生成命令行工具，可自动生成 Model / Mapper / Service / Manager / Controller / JavaScript / HTML 等文件。

---

## 目录

- [技术栈](#技术栈)
- [模块结构](#模块结构)
- [快速开始](#快速开始)
- [核心模块 (starmie-core)](#核心模块-starmie-core)
  - [实体层次结构](#实体层次结构)
  - [查询条件 BaseCriteria](#查询条件-basecriteria)
  - [分页 Pagination](#分页-pagination)
  - [Mapper / Service / Manager 层](#mapper--service--manager-层)
  - [工具类](#工具类)
- [Web 层 (starmie-app)](#web-层-starmie-app)
  - [BaseController](#basecontroller)
  - [BaseGenericController (JSON REST API)](#basegenericcontroller-json-rest-api)
  - [BaseWebController (页面 + 文件导入导出)](#basewebcontroller-页面--文件导入导出)
  - [CriteriaUtils - 请求参数转查询条件](#criteriautils---请求参数转查询条件)
- [Spring Boot 自动配置 (starmie-boot)](#spring-boot-自动配置-starmie-boot)
  - [线程池自动注册 (starmie-boot-executor)](#线程池自动注册-starmie-boot-executor)
  - [虚拟线程自动注册 (starmie-boot-executor-vt)](#虚拟线程自动注册-starmie-boot-executor-vt)
  - [启动 Banner (starmie-boot-banner)](#启动-banner-starmie-boot-banner)
- [文档工具 (starmie-utils)](#文档工具-starmie-utils)
  - [Excel 导入导出 (starmie-utils-poi)](#excel-导入导出-starmie-utils-poi)
  - [PDF 导出 (starmie-utils-pdf)](#pdf-导出-starmie-utils-pdf)
- [代码生成器 (starmie-generator)](#代码生成器-starmie-generator)
- [构建与测试](#构建与测试)
- [后续计划](#后续计划)

---

## 技术栈

| 组件 | 版本 |
|------|------|
| Java | 25 |
| Spring Boot | 4.0.x |
| Spring Framework | 7.0.x |
| MyBatis | 3.5.19 |
| MyBatis-Spring | 4.0.0 |
| MyBatis Generator | 1.4.2 |
| Apache POI | 5.5.1 |
| Apache PDFBox | 3.0.6 |
| FreeMarker | 2.3.34 |
| Thymeleaf | 3.1.3 |
| SLF4J | 2.0.x |

---

## 模块结构

```
starmie-framework/
├── starmie-core/                    # 核心抽象层
│   ├── starmie-core-base            # Base / BaseEntity / BaseResult / BaseDto
│   ├── starmie-core-common          # BaseCriteria 查询组装、枚举、工具类、常量
│   └── starmie-core-service         # BaseMapper / BaseService / BaseManager 接口及默认实现
├── starmie-app/                     # Web 层
│   ├── starmie-app-base             # BaseController、CriteriaUtils、SessionUtils、枚举常量
│   └── starmie-app-web              # BaseGenericController (JSON API)、BaseWebController (页面+文件)
├── starmie-boot/                    # Spring Boot 自动配置
│   ├── starmie-boot-executor        # ThreadPoolTaskExecutor 自动注册
│   ├── starmie-boot-executor-vt     # SimpleAsyncTaskExecutor (虚拟线程) 自动注册
│   └── starmie-boot-banner          # 启动 Banner 监听器
├── starmie-utils/                   # 文档工具
│   ├── starmie-utils-doc            # 通用文档头定义 (DocHead)、字体常量、转换器接口
│   ├── starmie-utils-poi            # Excel 读写 (Apache POI SXSSF / Event 模式)
│   └── starmie-utils-pdf            # PDF 写入 (Apache PDFBox)
└── starmie-generator/               # 代码生成器
    ├── starmie-generator-core       # MBG 扩展、数据模型、生成器 API、ShellRunner 入口
    ├── starmie-generator-freemarker # FreeMarker 模板生成实现
    └── starmie-generator-thymeleaf  # Thymeleaf 模板生成实现
```

---

## 快速开始

### 在 Maven 项目中引入

```xml
<!-- 核心层 -->
<dependency>
    <groupId>io.github.starmoon1617</groupId>
    <artifactId>starmie-core-service</artifactId>
    <version>2.0.1</version>
</dependency>

<!-- Web 层（包含 BaseGenericController / BaseWebController） -->
<dependency>
    <groupId>io.github.starmoon1617</groupId>
    <artifactId>starmie-app-web</artifactId>
    <version>2.0.1</version>
</dependency>

<!-- 线程池自动配置 -->
<dependency>
    <groupId>io.github.starmoon1617</groupId>
    <artifactId>starmie-boot-executor</artifactId>
    <version>2.0.1</version>
</dependency>

<!-- Excel 工具 -->
<dependency>
    <groupId>io.github.starmoon1617</groupId>
    <artifactId>starmie-utils-poi</artifactId>
    <version>2.0.1</version>
</dependency>
```

### 定义实体

```java
public class User extends BaseEntity<Long, Long> {
    private String username;
    private String email;
    // getters / setters
}
```

### 定义 Mapper

```java
@Mapper
public interface UserMapper extends BaseMapper<User, Long, Long> {
    // 继承 select / insert / update / delete / selectList / count
}
```

### 定义 Service & Manager

```java
@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long, Long> {
    @Autowired
    private UserMapper userMapper;

    @Override
    protected BaseMapper<User, Long, Long> getMapper() {
        return userMapper;
    }
}

@Component
public class UserManagerImpl extends BaseManagerImpl<User, Long, Long> {
    @Autowired
    private UserServiceImpl userService;

    @Override
    protected BaseService<User, Long, Long> getService() {
        return userService;
    }
}
```

### 定义 Controller

```java
@RestController
@RequestMapping("/user")
public class UserController extends BaseGenericController<User, Long, Long> {
    @Autowired
    private UserManagerImpl userManager;

    @Override
    protected BaseManager<User, Long, Long> getManager() {
        return userManager;
    }
}
```

自动获得以下 REST 接口：

| 路径 | 方法 | 说明 |
|------|------|------|
| `POST /user/list` | POST | 分页列表查询 |
| `POST /user/detail` | POST | 单条详情 |
| `POST /user/save` | POST | 新增 |
| `POST /user/update` | POST | 更新 |
| `POST /user/delete` | POST | 删除 |
| `POST /user/batchImport` | POST | JSON 批量导入 |
| `POST /user/batchExport` | POST | JSON 批量导出 |

---

## 核心模块 (starmie-core)

### 实体层次结构

```
Serializable
  └── Base<U extends Serializable>          # 审计字段: createTime / updateTime / createBy / updateBy
        └── BaseEntity<ID, U>               # 主键字段: id
```

- **`Base<U>`** — 定义四个审计字段（创建时间、更新时间、创建人、更新人），泛型 `U` 为用户标识类型（通常为 `Long` 或 `String`）。
- **`BaseEntity<ID, U>`** — 继承 `Base<U>`，增加主键字段 `id`，泛型 `ID` 为主键类型（通常为 `Long`）。

#### 返回值模型

```
Serializable
  └── BaseResult          # code (int) + msg (String)
        └── BaseDto<D>    # 在 BaseResult 基础上增加泛型 data 字段
```

- **`BaseResult`** — 统一返回码（`code`）和消息（`msg`）。
- **`BaseDto<D>`** — 在 `BaseResult` 基础上增加泛型业务数据 `data`，用于所有 REST 接口的返回体。

---

### 查询条件 BaseCriteria

`BaseCriteria` 是框架的核心查询组装类（`final`，通过 `BaseCriteria.getInstance()` 创建），支持链式调用：

```java
BaseCriteria criteria = BaseCriteria.getInstance()
    .addEqual("status", 1)                              // WHERE status = 1
    .addLike("name", "test")                            // AND name LIKE '%test%'
    .addCriterion(OperatorType.GT, "createTime", date)  // AND create_time > ?
    .addSortCriterion("createTime", SortType.DESC)      // ORDER BY create_time DESC
    .addLimitation(LimitationType.LIMIT, 10)            // LIMIT 10
    .addLimitation(LimitationType.OFFSET, 0);           // OFFSET 0
```

#### 主要方法

| 方法 | 说明 |
|------|------|
| `addEqual(field, values...)` | 单值时生成 `=`，多值时生成 `IN` |
| `addLike(field, value)` | 生成 `LIKE '%value%'` |
| `addCriterion(operatorType, field, values...)` | 添加指定操作符的条件 |
| `addCriterion(combinaType, combinaName, operatorType, field, values...)` | 添加带组合分组的条件 |
| `addSortCriterion(field, sortType)` | 添加排序（字段名自动转为下划线格式） |
| `addSortCriterion(order, field, sortType)` | 带优先级的排序，数字小的先排 |
| `addLimitation(limitationType, value)` | 设置 LIMIT / OFFSET / END |
| `clear()` | 清除所有条件 |

#### 操作符枚举 (`OperatorType`)

`EQ(=)` / `NEQ(!=)` / `GT(>)` / `GTE(>=)` / `LT(<)` / `LTE(<=)` /
`IN` / `NIN(NOT IN)` / `ISN(IS NULL)` / `ISNN(IS NOT NULL)` / `BTW(BETWEEN)` /
`LK(LIKE '%x%')` / `RLKM(LIKE 'x%')` / `LLKM(LIKE '%x')` /
`SLKO(LIKE '_x_')` / `RLKO(LIKE 'x_')` / `LLKO(LIKE '_x')`

#### 组合条件 (`CombinaType`)

支持将多个条件分组并用括号包裹，实现复杂 SQL 的 `AND (...) OR (...)` 嵌套：

| 枚举值 | 说明 |
|--------|------|
| `NON_COMBINA` | 非组合，作为组合中最前的条件（无 AND/OR 前缀） |
| `NON_COMBINA_AND` | 非组合 AND 条件（默认） |
| `NON_COMBINA_OR` | 非组合 OR 条件 |
| `COMBINA_AND` | 组合内 AND 条件 |
| `COMBINA_OR` | 组合内 OR 条件 |
| `COMBINA_OUTER_AND` | 组合对外的连接为 AND，位于组合条件最前 |
| `COMBINA_OUTER_OR` | 组合对外的连接为 OR，位于组合条件最前 |
| `COMBINA` | 组合内 AND 条件（不带外部连接词） |

---

### 分页 Pagination

```java
// pageNo 从 0 开始
Pagination<User> pagination = new Pagination<>();
pagination.setPageNo(0);   // 当前页（从 0 起）
pagination.setPageSize(20); // 每页数量

manager.find(pagination, criteria);

// 返回结果
pagination.getElms();       // 当前页数据列表
pagination.getSize();        // 当前页数量
pagination.getTotal();       // 总记录数（仅第一页查询时返回）
pagination.getTotalPage();   // 总页数
pagination.isReturnTotal();  // 是否包含总数
```

> **注意**：`pageNo` 从 `0` 开始。只有 `pageNo == 0`（第一页）时才会查询 `count`，后续翻页不重复查询总数，以提升性能。

---

### Mapper / Service / Manager 层

框架采用三层泛型架构，统一泛型参数 `<E, ID, U>`：

```
BaseMapper<E, ID, U>       # MyBatis 数据访问接口
  ↑ 被调用
BaseService<E, ID, U>      # 服务接口（无事务）
BaseServiceImpl<E, ID, U>  # 服务实现，持有 BaseMapper，抽象方法 getMapper()
  ↑ 被调用
BaseManager<E, ID, U>      # 业务接口（含批量操作和分页）
BaseManagerImpl<E, ID, U>  # 业务实现，持有 BaseService，抽象方法 getService()
                           # 写操作标注 @Transactional(propagation=REQUIRED, rollbackFor=Exception.class)
```

#### BaseMapper 接口方法

| 方法 | 说明 |
|------|------|
| `E select(E e)` | 按实体查询单条 |
| `int insert(E e)` | 插入单条 |
| `int update(E e)` | 更新单条 |
| `int delete(E e)` | 按实体删除 |
| `List<E> selectList(BaseCriteria criteria)` | 按条件查询列表 |
| `int count(BaseCriteria criteria)` | 按条件计数 |

#### BaseManager 额外方法

| 方法 | 说明 |
|------|------|
| `int save(Collection<E> es)` | 批量保存（事务） |
| `void find(Pagination<E> pagination, BaseCriteria criteria)` | 分页查询 |

> **事务说明**：`@Transactional` 仅在 **Manager 层**使用，Service 层不加事务注解。

---

### 工具类

#### `CommonUtils`
字符串、集合、Map 工具：

| 方法 | 说明 |
|------|------|
| `isNotBlank(String)` | 判断字符串非空（委托 Spring `StringUtils.hasText`） |
| `isEmpty(Collection)` | 判断集合为空 |
| `isEmpty(Map)` | 判断 Map 为空 |
| `isEmpty(Object[])` | 判断数组为空 |
| `capitalize(String)` | 首字母大写 |
| `toUnderScore(String)` | 驼峰转下划线（如 `userName` → `user_name`） |
| `splitToList(String, String)` | 字符串按分隔符拆分为 List |

#### `DateUtils`
日期解析和格式化，支持多种格式字符串自动识别，以及当月时间戳范围查询等。

#### `EntityUtils`
基于反射和 Jackson 的对象操作工具：

| 方法 | 说明 |
|------|------|
| `copyProperties(source, target)` | 属性复制 |
| `getValue(obj, fieldName)` | 反射获取字段值 |
| `setValue(obj, fieldName, value)` | 反射设置字段值 |
| `toJson(obj)` | 对象序列化为 JSON 字符串 |
| `toNonNJson(obj)` | 序列化并忽略 null 字段 |
| `fromJson(json, Class)` | JSON 反序列化 |
| `fromJsonToList(json, Class)` | JSON 反序列化为 List |
| `toJavaType(type)` | 将 `java.lang.reflect.Type` 转换为 Jackson `JavaType` |

#### `IpUtils`
本机 IP 地址获取工具，支持多网卡环境，应用启动时静态初始化。

#### `DateJsonDeserializer`
Jackson 的自定义 `JsonDeserializer<Date>`，将 JSON 中的日期字符串自动解析为 `java.util.Date`。

---

## Web 层 (starmie-app)

### BaseController

所有控制器的基类，提供统一的响应构建方法：

```java
// 成功响应（code=0）
BaseDto<T> getSuccess(T data);

// 失败响应（自定义 code 和 msg）
BaseDto<T> getFailure(int code, String msg);

// 自定义响应
BaseDto<T> getResult(int code, String msg, T data);

// 从当前请求上下文获取查询条件
BaseCriteria getCriteria();
BaseCriteria getCriteria(HttpServletRequest request);
```

---

### BaseGenericController (JSON REST API)

继承 `BaseController`，提供标准 JSON REST CRUD 接口，子类只需实现 `getManager()`：

```java
@RestController
@RequestMapping("/xxx")
public class XxxController extends BaseGenericController<XxxEntity, Long, Long> {
    @Override
    protected BaseManager<XxxEntity, Long, Long> getManager() { ... }

    // 可覆盖以自定义导入数据校验
    @Override
    protected String validateImportDatas(List<XxxEntity> datas) { ... }
}
```

| 端点 | HTTP | 说明 |
|------|------|------|
| `/list` | POST | 分页查询，请求参数通过 `_FC*` / `_FS*` / `_FL*` 前缀传递 |
| `/detail` | POST | 单条详情，Body 为实体对象 |
| `/save` | POST | 新增，Body 为实体对象 |
| `/update` | POST | 更新，Body 为实体对象 |
| `/delete` | POST | 删除，Body 为实体对象 |
| `/batchImport` | POST | JSON 批量导入，Body 为实体列表 |
| `/batchExport` | POST | JSON 批量导出，返回分页结果 |

---

### BaseWebController (页面 + 文件导入导出)

继承 `BaseGenericController`，额外提供页面跳转和 Excel 文件导入导出，子类需实现 `getViewBasePath()`：

```java
public class XxxWebController extends BaseWebController<XxxEntity, Long, Long> {
    @Override
    protected String getViewBasePath() { return "/xxx"; } // 视图基础路径

    // 可覆盖: 为导出字段头添加自定义转换器
    @Override
    protected void addConverters(List<DocHead> heads) { ... }

    // 可覆盖: 自定义日期格式 (默认 DATETIME)
    @Override
    protected DateMode getDateMode() { return DateMode.DATE; }

    // 可覆盖: 提供 Excel 读取处理器
    @Override
    protected ExcelReadHandler<XxxEntity> getExcelReadHandler() { ... }
}
```

| 端点 | HTTP | 说明 |
|------|------|------|
| `/toList` | GET | 跳转到列表页面 |
| `/toAdd` | GET | 跳转到新增页面 |
| `/toEdit` | GET | 跳转到编辑页面，将实体数据放入 Model |
| `/toDelete` | GET | 跳转到删除确认页面 |
| `/export` | POST | Excel 文件导出，请求参数包含 `fileName`、`heads`（JSON 格式列头） |
| `/import` | POST | Excel 文件导入，`multipart/form-data`，字段名 `uploadFile` |

---

### CriteriaUtils - 请求参数转查询条件

`CriteriaUtils` 将 HTTP 请求参数自动解析为 `BaseCriteria`，前端通过约定格式的参数名传递查询条件：

#### 参数命名规则

```
_FC{组合类型}{组合名}_{数据类型}{操作符}_{字段名}_{表别名}
_FS{排序顺序}_{字段名}_{表别名}
_FL{限制类型}
```

**组合类型**（0-7）：
`0`=NON_COMBINA, `1`=NON_COMBINA_AND（默认）, `2`=NON_COMBINA_OR,
`3`=COMBINA_AND, `4`=COMBINA_OR, `5`=COMBINA_OUTER_AND, `6`=COMBINA_OUTER_OR, `7`=COMBINA

**数据类型**：
`I`=Integer, `L`=Long, `S`=String, `H`=Short, `D`=Date, `T`=DateTime, `B`=BigDecimal

**操作符**（0-16）：
`0`=EQ, `1`=NEQ, `2`=GT, `3`=GTE, `4`=LT, `5`=LTE,
`6`=IN, `7`=NOT IN, `8`=IS NULL, `9`=IS NOT NULL, `10`=BETWEEN,
`11`=LIKE '%x%', `12`=LIKE 'x_', `13`=LIKE '_x', `14`=LIKE '_x_', `15`=LIKE 'x%', `16`=LIKE '%x'

**排序类型**（0/1）：`0`=ASC, `1`=DESC

**限制类型**（0/1/2）：`0`=LIMIT, `1`=OFFSET, `2`=END

#### 示例

```
// 查询 status = 1（Integer）
_FC1_I0_status = 1

// 查询 name 模糊匹配（String，LIKE '%test%'）
_FC1_S11_name = test

// 按 create_time 倒序
_FS0_createTime = 1

// 每页 20 条，从第 0 条开始
_FL0 = 20
_FL1 = 0
```

> 字段名使用驼峰命名，框架自动转换为数据库下划线格式（如 `userName` → `user_name`）。

---

## Spring Boot 自动配置 (starmie-boot)

### 线程池自动注册 (starmie-boot-executor)

自动读取 `starmie.executor.*` 配置，将 `ThreadPoolTaskExecutor` 注册为 Spring Bean，无需手动 `@Bean`。

```properties
# 注册 taskExor 和 3 个 io0Exor/io1Exor/io2Exor 共 4 个线程池
starmie.executor.names=task,io~3

# 全局默认参数
starmie.executor.default.coreSize=2
starmie.executor.default.maxSize=10
starmie.executor.default.capacity=10000
starmie.executor.default.aliveSeconds=300
starmie.executor.default.awaitTerminationSeconds=600
starmie.executor.default.allowCoreThreadTimeOut=true
starmie.executor.default.waitForTasksToCompleteOnShutdown=true

# 针对单个执行器的覆盖配置
starmie.executor.task.coreSize=5
starmie.executor.task.maxSize=20
starmie.executor.task.rejectedHandler=myRejectedHandler     # 自定义拒绝策略 Bean 名
starmie.executor.task.taskDecorator=myTaskDecorator         # 自定义任务装饰器 Bean 名
starmie.executor.task.threadFactory=myThreadFactory         # 自定义线程工厂 Bean 名
```

注册后 Bean 名称规则：`{name}Exor`（如 `task` → `taskExor`），线程名前缀：`{name}-exor`。

> **`~N` 语法**：`io~3` 等价于 `io0,io1,io2`，批量生成同类线程池时使用。

**默认参数（与 `ThreadPoolTaskExecutor` 默认值的差异）：**

| 参数 | 框架默认值 | Spring 默认值 |
|------|-----------|--------------|
| `coreSize` | 1 | 1 |
| `maxSize` | 3 | `Integer.MAX_VALUE` |
| `queueCapacity` | 10000 | `Integer.MAX_VALUE` |
| `aliveSeconds` | 300 | 60 |
| `awaitTerminationSeconds` | 600 | 0 |
| `allowCoreThreadTimeOut` | **true** | false |
| `waitForTasksToCompleteOnShutdown` | **true** | false |

---

### 虚拟线程自动注册 (starmie-boot-executor-vt)

基于 Java 21+ 虚拟线程特性，自动注册 `SimpleAsyncTaskExecutor`（开启 `virtualThreads=true`）。

```properties
# 注册 asyncVtExor 虚拟线程执行器
starmie.executor.vt.names=async

# 全局默认配置
starmie.executor.vt.default.taskDecorator=myTaskDecorator
starmie.executor.vt.default.threadNamePrefix=my-vt-

# 单个执行器配置
starmie.executor.vt.async.threadNamePrefix=async-vt-
```

注册后 Bean 名称规则：`{name}VtExor`，线程名前缀默认：`{name}-vt-exor`。

---

### 启动 Banner (starmie-boot-banner)

应用启动完成后（`ApplicationReadyEvent`）打印 Banner。

```properties
# 指定 Banner 文件路径（classpath 或文件系统路径），默认查找 starmieBanner.txt
starmie.boot.startup.banner=starmieBanner.txt
```

- 如果找到配置的文件则打印文件内容；
- 否则打印内置的 Starmie ASCII Art Banner。

> 该模块不依赖 Spring Boot 的 `spring.banner.*` 配置，而是在应用就绪后独立打印。

---

## 文档工具 (starmie-utils)

### Excel 导入导出 (starmie-utils-poi)

基于 Apache POI，写入使用 **SXSSF**（流式，低内存占用），读取使用 **Event User Mode**（SAX 解析，适合大文件）。

#### 写出 Excel

```java
// 方式一：一次性写出
List<DocHead> heads = List.of(
    new DocHead("姓名", 120, "name", null),
    new DocHead("创建时间", 160, "createTime", new DateConverter("yyyy-MM-dd"))
);
ExcelWriter.WriteToExcel(outputStream, "Sheet1", heads, dataList, DateMode.DATETIME);

// 方式二：分批写出（适合大数据量导出）
ExcelWriteHandler<User> handler = ExcelWriter.buildExcelWriteHandler("Sheet1", heads, DateMode.DATETIME);
ExcelWriter.writeDatas(handler, page1);
ExcelWriter.writeDatas(handler, page2);
ExcelWriter.flush(outputStream, handler);
```

#### 读入 Excel

```java
// 列头顺序对应实体字段名（驼峰）
List<String> fieldNames = List.of("username", "email", "createTime");
List<User> users = ExcelReader.read(inputStream, fieldNames, User.class);

// 带行监听器（可用于逐行处理、数据校验）
List<User> users = ExcelReader.read(inputStream, fieldNames, User.class, rowData -> {
    // 自定义行后处理逻辑
});
```

#### `DocHead` 字段头定义

| 字段 | 说明 |
|------|------|
| `title` | 列标题（表头显示文字） |
| `width` | 列宽（单位：字符宽度 × 256） |
| `field` | 对应实体字段名（驼峰命名） |
| `converter` | 值转换器（实现 `Converter<T>` 接口），如 `DateConverter` |

---

### PDF 导出 (starmie-utils-pdf)

基于 Apache PDFBox，支持自定义字体、页面尺寸、方向以及印章图片。

```java
// 一次性写出
PdfWriter.WriteToPdf(outputStream, "标题", heads, dataList,
    headFontInputStream,    // 标题字体流（如仿宋.ttf）
    textFontInputStream,    // 正文字体流
    sealConf                // 印章配置（可为 null）
);

// 分批写出
PdfWriteHandler<User> handler = PdfWriter.buildPdfWriteHandler(
    "标题", heads, headFontIs, textFontIs, sealConf);
PdfWriter.writeDatas(handler, page1);
PdfWriter.flush(outputStream, handler);
```

#### 页面与印章配置

```java
// 页面配置（通过 PageConf 设置）
PageConf pageConf = new PageConf();
// 页面类型（A4/A3 等）: PageType 枚举
// 页面方向（横向/纵向）: PageOrientation 枚举

// 印章配置
SealConf sealConf = new SealConf();
// 印章类型: SealType 枚举（圆形、椭圆形等）
// 印章图片位置: SealImage
```

---

## 代码生成器 (starmie-generator)

基于 **MyBatis Generator 1.4.2** 扩展，通过属性文件驱动，支持 Thymeleaf 和 FreeMarker 模板引擎，可生成：

- Java：Model / Mapper / Service / ServiceImpl / Manager / ManagerImpl / Controller
- XML：MyBatis Mapper XML
- 文本：JavaScript / HTML / Vue 等前端文件

### 运行方式

```bash
java -cp starmie-generator-xxx.jar \
     io.github.starmoon1617.starmie.generator.core.api.ShellRunner \
     -configfile /path/to/generator.properties \
     -overwrite
```

**命令行参数：**

| 参数 | 说明 |
|------|------|
| `-configfile <file>` | 生成器属性配置文件路径（必填） |
| `-overwrite` | 覆盖已存在的文件 |
| `-contextids <ids>` | 仅运行指定 Context（逗号分隔） |
| `-tables <tables>` | 仅生成指定表（逗号分隔） |
| `-verbose` | 打印详细进度 |
| `-?` / `-h` | 打印帮助信息 |

### 生成器 API

框架通过 Strategy 模式提供扩展点：

| 接口/类 | 说明 |
|---------|------|
| `TemplateGenerator` | 模板处理抽象类，`process(template, datas)` 渲染模板 |
| `DataGenerator` | 数据生成接口，提供 `getImports()` 等 |
| `FieldGenerator` | 字段定义生成接口 |
| `MethodGenerator` | 方法定义生成接口 |
| `GenericTypeGenerator` | 泛型类型字符串生成接口 |
| `ModelFieldGenerator` | Model 字段生成器 |
| `ModelMethodGenerator` | Model 方法生成器 |

#### 数据模型

| 类 | 说明 |
|----|------|
| `JavaData` | Java 源文件数据（包名、类名、字段、方法等） |
| `XmlData` | XML 文件数据 |
| `TextData` | 文本/前端文件数据 |
| `FieldData` | 字段定义数据 |
| `MethodData` | 方法定义数据 |
| `ColumnData` | 数据库列信息 |
| `GenericTypeData` | 泛型类型数据 |

---

## 构建与测试

```bash
# 完整构建（默认跳过测试）
mvn clean install

# 构建指定模块
mvn clean install -pl starmie-core/starmie-core-base -am

# 运行测试
mvn clean install -DskipTests=false

# 运行单个测试类
mvn test -DskipTests=false -Dtest=ClassName -pl module-path

# 生成 Javadoc
mvn javadoc:jar
```

---

## 后续计划

1. 增加基于 **JavaFX + Spring Boot** 实现的 Generator UI 界面。
2. 增加基于 starmie-framework 和 generator 实现的完整 **Demo 项目**（Spring Boot + Spring MVC + MyBatis + MySQL + Vue2 + Vuetify2）。
3. 增加更多 **Spring Boot AutoConfiguration** 实现（如缓存、消息队列等）。

---

## License

[MIT License](LICENSE) © 2023 Nathan Liao
