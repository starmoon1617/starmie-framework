# Copilot Instructions for Starmie Framework

## Build Commands

```bash
# Full build (skip tests by default)
mvn clean install

# Build specific module
mvn clean install -pl starmie-core/starmie-core-base -am

# Run with tests (tests skipped by default in pom.xml)
mvn clean install -DskipTests=false

# Run a single test class
mvn test -DskipTests=false -Dtest=ClassName -pl module-path

# Generate javadocs
mvn javadoc:jar
```

## Technology Stack

- **Java 25**, Maven multi-module project
- **Spring Boot 4.0.0** / Spring Framework 7.0.2
- **MyBatis 3.5.19** for data access
- **Apache POI 5.5.1** (Excel), **PDFBox 3.0.6** (PDF)
- **Freemarker 2.3.34** / **Thymeleaf 3.1.3** for templating

## Architecture Overview

### Module Structure

```
starmie-framework/
├── starmie-core/           # Base abstractions
│   ├── starmie-core-base      # BaseEntity, Base, BaseDto, BaseResult
│   ├── starmie-core-common    # BaseCriteria query builder, enums
│   └── starmie-core-service   # BaseMapper, BaseService, BaseManager
├── starmie-app/            # Web layer
│   ├── starmie-app-base       # BaseController with response helpers
│   └── starmie-app-web        # Web utilities
├── starmie-boot/           # Spring Boot auto-configuration
│   ├── starmie-boot-executor     # Thread pool auto-config
│   ├── starmie-boot-executor-vt  # Virtual thread executor (Java 21+)
│   └── starmie-boot-banner       # Startup banner
├── starmie-utils/          # Document utilities
│   ├── starmie-utils-poi      # Excel import/export
│   ├── starmie-utils-pdf      # PDF generation
│   └── starmie-utils-doc      # General document utilities
└── starmie-generator/      # Code generation (MyBatis Generator + templates)
    ├── starmie-generator-core       # Core generation logic
    ├── starmie-generator-freemarker # Freemarker template support
    └── starmie-generator-thymeleaf  # Thymeleaf template support
```

### Generic Type Pattern (3-Layer Stack)

The core uses a consistent 3-generic-parameter pattern throughout:

```java
// E = Entity type (extends BaseEntity<ID, U>)
// ID = Primary key type (Serializable)  
// U = Audit user type (Serializable)

BaseMapper<E, ID, U>           // Data access layer
BaseService<E, ID, U>          // Service layer (delegates to Mapper)
BaseServiceImpl<E, ID, U>      // Service implementation
BaseManager<E, ID, U>          // Business layer with @Transactional
BaseManagerImpl<E, ID, U>      // Manager implementation (delegates to Service)
```

**Entity hierarchy:**
```java
Base<U>                        // Audit fields: createTime, updateTime, createBy, updateBy
  └── BaseEntity<ID, U>        // Adds: id field
```

### Query Building with BaseCriteria

Use `BaseCriteria` for fluent query construction:

```java
BaseCriteria criteria = new BaseCriteria()
    .addEqual("status", 1)
    .addLike("name", "test")
    .addCriterion(...)
    .addSortCriterion(new SortCriterion("createTime", SortType.DESC));
criteria.setOffset(0);
criteria.setLimit(10);
```

### REST Response Pattern

All REST responses use `BaseDto<D>` wrapper:

```java
public class BaseController {
    protected <D> BaseDto<D> getSuccess(D data);
    protected <D> BaseDto<D> getFailure(String message);
    protected BaseCriteria getCriteria(HttpServletRequest request);
}
```

## Key Conventions

### Transaction Management

- `@Transactional` is applied at the **Manager layer** (not Service)
- Manager methods use `propagation = REQUIRED`
- Batch operations (e.g., `save(Collection<E>)`) are in Manager layer

### Auto-Configuration Properties

Thread pool executors:
```properties
starmie.executor.names=task,io~3           # Creates: taskExor, io0Exor, io1Exor, io2Exor
starmie.executor.default.coreSize=2        # Global defaults
starmie.executor.task.coreSize=5           # Per-executor config
```

Virtual thread executors (Java 21+):
```properties
starmie.executor.vt.names=async
```

Startup banner:
```properties
starmie.boot.startup.banner=starmieBanner.txt
```

### Code Generator Pattern

Generators use Strategy pattern with these interfaces:
- `FieldGenerator` - generates field definitions
- `MethodGenerator` - generates method definitions  
- `GenericTypeGenerator` - generates generic type strings
- `TemplateGenerator` - processes templates (Freemarker/Thymeleaf implementations)

Data models: `JavaData`, `XmlData`, `TextData`, `FieldData`, `MethodData`

### Naming Conventions

- Executor beans are named with `Exor` suffix (e.g., `taskExor`)
- Virtual thread executors use `-vt-` in thread names
- Auto-configuration classes registered via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
