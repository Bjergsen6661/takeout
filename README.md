# takeout

#### 项目介绍

- 使用SSM框架搭建，实现CURD功能
- 简单的外卖系统，包括前台用户下单系统和后台管理系统。前台用户下单系统包括菜单展示、购物车、个人地址信息等模块；后台管理系统包括员工管理、分类管理、菜品管理、套餐管理和订单明细模块。
- 前台使用短信验证码方式进行登录，后台使用账号密码方式进行登录验证。

#### 技术选型

- 核心框架：Spring Boot、MyBatis-Plus
- 数据库：Mysql
- 数据库连接池：Druid
- 缓存：Redis、Spring Cache
- 前端技术：Vue.js、Element
- 第三方服务：腾讯云短信服务

#### 环境搭建

##### 开发工具

| 工具                   | 版本     | 官网                                                         |
| ---------------------- | -------- | ------------------------------------------------------------ |
| IDEA                   | 2020.3.x | [https://www.jetbrains.com/idea/download](https://gitee.com/link?target=https%3A%2F%2Fwww.jetbrains.com%2Fidea%2Fdownload) |
| Typora                 | 0.11.13  | [https://typora.io/](https://gitee.com/link?target=https%3A%2F%2Ftypora.io%2F) |
| SQLyog                 | 11.2.5   | [https://sqlyog.en.softonic.com/](https://gitee.com/link?target=https%3A%2F%2Fsqlyog.en.softonic.com%2F) |
| VMware Workstation Pro | 15.5.1   | https://www.vmware.com/                                      |

##### 开发环境

| 工具  | 版本号        | 下载                                                         |
| ----- | ------------- | ------------------------------------------------------------ |
| JDK   | 13(8以上即可) | https://www.oracle.com/java/technologies/downloads/          |
| Mysql | 8.0.24        | [https://www.mysql.com/](https://gitee.com/link?target=https%3A%2F%2Fwww.mysql.com%2F) |
| Redis | 6.2.6         | [https://redis.io/download](https://gitee.com/link?target=https%3A%2F%2Fredis.io%2Fdownload) |

#### 项目演示

##### 前台用户下单系统

##### 后台管理系统

#### 参与贡献

1.  新建 master、v2.0、v3.0_读写分离分支
    - master：主分支，最终项目保存模型
    - v2.0：主要用于编辑项目，经过检查后合并入master分支
    - v3.0_读写分离，独立分支，用于实现读写分离功能
2.  提交代码
