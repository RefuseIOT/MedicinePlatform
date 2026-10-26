# 需求基线 UML 图

本页使用 GitHub 原生支持的 Mermaid 渲染，不依赖图片 CDN。PlantUML 源文件仍保留在同一目录，便于继续维护。

## 1. 问诊模块用例图

源文件：[`use-case-consultation.puml`](use-case-consultation.puml)

```mermaid
flowchart LR
    P((患者))
    D((医生))

    subgraph 问诊模块
        UC01([UC-01 注册与登录])
        UC03([UC-03 浏览科室与医生])
        UC04([UC-04 提交问诊订单])
        UC05([UC-05 支付问诊费])
        UC06([UC-06 接诊与拒诊])
        UC07([UC-07 图文问诊])
        UC14([UC-14 结束问诊])
    end

    P --> UC01
    P --> UC03
    P --> UC04
    P --> UC05
    P --> UC07
    D --> UC01
    D --> UC06
    D --> UC07
    D --> UC14
```

## 2. 处方模块用例图

源文件：[`use-case-prescription.puml`](use-case-prescription.puml)

```mermaid
flowchart LR
    D((医生))
    PH((药师))
    P((患者))

    subgraph 处方模块
        UC08([UC-08 开具处方])
        UC09([UC-09 提交处方审核])
        UC10([UC-10 审核处方通过])
        UC11([UC-11 驳回处方])
        UC12([UC-12 修改并重提处方])
        UC13([UC-13 核销处方])
        UC15([UC-15 查看处方与状态])
    end

    D --> UC08
    D --> UC09
    D --> UC12
    D --> UC15
    PH --> UC10
    PH --> UC11
    PH --> UC13
    PH --> UC15
    P --> UC15
    UC09 -. include .-> UC08
    UC12 -. extend .-> UC11
```

## 3. 管理模块用例图

源文件：[`use-case-admin.puml`](use-case-admin.puml)

```mermaid
flowchart LR
    A((管理员))

    subgraph 管理模块
        UC01([UC-01 注册与登录])
        UC02([UC-02 账号与药品维护])
        UC16([UC-16 统计与操作日志])
    end

    A --> UC01
    A --> UC02
    A --> UC16
```

## 4. 活动图：问诊主流程

源文件：[`activity-consultation.puml`](activity-consultation.puml)

```mermaid
flowchart TD
    A([开始]) --> B[患者选择科室与医生]
    B --> C[填写病情描述并提交订单]
    C --> D[模拟支付]
    D --> E{支付成功?}
    E -- 是 --> F[订单状态：待接诊]
    E -- 否 --> G[订单关闭：待支付超时]
    G --> H([结束])
    F --> I[医生查看待接诊列表]
    I --> J{医生接诊?}
    J -- 接诊 --> K[订单状态：问诊中]
    J -- 拒诊 --> L[填写拒诊理由]
    L --> M[订单取消并退款]
    M --> N([结束])
    K --> O[双方图文沟通]
    O --> P[医生判断是否需要处方]
    P --> Q{需要处方?}
    Q -- 是 --> R[开具并提交处方]
    R --> S[等待药师审核]
    Q -- 否 --> T[勾选“无需处方”]
    S --> U[医生结束问诊]
    T --> U
    U --> V[订单状态：已完成]
    V --> W([结束])
```

## 5. 活动图：处方流转

源文件：[`activity-prescription.puml`](activity-prescription.puml)

```mermaid
flowchart TD
    A([开始]) --> B[医生创建处方草稿]
    B --> C[状态：待开具]
    C --> D[填写诊断与药品明细]
    D --> E[提交审核]
    E --> F[状态：待审核]
    F --> G[药师审阅]
    G --> H{审核结果?}
    H -- 通过 --> I[状态：已流转]
    I --> J[设置有效期 3 天]
    J --> K[患者到药房取药]
    K --> L{有效期内核销?}
    L -- 是 --> M[状态：已核销]
    L -- 否 --> N[系统定时任务置为作废]
    H -- 驳回 --> O[填写驳回理由]
    O --> P[状态：已驳回]
    P --> Q[医生查看理由并修改]
    Q --> R[重新提交，回到待审核]
    M --> S([结束])
    N --> S
    R --> S
```

## 6. 领域模型类图

源文件：[`domain-model.puml`](domain-model.puml)

```mermaid
classDiagram
    class 用户 {
        +Long userId
        +String 手机号
        +String 密码摘要
        +String 姓名
        +Enum 角色
        +Enum 状态
    }

    class 医生 {
        +String 职称
        +String 擅长
        +Decimal 问诊价
        +Boolean 在线状态
    }

    class 科室 {
        +String 科室名
        +Integer 排序号
    }

    class 问诊订单 {
        +String 订单号
        +Text 病情描述
        +Enum 状态
        +Decimal 问诊费
        +DateTime 支付时间
    }

    class 会话消息 {
        +Enum 消息类型
        +Text 内容
        +DateTime 发送时间
    }

    class 处方 {
        +String 处方号
        +String 诊断
        +Enum 状态
        +String 驳回理由
        +DateTime 有效期至
    }

    class 处方明细 {
        +String 药品名称快照
        +Decimal 单价快照
        +Integer 数量
        +String 用法用量
        +Integer 天数
    }

    class 药品 {
        +String 药品名
        +String 规格
        +Decimal 单价
        +Boolean 是否处方药
    }

    class 审方记录 {
        +Enum 审核结果
        +String 理由
        +DateTime 审核时间
    }

    用户 "1" --> "0..1" 医生 : 扩展
    科室 "1" --> "0..*" 医生 : 归属
    用户 "1" --> "0..*" 问诊订单 : 患者下单
    医生 "1" --> "0..*" 问诊订单 : 接诊
    问诊订单 "1" --> "0..*" 会话消息
    问诊订单 "1" --> "0..*" 处方
    处方 "1" --> "1..*" 处方明细
    药品 "1" --> "0..*" 处方明细
    处方 "1" --> "0..*" 审方记录
```

## 7. 处方状态图

源文件：[`state-prescription.puml`](state-prescription.puml)

```mermaid
stateDiagram-v2
    [*] --> 待开具 : 医生创建草稿
    待开具 --> 待审核 : T1 提交审核 / 医生
    待审核 --> 已流转 : T2 审核通过 / 药师
    待审核 --> 已驳回 : T3 审核驳回 / 药师
    已驳回 --> 待审核 : T4 修改后重提 / 医生
    待开具 --> 已作废 : T5 作废 / 医生
    已流转 --> 已核销 : T6 核销 / 药师
    已流转 --> 已作废 : T7 过期未核销 / 系统
    待审核 --> 已作废 : T8 强制作废 / 管理员
    已核销 --> [*]
    已作废 --> [*]
```
