package com.medicine.platform.catalog.interfaces;

import java.util.List;

import com.medicine.platform.catalog.application.CatalogQueryService;
import com.medicine.platform.catalog.domain.Department;
import com.medicine.platform.common.api.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 科室查询接口。见《API 接口规范》§5.1。
 */
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final CatalogQueryService catalogQueryService;

    public DepartmentController(CatalogQueryService catalogQueryService) {
        this.catalogQueryService = catalogQueryService;
    }

    @GetMapping
    public ApiResponse<List<Department>> list() {
        return ApiResponse.ok(catalogQueryService.listDepartments());
    }
}
