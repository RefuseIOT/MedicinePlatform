package com.medicine.platform.catalog.interfaces;

import com.medicine.platform.catalog.application.CatalogQueryService;
import com.medicine.platform.catalog.domain.Doctor;
import com.medicine.platform.common.api.ApiResponse;
import com.medicine.platform.common.api.PageResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医生目录查询接口。见《API 接口规范》§5.2。
 */
@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private static final int MAX_SIZE = 100;

    private final CatalogQueryService catalogQueryService;

    public DoctorController(CatalogQueryService catalogQueryService) {
        this.catalogQueryService = catalogQueryService;
    }

    @GetMapping
    public ApiResponse<PageResponse<Doctor>> list(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), MAX_SIZE);
        return ApiResponse.ok(catalogQueryService.listDoctors(departmentId, safePage, safeSize));
    }
}
