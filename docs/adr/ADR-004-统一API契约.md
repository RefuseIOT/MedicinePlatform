# ADR-004：统一 API 版本、响应与错误码

- 状态：已接受
- 备选：裸 REST 响应；GraphQL。
- 决策：使用 `/api/v1`、`ApiResponse{code,message,data,traceId}`、统一分页和分段错误码。
- 理由：前端处理一致，便于接口测试和故障追踪。
- 后果：204 响应无正文；Controller 不直接返回实体。
