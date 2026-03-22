# Agent 并行协作方案

## 1. 目标

通过明确模块边界和文件归属，把客户端、服务端、数据库、测试、部署文档拆成可并行推进的任务包，减少上下文膨胀和多人冲突，提高交付速度与质量。

## 2. 总原则

- 每个 Agent 只负责一个清晰边界的模块
- 不允许多个 Agent 同时修改同一核心文件
- 公共接口、数据库结构先冻结，再进入并行开发
- 每轮先并行收敛结果，再开始下一轮

## 3. 建议的 Agent 分工

## 3.1 基础层 Agent

### Agent A：前端 H5 骨架

负责范围：

- `client-h5` 工程初始化
- 路由、布局、请求封装、状态管理
- 登录页与基础壳

文件归属：

- `client-h5/src/router/**`
- `client-h5/src/stores/**`
- `client-h5/src/layouts/**`
- `client-h5/src/utils/request.ts`

### Agent B：管理后台骨架

负责范围：

- `admin-web` 工程初始化
- 菜单、路由、登录、权限基础结构

文件归属：

- `admin-web/src/router/**`
- `admin-web/src/layouts/**`
- `admin-web/src/stores/**`
- `admin-web/src/utils/request.ts`

### Agent C：服务端骨架与认证

负责范围：

- `server` 工程初始化
- 统一响应、异常处理
- 登录认证、权限基础能力

文件归属：

- `server/src/main/java/**/common/**`
- `server/src/main/java/**/security/**`
- `server/src/main/java/**/modules/auth/**`

### Agent D：数据库与初始化脚本

负责范围：

- 建库脚本
- 核心表结构
- 初始化字典数据

文件归属：

- `db/migrations/**`
- `db/init/**`
- `db/seed/**`

## 3.2 业务层 Agent

### Agent E：H5 回收登记功能

负责范围：

- 回收登记页
- 图片上传组件接入
- 我的回收记录页

文件归属：

- `client-h5/src/views/recycle/**`
- `client-h5/src/views/records/**`
- `client-h5/src/components/upload/**`

### Agent F：服务端回收记录与上传

负责范围：

- 文件上传接口
- 回收记录新增、查询、审核、作废
- 积分计算触发

文件归属：

- `server/src/main/java/**/modules/recycle/**`
- `server/src/main/java/**/modules/file/**`

### Agent G：后台基础数据管理

负责范围：

- 项目、班组、工人管理
- 材料分类管理
- 积分规则管理

文件归属：

- `admin-web/src/views/project/**`
- `admin-web/src/views/team/**`
- `admin-web/src/views/worker/**`
- `admin-web/src/views/material/**`
- `admin-web/src/views/rule/**`

### Agent H：服务端基础业务数据

负责范围：

- 项目、班组、工人、材料、积分规则接口

文件归属：

- `server/src/main/java/**/modules/project/**`
- `server/src/main/java/**/modules/worker/**`
- `server/src/main/java/**/modules/material/**`
- `server/src/main/java/**/modules/pointrule/**`

## 3.3 积分与兑换 Agent

### Agent I：H5 积分与兑换页面

负责范围：

- 我的积分页
- 积分流水页
- 商品列表与兑换申请页

文件归属：

- `client-h5/src/views/points/**`
- `client-h5/src/views/goods/**`

### Agent J：服务端积分账户与兑换

负责范围：

- 积分账户
- 积分流水
- 兑换申请
- 扣分逻辑

文件归属：

- `server/src/main/java/**/modules/point/**`
- `server/src/main/java/**/modules/goods/**`
- `server/src/main/java/**/modules/redeem/**`

### Agent K：后台兑换审核与报表

负责范围：

- 商品管理
- 兑换审核
- 基础报表页

文件归属：

- `admin-web/src/views/goods/**`
- `admin-web/src/views/redeem/**`
- `admin-web/src/views/report/**`

## 3.4 联调与质量 Agent

### Agent L：接口联调与测试

负责范围：

- 接口联调
- 接口测试脚本
- 关键路径回归测试

文件归属：

- `api-examples/**`
- `server/src/test/**`
- `testcases/**`

### Agent M：部署与运维文档

负责范围：

- 本地启动文档
- 测试环境部署文档
- Nginx 配置样例
- Docker 化预案

文件归属：

- `ops/**`
- 根目录部署文档

## 4. 推荐并行节奏

## 第一轮

- Agent A
- Agent B
- Agent C
- Agent D

前置冻结项：

- 技术栈
- 仓库结构
- 统一返回结构
- 数据库命名规范

## 第二轮

- Agent E
- Agent F
- Agent G
- Agent H

前置冻结项：

- 基础表结构
- 登录方式
- 上传接口协议

## 第三轮

- Agent I
- Agent J
- Agent K

前置冻结项：

- 积分规则
- 兑换流程
- 商品数据结构

## 第四轮

- Agent L
- Agent M

前置冻结项：

- 核心功能联调完成
- 环境变量约定完成

## 5. 避免冲突的具体办法

### 5.1 文件所有权

- 公共配置文件由专门 Agent 负责
- 业务页面按目录归属到单一 Agent
- 服务端模块按领域目录归属到单一 Agent

### 5.2 接口先行

- 先定义接口文档
- 再由前后端并行实现
- 接口变更必须同步更新文档

### 5.3 数据库先行

- 核心表先评审
- 评审后冻结主字段
- 结构变更通过 migration 管理

## 6. 每轮交付要求

- 必须附带改动说明
- 必须说明文件范围
- 必须说明是否影响其他模块
- 必须提供最小验证方法

## 7. 当前最适合的执行顺序

1. 先让 Agent A/B/C/D 建立基础骨架
2. 再让 Agent E/F/G/H 并行做回收主流程
3. 然后让 Agent I/J/K 做积分与兑换
4. 最后由 Agent L/M 做联调、测试与部署文档

## 8. 结论

只要严格执行“按模块分治、按目录归属、按轮次冻结前置条件”的方式，这个项目非常适合并行开发，而且能有效控制上下文大小和冲突风险。
