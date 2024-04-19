# starmie-framework
一些工作中总结的,或是自己实现的功能 :
1. core : 基于泛型的 base model/mapper/service/manager定义,自定义查询条件组装类criterion,以及一些工具类.
2. utils : 通用的Excel导入/导出, PDF导出工具.
3. boot : 基于springboot, 线程池自动组装配置(autoconfiguration)类, 和启动banner设置类
4. app : web/service相关工具类,提供通用的增/删/改/查基础定义和实现
5. generator :  基于MyBatis Generator + 模板{Thymeleaf(已实现)/Freemarker(接口)}的 代码生成命令行工具,可实现 model/mapper/service/manager/controller/javascript等java或JS/html页面文件的自动生成.

后续计划 : 
1. 增加基于 javaFX + springboot 实现的 Generator UI
2. 增加基于 starmie-framework 和 generator 实现的 demo (springboot + springMVC + MyBatis + mysql + vue2 + vuetify2 )
3. 增加更多的自动配置(autoconfiguration)实现
