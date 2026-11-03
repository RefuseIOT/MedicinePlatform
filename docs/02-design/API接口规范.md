# 智慧医疗在线问诊与处方流转平台 · API 接口规范

| 项 | 内容 |
|---|---|
| 版本 | v0.1 |
| 日期 | 2026-10-26 |
| 编写 | 2号（架构与权限） |
| 状态 | 与《系统设计规格说明书（架构与接口分册）》配套；S0 起作为前后端联调契约 |
| 适用范围 | 后端 Controller、前端 `src/api/`、接口测试、PR 审查 |

> 约定：本文只写「接口长什么样」。业务规则、状态机、权限语义见《系统设计规格说明书》第 5、6 章。
> 任何字段/路径变更，先改本文并走 PR；不允许前端先猜、后端后改。

---

## 1 通用约定

| 项 | 约定 |
|---|---|
| Base URL | `/api/v1` |
| 编码 | UTF-8；`Content-Type: application/json` |
| 认证 | `Authorization: Bearer <JWT>` |
| JSON 命名 | lowerCamelCase |
| ID | JSON 字符串（后端 Long），避免 JS 精度丢失 |
| 金额 | 字符串，单位元，两位小数，如 `"49.00"` |
| 时间 | ISO 8601 带时区，如 `2026-10-26T18:43:11+08:00` |
| 分页 | `page` 从 1 开始，默认 1；`size` 默认 20，最大 100 |
| 排序 | `sort=createdAt,desc`，字段白名单 |
| 空列表 | 返回 `[]`，不返回 `null` |
| 追踪 | 每个响应带 `traceId` |
| 幂等 | 状态迁移必须带 `version`；重复提交返回 409 |

### 1.1 统一响应

```json
{
  "code": 0,
  "message": "ok",
  "data": {},
  "traceId": "b7c3f0d2e8a14f6c"
}
```

- `code=0` 成功；非 0 为业务错误；
- HTTP 状态码必须正确，不允许全部 200；
- 失败时 `data` 可为 `null` 或补充信息。

### 1.2 分页响应

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [],
    "page": 1,
    "size": 20,
    "total": 0
  },
  "traceId": "..."
}
```

---

## 2 错误码

| code | HTTP | 含义 | 前端处理 |
|---|---|---|---|
| 0 | 200/201/204 | 成功 | 正常 |
| 40001 | 400 | 参数校验失败 | 展示字段错误 |
| 40002 | 400 | 业务参数不合法（如理由太短） | 展示 message |
| 40101 | 401 | 未登录/token 无效/过期 | 清 token，跳登录 |
| 40301 | 403 | 角色不允许 | 展示无权限 |
| 40401 | 404 | 资源不存在或不属于当前用户 | 展示未找到 |
| 40901 | 409 | 状态不允许 | 刷新详情后重试 |
| 40902 | 409 | 版本冲突 | 刷新详情后重试 |
| 40903 | 409 | 重复操作（重复支付/核销） | 展示已处理 |
| 42901 | 429 | 请求过于频繁 | 稍后再试 |
| 50000 | 500 | 系统错误 | 展示通用错误 + traceId |

校验失败示例：

```json
{
  "code": 40001,
  "message": "参数校验失败",
  "data": {
    "fields": {
      "description": "病情描述长度需在 10–500 字之间"
    }
  },
  "traceId": "..."
}
```

---

## 3 枚举

### 3.1 角色 `Role`

| 值 | 含义 |
|---|---|
| `PATIENT` | 患者 |
| `DOCTOR` | 医生 |
| `PHARMACIST` | 药师 |
| `ADMIN` | 管理员 |

### 3.2 订单状态 `OrderStatus`

| 值 | 含义 |
|---|---|
| `PENDING_PAYMENT` | 待支付 |
| `PENDING_ACCEPT` | 待接诊 |
| `IN_CONSULTATION` | 问诊中 |
| `COMPLETED` | 已完成 |
| `CANCELLED` | 已取消 |

### 3.3 处方状态 `PrescriptionStatus`

| 值 | 含义 |
|---|---|
| `DRAFT` | 待开具（草稿） |
| `PENDING_REVIEW` | 待审核 |
| `REJECTED` | 已驳回 |
| `CIRCULATED` | 已流转 |
| `DISPENSED` | 已核销 |
| `CANCELLED` | 已作废 |

### 3.4 消息类型 `MessageType`

| 值 | 含义 |
|---|---|
| `TEXT` | 文字 |
| `IMAGE` | 图片 |

### 3.5 审方结果 `ReviewResult`

| 值 | 含义 |
|---|---|
| `APPROVED` | 通过 |
| `REJECTED` | 驳回 |

---

## 4 认证与账号

### 4.1 患者注册

`POST /api/v1/auth/register`（公开）

请求：

```json
{
  "phone": "13800138000",
  "password": "Passw0rd!",
  "name": "张三"
}
```

规则：

- 服务端固定 `role=PATIENT`；请求体**不接收** `role`，传了也忽略；
- 手机号 11 位、唯一；密码 8–32 位，至少含字母和数字；
- 密码 BCrypt 存储。

响应 201：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": "1001",
    "phoneMasked": "138****8000",
    "name": "张三",
    "role": "PATIENT"
  },
  "traceId": "..."
}
```

### 4.2 登录

`POST /api/v1/auth/login`（公开）

请求：

```json
{
  "phone": "13800138000",
  "password": "Passw0rd!"
}
```

响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 7200,
    "user": {
      "id": "1001",
      "name": "张三",
      "role": "PATIENT"
    }
  },
  "traceId": "..."
}
```

失败：401 / `40101`，不区分「手机号不存在」与「密码错误」。

### 4.3 当前用户

`GET /api/v1/auth/me`（登录）

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": "1001",
    "name": "张三",
    "role": "PATIENT",
    "phoneMasked": "138****8000",
    "status": "ACTIVE"
  },
  "traceId": "..."
}
```

### 4.4 注销

`POST /api/v1/auth/logout`（登录）

响应 204，无 body。服务端记录审计；本期客户端清 token。

### 4.5 账号管理（管理员）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/v1/admin/users?role=DOCTOR&page=1&size=20` | 账号列表，手机号脱敏 |
| POST | `/api/v1/admin/users` | 创建医生/药师/管理员 |
| PATCH | `/api/v1/admin/users/{id}/status` | 启用/停用 |

创建请求：

```json
{
  "phone": "13900139000",
  "password": "Doctor123",
  "name": "李医生",
  "role": "DOCTOR",
  "title": "主治医师",
  "departmentId": "10",
  "specialty": "呼吸内科",
  "consultationFee": "49.00"
}
```

规则：

- 只有 ADMIN 可调用；
- 角色只能是 `DOCTOR`、`PHARMACIST`、`ADMIN`，不能创建 PATIENT（患者走公开注册）；
- 创建医生时同步创建 `doctor` 扩展记录；
- 停用账号后，该用户 token 在过期前仍可能有效（本期限制），S4 可加 `tokenVersion` 即时失效。

---

## 5 基础数据

### 5.1 科室列表

`GET /api/v1/departments`（登录）

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    { "id": "10", "name": "呼吸内科", "sort": 1 },
    { "id": "11", "name": "皮肤科", "sort": 2 }
  ],
  "traceId": "..."
}
```

### 5.2 医生列表

`GET /api/v1/doctors?departmentId=10&page=1&size=20`（登录）

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [
      {
        "id": "2001",
        "name": "李医生",
        "departmentId": "10",
        "departmentName": "呼吸内科",
        "title": "主治医师",
        "specialty": "慢性咳嗽、哮喘",
        "consultationFee": "49.00",
        "online": true
      }
    ],
    "page": 1,
    "size": 20,
    "total": 1
  },
  "traceId": "..."
}
```

### 5.3 医生详情

`GET /api/v1/doctors/{id}`（登录）

返回字段同上，另加 `introduction`、`avatarUrl`（可为 `null`）。

### 5.4 药品目录

`GET /api/v1/drugs?keyword=阿莫西林&page=1&size=20`（DOCTOR/ADMIN）

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [
      {
        "id": "3001",
        "name": "阿莫西林胶囊",
        "spec": "0.25g×24粒",
        "unitPrice": "12.50",
        "prescriptionOnly": true,
        "enabled": true
      }
    ],
    "page": 1,
    "size": 20,
    "total": 1
  },
  "traceId": "..."
}
```

### 5.5 药品维护

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | `/api/v1/admin/drugs` | ADMIN | 新增 |
| PUT | `/api/v1/admin/drugs/{id}` | ADMIN | 修改/启停 |

请求：

```json
{
  "name": "阿莫西林胶囊",
  "spec": "0.25g×24粒",
  "unitPrice": "12.50",
  "prescriptionOnly": true,
  "enabled": true
}
```

---

## 6 问诊订单

### 6.1 创建订单

`POST /api/v1/consultation-orders`（PATIENT）

请求：

```json
{
  "doctorId": "2001",
  "description": "反复咳嗽三天，无发热，想咨询是否需要拍片。"
}
```

规则：

- 病情描述 10–500 字；
- 医生必须存在且启用；
- 初始状态 `PENDING_PAYMENT`，金额取医生当前问诊价快照；
- 30 分钟未支付由定时任务关闭（S1 可先手工）。

响应 201：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": "4001",
    "orderNo": "CO202610260001",
    "status": "PENDING_PAYMENT",
    "fee": "49.00",
    "version": 0,
    "createdAt": "2026-10-26T18:43:11+08:00"
  },
  "traceId": "..."
}
```

### 6.2 模拟支付

`POST /api/v1/consultation-orders/{id}/pay`（PATIENT + owner）

请求：

```json
{
  "version": 0
}
```

响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": "4001",
    "status": "PENDING_ACCEPT",
    "paidAt": "2026-10-26T18:45:00+08:00",
    "version": 1
  },
  "traceId": "..."
}
```

规则：只有 `PENDING_PAYMENT` 可支付；重复支付返回 409/`40903`。

### 6.3 接诊 / 拒诊

| 方法 | 路径 | 权限 | 请求 |
|---|---|---|---|
| POST | `/consultation-orders/{id}/accept` | DOCTOR + owner | `{"version":1}` |
| POST | `/consultation-orders/{id}/reject` | DOCTOR + owner | `{"version":1,"reason":"..."}` |

规则：

- 只有 `PENDING_ACCEPT` 可操作；
- 拒诊理由 ≥5 字；拒诊后订单 `CANCELLED`，模拟退款；
- 接诊后 `IN_CONSULTATION`。

### 6.4 订单列表

`GET /api/v1/consultation-orders?status=IN_CONSULTATION&page=1&size=20`（登录）

- 患者：只返回 `patientId = 当前用户`；
- 医生：只返回 `doctorId = 当前医生`；
- 管理员：全部；
- 药师：无权限（403）。

列表项：

```json
{
  "id": "4001",
  "orderNo": "CO202610260001",
  "patientName": "张三",
  "doctorName": "李医生",
  "departmentName": "呼吸内科",
  "status": "IN_CONSULTATION",
  "fee": "49.00",
  "createdAt": "2026-10-26T18:43:11+08:00"
}
```

> 患者看医生、医生看患者，姓名按需展示；管理员列表手机号脱敏。

### 6.5 订单详情

`GET /api/v1/consultation-orders/{id}`（owner / ADMIN）

返回订单字段 + `doctor` 摘要 + `prescriptionSummary`（最新处方 id/状态，可为 `null`）。

### 6.6 结束问诊

`POST /api/v1/consultation-orders/{id}/end`（DOCTOR + owner + state）

请求：

```json
{
  "version": 4,
  "noPrescription": false
}
```

规则：

- 状态必须 `IN_CONSULTATION`；
- 有 `CIRCULATED` 或 `DISPENSED` 处方，或 `noPrescription=true` 才能结束；
- 结束后订单 `COMPLETED`；消息只读。

---

## 7 图文消息

### 7.1 消息列表

`GET /api/v1/consultation-orders/{id}/messages?page=1&size=50`（owner）

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [
      {
        "id": "5001",
        "type": "TEXT",
        "content": "请问咳嗽有痰吗？",
        "senderId": "2001",
        "senderName": "李医生",
        "senderRole": "DOCTOR",
        "createdAt": "2026-10-26T19:00:00+08:00"
      }
    ],
    "page": 1,
    "size": 50,
    "total": 1
  },
  "traceId": "..."
}
```

### 7.2 发送消息

`POST /api/v1/consultation-orders/{id}/messages`（owner + state）

```json
{
  "type": "TEXT",
  "content": "有少量白痰，没有发热。"
}
```

规则：

- 订单必须 `IN_CONSULTATION`；结束后只读，返回 409；
- 文字 1–1000 字；图片先走上传接口拿 `url`，再发 `{"type":"IMAGE","content":"/uploads/..."}`；
- 发送者从当前用户取，不信任请求体。

---

## 8 处方

### 8.1 创建处方草稿

`POST /api/v1/consultation-orders/{orderId}/prescriptions`（DOCTOR + owner + state）

```json
{
  "diagnosis": "急性支气管炎",
  "items": [
    {
      "drugId": "3001",
      "quantity": 2,
      "usage": "口服，一次0.5g，一日3次",
      "days": 5
    }
  ]
}
```

规则：

- 订单必须是该医生的 `IN_CONSULTATION`；
- 诊断 1–200 字；明细 1–5 条；药品必须启用；
- 保存药品名称/规格/单价快照；
- 初始状态 `DRAFT`。

响应 201：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": "6001",
    "prescriptionNo": "RX202610260001",
    "status": "DRAFT",
    "version": 0,
    "totalAmount": "25.00"
  },
  "traceId": "..."
}
```

### 8.2 修改处方

`PUT /api/v1/prescriptions/{id}`（DOCTOR + owner + state）

请求同创建，另加 `version`。只有 `DRAFT` 或 `REJECTED` 可改；`PENDING_REVIEW`/`CIRCULATED`/`DISPENSED` 返回 409。

### 8.3 处方详情

`GET /api/v1/prescriptions/{id}`（owner / PHARMACIST / ADMIN）

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": "6001",
    "prescriptionNo": "RX202610260001",
    "orderId": "4001",
    "patientId": "1001",
    "patientName": "张三",
    "doctorId": "2001",
    "doctorName": "李医生",
    "diagnosis": "急性支气管炎",
    "status": "PENDING_REVIEW",
    "rejectReason": null,
    "validUntil": null,
    "version": 3,
    "totalAmount": "25.00",
    "items": [
      {
        "id": "6101",
        "drugId": "3001",
        "drugName": "阿莫西林胶囊",
        "drugSpec": "0.25g×24粒",
        "unitPrice": "12.50",
        "quantity": 2,
        "usage": "口服，一次0.5g，一日3次",
        "days": 5,
        "subtotal": "25.00"
      }
    ],
    "reviews": [],
    "timeline": [
      { "status": "DRAFT", "at": "2026-10-26T18:50:00+08:00", "by": "李医生" },
      { "status": "PENDING_REVIEW", "at": "2026-10-26T19:02:00+08:00", "by": "李医生" }
    ],
    "createdAt": "2026-10-26T18:50:00+08:00",
    "updatedAt": "2026-10-26T19:02:00+08:00"
  },
  "traceId": "..."
}
```

### 8.4 处方列表

`GET /api/v1/prescriptions?status=PENDING_REVIEW&page=1&size=20`（登录）

- 患者：自己的处方；
- 医生：自己开的处方；
- 药师：`PENDING_REVIEW` 队列 + `CIRCULATED` 待核销；
- 管理员：全部。

### 8.5 提交审核

`POST /api/v1/prescriptions/{id}/submit`（DOCTOR + owner + state）

```json
{
  "version": 2
}
```

规则：

- 状态 `DRAFT` 或 `REJECTED`；
- 诊断非空、明细 1–5 条、药品启用；
- 状态变 `PENDING_REVIEW`，写提交时间与审计；
- 版本冲突 409/`40902`。

### 8.6 审核通过

`POST /api/v1/prescriptions/{id}/approve`（PHARMACIST + state）

```json
{
  "version": 3
}
```

规则：

- 状态 `PENDING_REVIEW`；
- `validUntil = now + 3 天`；
- 状态变 `CIRCULATED`，写 `prescription_review`、审计、事件；
- 版本冲突 409。

### 8.7 驳回

`POST /api/v1/prescriptions/{id}/reject`（PHARMACIST + state）

```json
{
  "version": 3,
  "reason": "阿莫西林与患者过敏史冲突，请更换药品。"
}
```

规则：

- 理由 ≥10 字；
- 状态 `PENDING_REVIEW` → `REJECTED`；
- 写审方记录、驳回理由、审计。

### 8.8 核销

`POST /api/v1/prescriptions/{id}/dispense`（PHARMACIST + state）

```json
{
  "version": 5
}
```

规则：

- 状态 `CIRCULATED`；
- `now <= validUntil`，否则 409；
- 状态变 `DISPENSED`，写核销人/时间、审计；
- 重复核销 409/`40903`。

### 8.9 作废

`POST /api/v1/prescriptions/{id}/cancel`（DOCTOR + owner（仅 DRAFT）/ ADMIN（仅 PENDING_REVIEW））

```json
{
  "version": 2,
  "reason": "患者取消问诊"
}
```

规则：

- 医生只能作废自己的 `DRAFT`；
- 管理员只能强制作废 `PENDING_REVIEW`，必须填理由，必须写审计；
- 其他状态 409；
- 作废后 `CANCELLED`，终态。

---

## 9 管理

### 9.1 统计概览

`GET /api/v1/admin/stats/overview?from=2026-10-01&to=2026-10-31`（ADMIN）

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "orderCount": 120,
    "prescriptionCount": 86,
    "rejectRate": "12.8%",
    "dispenseRate": "73.3%"
  },
  "traceId": "..."
}
```

### 9.2 审计日志

`GET /api/v1/admin/audit-logs?action=PRESCRIPTION_FORCE_CANCEL&page=1&size=20`（ADMIN）

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [
      {
        "id": "9001",
        "actorName": "管理员",
        "actorRole": "ADMIN",
        "action": "PRESCRIPTION_FORCE_CANCEL",
        "resourceType": "PRESCRIPTION",
        "resourceId": "6001",
        "reason": "重复开方",
        "createdAt": "2026-10-26T20:00:00+08:00"
      }
    ],
    "page": 1,
    "size": 20,
    "total": 1
  },
  "traceId": "..."
}
```

---

## 10 上传（可选，S2）

`POST /api/v1/uploads`（登录，`multipart/form-data`）

- 字段 `file`；限制 5MB；只允许 jpg/png/webp；
- 返回 `{"url":"/uploads/2026/10/xxx.png"}`；
- 文件名随机，禁止原文件名路径穿越；
- Nginx 对 `/uploads` 只读，禁止执行。

---

## 11 权限速查

| 接口组 | PATIENT | DOCTOR | PHARMACIST | ADMIN |
|---|---|---|---|---|
| `/auth/**` | ✓ | ✓ | ✓ | ✓ |
| `/departments`、`/doctors` | ✓ | ✓ | ✓ | ✓ |
| `/drugs` | — | ✓ | — | ✓ |
| `/consultation-orders` 创建/支付 | ✓ | — | — | — |
| `/consultation-orders` 接诊/拒诊/结束 | — | ✓ | — | — |
| `/consultation-orders` 查看 | 自己 | 自己接诊 | — | 全部 |
| `/messages` | 自己订单 | 自己订单 | — | — |
| `/prescriptions` 开方/改/提交 | — | ✓ | — | — |
| `/prescriptions` 审方/核销 | — | — | ✓ | — |
| `/prescriptions` 查看 | 自己 | 自己 | 待审/待核销 | 全部 |
| `/admin/**` | — | — | — | ✓ |

> 完整规则（含 owner/state）见《系统设计规格说明书》6.4–6.6 与《权限矩阵.md》。

---

## 12 变更记录

| 版本 | 日期 | 修改人 | 说明 |
|---|---|---|---|
| v0.1 | 2026-10-26 | 2号 | 初稿：通用约定、认证、订单、消息、处方、管理、权限速查 |
