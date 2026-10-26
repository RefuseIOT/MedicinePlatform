# PR 审查清单（架构 / 权限 / 接口）

> 使用人：2号（架构与权限），以及所有参与 review 的同学。
> 目标：每次 review 10 分钟内看完，问题说得具体，不卡与本次 PR 无关的事。
> 配套：`系统设计规格说明书.md`、`API接口规范.md`、`权限矩阵.md`。

---

## 1 审查顺序

1. **看 PR 描述**：改了什么、为什么改、怎么验证、有没有截图/接口示例。
2. **看文件列表**：有没有夹带无关文件、密钥、`target/`、`node_modules/`、`.env`。
3. **看契约**：接口/字段/权限矩阵有没有同步改。
4. **看代码**：Controller → Application → Domain → SQL，重点看权限与状态。
5. **看测试**：有没有覆盖新规则；至少有一条权限/状态用例。
6. **留评论**：按 `[blocking]` / `[suggestion]` / `[question]` 标注；最后给结论。

---

## 2 通用检查

- [ ] PR 标题能看懂（`feat:` / `fix:` / `docs:` / `test:` / `chore:`）。
- [ ] 描述写了「改了什么、为什么、怎么验证」。
- [ ] 没有提交 `.env`、密钥、token、真实患者数据、教师未公开材料。
- [ ] 没有提交 `target/`、`node_modules/`、`.idea/`、`dist/` 等生成物。
- [ ] 改动范围与 PR 描述一致，没有顺手重构无关代码。
- [ ] 新增/修改的文档能正常渲染（Markdown 表格、PlantUML 图）。
- [ ] 至少 1 名相关模块 owner 看过。

---

## 3 架构检查

- [ ] 新代码放在正确的模块/分层（interfaces / application / domain / infrastructure）。
- [ ] `domain` 没有 import Spring Web、MyBatis、Redis 等基础设施。
- [ ] 没有跨模块直接注入对方的 `Mapper`/`RepositoryImpl`。
- [ ] 跨模块调用走 application 接口或 query port，没有直接查对方的表。
- [ ] 没有在 Controller 里写业务规则/状态迁移。
- [ ] 没有为了一个页面需求引入新组件（MQ、Redis、Elasticsearch）而不说明理由。
- [ ] 新增外部依赖（pom/npm）说明了用途，版本与团队一致。
- [ ] 配置项走 `application.yml` / 环境变量，没有硬编码地址、密码、密钥。

---

## 4 接口契约检查

- [ ] 新接口已写入 `API接口规范.md` 的接口清单。
- [ ] 路径符合 `/api/v1/...`，资源命名是名词复数。
- [ ] 请求/响应字段是 lowerCamelCase，ID 是字符串。
- [ ] 金额是字符串、时间是 ISO 8601 带时区。
- [ ] 分页参数/响应结构统一（`items/page/size/total`）。
- [ ] 错误返回统一 `{code,message,data,traceId}`，HTTP 状态正确。
- [ ] 没有把 `role`、`doctorId`、`patientId` 作为可信请求参数。
- [ ] 破坏性变更升版本或前后端同 Sprint 同步，并在描述里写清影响。
- [ ] 新增枚举值考虑了前端兼容（前端不硬编码中文）。
- [ ] 接口示例可直接复制到 Postman/curl 跑通（至少登录后的一个）。

---

## 5 权限检查

- [ ] 新接口在 `权限矩阵.md` 中有登记，角色与矩阵一致。
- [ ] `@PreAuthorize` 只挡角色；归属与状态在 application/domain。
- [ ] 查询条件带 `patient_id` / `doctor_id`，不是先查全部再内存过滤。
- [ ] 资源不属于当前用户时返回 404，不是 403。
- [ ] 状态迁移走 `PrescriptionStateMachine` / `OrderStateMachine`，没有多个入口各自判断。
- [ ] 迁移请求带 `version`，SQL 带 `WHERE status=? AND version=?`。
- [ ] 并发失败返回 409，`data` 带当前状态/版本。
- [ ] 审方、核销、强制作废、账号启停写 `audit_log`。
- [ ] 日志没有打印密码、token、Authorization、完整手机号。
- [ ] 新增/更新了 SEC-xx 权限测试用例。

---

## 6 数据与事务检查

- [ ] 表有明确 owner 模块，没有跨模块写表。
- [ ] 状态更新、审方记录、审计日志在同一事务。
- [ ] 通知/消息在 `AFTER_COMMIT` 后发，避免回滚误发。
- [ ] 金额用 `BigDecimal` / `DECIMAL`，没有 `double`。
- [ ] 时间统一 `Asia/Shanghai`，没有混用 `Date`/`LocalDateTime`。
- [ ] 唯一约束（手机号、订单号、处方号）已建。
- [ ] 查询没有 N+1；列表有分页；排序字段白名单。
- [ ] SQL 使用参数绑定，没有字符串拼接。

---

## 7 测试检查

- [ ] 新增业务规则有单元测试（状态机至少覆盖合法/非法迁移）。
- [ ] 新增接口有集成测试或 Postman 用例。
- [ ] 权限用例至少覆盖：角色不允许（403）、不属于自己（404）、状态冲突（409）。
- [ ] 测试数据是虚构的，没有真实患者信息。
- [ ] 测试能本地/CI 跑通，不依赖某个人电脑上的数据库状态。

---

## 8 评论写法

### 8.1 分级

| 前缀 | 含义 | 是否阻塞合并 |
|---|---|---|
| `[blocking]` | 安全/正确性/契约问题，必须改 | 是 |
| `[suggestion]` | 可以更好，但不影响本次合并 | 否 |
| `[question]` | 没看懂，需要作者解释 | 看回答 |
| `[nit]` | 格式/命名小问题 | 否 |

### 8.2 好评论 vs 坏评论

**坏：**

> 这里不对。

**好：**

> `[blocking]` 这里用 `findById` 再判断 `doctorId`，别人的处方会先查出来再返回 403，等于告诉调用方「这个 id 存在」。
> 建议改成 `findByIdAndDoctorId(id, currentDoctorId)`，查不到直接 404，和权限矩阵的 ADR-004 一致。

**坏：**

> 权限没做。

**好：**

> `[blocking]` `approve` 只加了 `@PreAuthorize("hasRole('PHARMACIST')")`，没有校验处方状态。已核销的处方还能再次 approve。
> 建议在 `Prescription.approve()` 里判断 `status == PENDING_REVIEW`，并带 `version` 更新；失败返回 409。对应 SEC-05。

### 8.3 结论

- **Approve**：没有 blocking，建议可后续跟进。
- **Comment**：有讨论点，但不确认是否阻塞。
- **Request changes**：有 blocking，改完再 review。

> 不要用「Approve」夹带 blocking 评论；也不要在无关 PR 里翻旧账。

---

## 9 合并前 60 秒复检

- [ ] CI/本地构建通过（至少后端 `mvn test`、前端 `npm run build`）。
- [ ] 没有冲突标记 `<<<<<<<`。
- [ ] 没有调试代码（`System.out.println`、`console.log`、`TODO` 临时绕过）。
- [ ] 数据库变更脚本可重复执行/有回滚说明。
- [ ] 文档中的图/链接能打开。
- [ ] 合并方式与团队约定一致（Squash merge，保留 PR 号）。

---

## 10 本项目的重点模块检查表

### 10.1 identity / 认证

- [ ] 注册固定 PATIENT，不接收 role。
- [ ] 密码 BCrypt，登录失败不区分手机号/密码。
- [ ] JWT 密钥从环境变量来，有效期 2h。
- [ ] 停用账号不能登录；已发 token 的限制写清楚。

### 10.2 consultation / 订单

- [ ] 创建订单校验医生启用、描述长度。
- [ ] 支付只能本人、只能 `PENDING_PAYMENT`。
- [ ] 接诊/拒诊只能接诊医生本人。
- [ ] 结束问诊校验处方条件。

### 10.3 prescription / 处方

- [ ] 状态机 8 条迁移与需求 A.3 一致。
- [ ] 明细 1–5 条、药品启用、诊断非空。
- [ ] 药品名称/单价快照。
- [ ] 所有迁移带 version，409 带当前状态。
- [ ] 审方记录不可修改。

### 10.4 pharmacy / 药房

- [ ] 待审队列按 `PENDING_REVIEW` 过滤。
- [ ] 核销校验 `validUntil`。
- [ ] 药师看不到无关患者隐私。

### 10.5 admin / 管理

- [ ] 强制作废必须填理由、写审计。
- [ ] 统计口径与需求一致（驳回率/核销率分母写清楚）。
- [ ] 审计日志只增不改。

---

## 11 变更记录

| 版本 | 日期 | 修改人 | 说明 |
|---|---|---|---|
| v0.1 | 2026-10-26 | 2号 | 初稿 |
