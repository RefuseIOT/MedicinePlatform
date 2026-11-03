package com.medicine.platform.catalog.domain;

import java.math.BigDecimal;

/**
 * 医生目录（只读查询模型）。S0 为内存种子数据，S1 起落 doctor 表。
 *
 * @param consultationFee 问诊价，单位元，两位小数（JSON 序列化为字符串，见 ADR-005）
 */
public record Doctor(
        Long id,
        String name,
        Long departmentId,
        String departmentName,
        String title,
        String specialty,
        BigDecimal consultationFee,
        boolean online) {
}
