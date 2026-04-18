# TDD 测试计划与阶段用例

## 1. 目标

本文件用于固定当前项目后续补全时的测试驱动开发节奏。

原则：

1. 先写测试，再改实现
2. 先锁定业务规则，再补页面与交互细节
3. 每一阶段都要有可重复执行的自动化验证
4. 每完成一个阶段，就提交并推送到 GitHub

## 2. 当前优先级

结合当前代码、文档约定和已实现功能，第一批优先锁定以下风险点：

1. 兑换申请前置校验不完整
2. 回收积分规则兜底行为需要固定
3. Demo 试运行链路需要补安全与冒烟验证

## 3. 阶段拆分

### 阶段 1：兑换申请规则补齐

目标：先用测试固定兑换申请的正确业务边界。

自动化用例：

- `TC-EX-001`：积分不足时，工人提交兑换申请应直接失败，不能生成订单
- `TC-EX-002`：库存不足时，工人提交兑换申请应直接失败，不能生成订单
- `TC-EX-003`：积分和库存都满足时，订单可提交、审核、发放，并正确扣减积分与库存

对应文件：

- `server/src/test/java/com/gongdi/materialpoints/ExchangeOrderIntegrationTests.java`

### 阶段 2：回收积分规则与审核闭环

目标：固定回收登记和审核的关键规则，避免前端试用时因为边界条件失败。

自动化用例：

- `TC-RC-001`：回收记录至少要上传一张图片
- `TC-RC-002`：存在精确规则时，按精确规则计算积分
- `TC-RC-003`：缺少完好度专属规则时，允许回退到基础规则并按默认系数计算
- `TC-RC-004`：审核通过后必须写入积分账户与积分流水

对应文件：

- `server/src/test/java/com/gongdi/materialpoints/RecycleRecordIntegrationTests.java`

### 阶段 3：认证与 Demo 试运行保护

目标：让试运行环境既可演示，也不会因为鉴权处理过宽导致行为失真。

自动化用例：

- `TC-AU-001`：未登录或无效 Token 不能访问受保护接口
- `TC-AU-002`：工人只能看到自己的回收记录和兑换记录
- `TC-AU-003`：库管和管理员可以执行审核操作
- `TC-DEMO-001`：Demo 模式能加载演示数据并完成最小冒烟

建议文件：

- `server/src/test/java/com/gongdi/materialpoints/PermissionIntegrationTests.java`
- `server/src/test/java/com/gongdi/materialpoints/DemoSmokeIntegrationTests.java`

## 4. 执行顺序

建议按下面顺序推进：

1. 先让后端业务规则测试完整
2. 再按测试结果修服务端实现
3. 后续补页面级联调与浏览器验收

## 5. 当前说明

本轮先落地“阶段 1：兑换申请规则补齐”。
