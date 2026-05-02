# Architecture Overview — Starmie Framework

> 面向开发者的框架架构说明文档

---

## 1. 整体层次结构

```
HTTP Request
     │
     ▼
┌─────────────────────────┐
│    BaseController        │  starmie-app-base
│  BaseGenericController   │  提供统一 REST 响应包装 (BaseDto<D>)
│   BaseWebController      │  starmie-app-web / 提供导入导出
└────────────┬────────────┘
             │ @Autowired / 依赖注入
             ▼
┌─────────────────────────┐
│     BaseManager<E,ID,U> │  starmie-core-service
│    BaseManagerImpl       │  @Transactional 事务边界在此层
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│     BaseService<E,ID,U> │  starmie-core-service
│    BaseServiceImpl       │  无事务，委托给 Mapper
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│     BaseMapper<E,ID,U>  │  starmie-core-service
│     (MyBatis Mapper)     │  MyBatis 数据访问
└─────────────────────────┘
```

---

## 2. 泛型三元组规则

整个框架使用统一的三参数泛型模式：

```
E  = 实体类型，必须继承 BaseEntity<ID, U>
ID = 主键类型，必须实现 Serializable
U  = 审计用户类型，必须实现 Serializable（如 String、Long）
```

示例：

```java
// 用户实体
public class User extends BaseEntity<Long, String> { ... }

// Mapper
@Mapper
public interface UserMapper extends BaseMapper<User, Long, String> { }

// Service
public interface UserService extends BaseService<User, Long, String> { }

// Manager
@Component
public class UserManager extends BaseManagerImpl<User, Long, String> {
    @Autowired
    private UserService userService;
    
    @Override
    protected BaseService<User, Long, String> getService() {
        return userService;
    }
}
```

---

## 3. 实体继承体系

```
Serializable
    └── Base<U>                   // 四个审计字段
            ├── createTime        // 创建时间
            ├── updateTime        // 更新时间
            ├── createBy          // 创建人（类型为 U）
            └── updateBy          // 修改人（类型为 U）
                  └── BaseEntity<ID, U>
                            └── id  // 主键（类型为 ID）
```

---

## 4. 查询构建器 — BaseCriteria

`BaseCriteria` 是 `final` 类，通过静态工厂方法创建：

```java
BaseCriteria criteria = BaseCriteria.getInstance()
    .addEqual("status", 1)
    .addLike("name", "张")
    .addGreaterThan("age", 18)
    .addBetween("createTime", start, end)
    .addSortCriterion(new SortCriterion("createTime", SortType.DESC));
criteria.setOffset(0);
criteria.setLimit(20);
```

### 支持的操作符（OperatorType）

| 枚举值 | SQL | 说明 |
|--------|-----|------|
| `EQ` | `=` | 等于 |
| `NEQ` | `!=` | 不等于 |
| `GT` | `>` | 大于 |
| `GTE` | `>=` | 大于等于 |
| `LT` | `<` | 小于 |
| `LTE` | `<=` | 小于等于 |
| `LK` | `LIKE '%X%'` | 全模糊 |
| `RLKM` | `LIKE 'X%'` | 右模糊（前缀匹配） |
| `LLKM` | `LIKE '%X'` | 左模糊（后缀匹配） |
| `RLKO` | `LIKE 'X_'` | 右通配（单字符） |
| `LLKO` | `LIKE '_X'` | 左通配（单字符） |
| `SLKO` | `LIKE '_X_'` | 两侧通配 |
| `IN` | `IN (...)` | 包含 |
| `NIN` | `NOT IN (...)` | 不包含 |
| `ISN` | `IS NULL` | 为空 |
| `ISNN` | `IS NOT NULL` | 不为空 |
| `BTW` | `BETWEEN ... AND ...` | 区间 |

---

## 5. 分页机制

```java
// pageNo 从 0 开始，第 0 页同时查询 count
Pagination<User> page = new Pagination<>();
page.setPageNo(0);    // 第一页 → 触发 count 查询
page.setPageSize(20);

userManager.find(page, criteria);

System.out.println(page.getTotal());     // 总记录数
System.out.println(page.getTotalPage()); // 总页数
System.out.println(page.getElms());      // 当前页数据
```

> ⚠️ **注意**：只有 `pageNo == 0` 时才执行 `count` 查询以获取总数，后续翻页不重复统计。

---

## 6. REST 响应规范

所有 Controller 方法应通过 `BaseController` 提供的辅助方法返回 `BaseDto<D>`：

```java
// 成功响应
return getSuccess(data);           // code=200, data=data
return getSuccess();               // code=200, data=null

// 失败响应
return getFailure("错误原因");     // code=500, msg=...
return getFailure(ResultCode.xxx); // 使用枚举 code
```

`BaseDto<D>` 结构：
```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

---

## 7. Web 导入导出

`BaseWebController` 提供两个模板方法，子类实现具体逻辑：

```java
// 导出 Excel
@GetMapping("/export")
public void export(HttpServletRequest request, HttpServletResponse response) {
    doExport(request, response);
}

// 必须实现：返回导出数据和列定义
@Override
protected ExportResult<User> getExportData(HttpServletRequest request) {
    List<User> users = userManager.find(getCriteria(request));
    List<DocHead> heads = List.of(
        DocHead.of("name", "姓名", 100),
        DocHead.of("email", "邮箱", 150)
    );
    return ExportResult.of(users, heads);
}

// 导入 Excel  
@PostMapping("/import")
public BaseDto<Void> importData(HttpServletRequest request) {
    return doImport(request);
}

// 必须实现：处理导入的每行数据
@Override
protected void handleImportRow(int rowNum, User user) {
    userManager.save(user);
}
```

---

## 8. 线程池自动配置

### 普通线程池

```properties
# 创建 taskExor, io0Exor, io1Exor, io2Exor 四个线程池
starmie.executor.names=task,io~3

# 全局默认值
starmie.executor.default.coreSize=2
starmie.executor.default.maxSize=10
starmie.executor.default.queueSize=500

# 单独配置 task 线程池
starmie.executor.task.coreSize=5
starmie.executor.task.maxSize=20
```

Bean 名称规则：配置名 + `Exor` 后缀（`io~3` 展开为 `io0Exor`、`io1Exor`、`io2Exor`）。

### 虚拟线程池（Java 21+）

```properties
starmie.executor.vt.names=async
# 注册 asyncExor，使用 Virtual Thread per task executor
```

---

## 9. 代码生成器使用

生成器基于 MyBatis Generator 扩展，支持 Freemarker 和 Thymeleaf 两种模板引擎。

```xml
<!-- generatorConfig.xml 片段 -->
<context id="context" targetRuntime="MyBatis3">
    <plugin type="io.github.starmoon1617.starmie.generator.core.plugin.StarmiePlugin">
        <property name="templateType" value="freemarker"/>
        <property name="templatePath" value="templates/"/>
    </plugin>
    ...
</context>
```

运行命令：
```bash
mvn mybatis-generator:generate
```

### 生成器 Strategy Pattern

```
FieldGenerator     → 生成字段定义
MethodGenerator    → 生成方法定义
GenericTypeGenerator → 生成泛型类型字符串
TemplateGenerator  → 处理模板（Freemarker / Thymeleaf 实现）
```

数据模型：`JavaData`, `XmlData`, `TextData`, `FieldData`, `MethodData`
