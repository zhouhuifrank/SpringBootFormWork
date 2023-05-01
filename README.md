# SpringBootFormWork
SpringBoot项目模板,整合了常用组件和框架以及增删改查示例代码,可以在此基础上快速开发

# 模板整体功能
+ SpringBoot 2.7.10
+ Spring MVC
+ Mysql 8.x
+ Druid数据源
+ Mybatis
+ Mybatis-Plus
+ Spring Session Redis 分布式登录
+ Spring Data Reids 缓存
+ Spring Data ElasticSearch 搜索引擎
+ Spring AOP
+ HuTool 工具类
+ Lombok 注解
+ Swagger + Knife4j 接口文档
+ Redis工具类封装
+ 全局请求拦截器(性能监控) AOP切面示例
+ 全局异常处理类
+ 通用对象
+ 通用返回类
+ 错误码
+ 用户登录、注册、注销以及用户管理增删改查代码
+ 原生Mybatis xml代码 Mybatis-Plus QueryWrapper代码
+ sql建表语句
+ 单元测试 
+ 接口文档地址:localhost:8080/open-api/doc.html 可以在线调试

# 模板分包说明
+ annotation 自定义注解
+ aop AOP切面编程
+ common 通用功能
  + constant 常量
  + enums 枚举类
  + exception 自定义异常
  + interceptor 拦截器
  + util 工具类
+ config 配置类
+ controller 控制器处理请求地址映射
+ es  ElasticSearch搜索方法
+ manager service通用能力下沉
+ mapper sql方法
+ model 数据模型
  + dto 请求类
  + entity 数据库实体类
  + excel EasyExcel的Listener和excelDTO
  + vo 响应类
+ redis redis工具类封装
+ service 业务逻辑层
+ task 定时任务方法
+ wxmp 微信公众号对接