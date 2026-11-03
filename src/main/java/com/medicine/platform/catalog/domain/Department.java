package com.medicine.platform.catalog.domain;

/**
 * 科室（只读查询模型）。S0 为内存种子数据，S1 起落 department 表。
 *
 * @param id   科室 ID
 * @param name 科室名
 * @param sort 排序号，越小越靠前
 */
public record Department(Long id, String name, Integer sort) {
}
