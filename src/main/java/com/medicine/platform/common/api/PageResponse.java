package com.medicine.platform.common.api;

import java.util.List;

/**
 * 分页响应体。结构约定见《API 接口规范》§1.2。
 *
 * @param items 当前页数据；空页返回空列表而非 null
 * @param page  页码，从 1 开始
 * @param size  每页条数
 * @param total 总条数（用 int 而非 long，避免被全局 Long→String 序列化规则转成字符串）
 */
public record PageResponse<T>(List<T> items, int page, int size, int total) {

    public static <T> PageResponse<T> of(List<T> items, int page, int size, int total) {
        return new PageResponse<>(items, page, size, total);
    }
}
