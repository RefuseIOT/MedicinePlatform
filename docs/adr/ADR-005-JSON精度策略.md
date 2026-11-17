# ADR-005：Long 与 BigDecimal 以字符串传输

- 状态：已接受
- 备选：JSON 数字直出。
- 决策：Long、BigDecimal 输出为字符串；时间使用 ISO 8601；枚举使用稳定英文编码。
- 理由：避免 JavaScript 整数精度和金额浮点误差。
- 后果：前端不得对 ID 使用 `Number()`，金额展示使用十进制定点处理。
