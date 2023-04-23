# Simple Neo4j

目前使用的项目框架是自研的, 集成不了Spring Data Neo4j, 日常开发中手写cql非常的浪费时间, 因此本人模仿SDN手写一套简单的单节点CURD, Page, Sort等基础功能的小工具

## 相关类介绍

com.wenjin.neo4j    

├── annotation                              // 相关注解

├── bean                                    // 实体类

├── component                               // orm相关组件

├── config                                  // 配置

│       └── BeanPostProcessor               // 系统启动时, 创建对象注入到容器里时拦截对象解析注解相关的数据

├── context                                 // 对象映射时需要用到的上下文数据

│       └── Neo4jEntityInformation          // 存储实体的相关信息

│       └── Neo4jMappingContext             // neo4j映射对象上下文

│       └── Neo4jPersistentEntity           // 实体数据

│       └── Property                        // 实体字段数据

├── repository                              

│       └── AnimalRepository                // 测试repository

├── util                                    // 工具类

│       └── CypherGenerator                 // 组装Cypher语句

├──pom.xml                // 依赖



!! 注意: 此工具暂不可直接引入使用, 主要是执行cql的driver使用不同, 分页类也不同, 可根据自身项目自行调整即可, 若启动起来没有生效, BeanPostProcessor打断点检查有没有解析到数据

