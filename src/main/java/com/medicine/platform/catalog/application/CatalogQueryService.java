package com.medicine.platform.catalog.application;

import java.math.BigDecimal;
import java.util.List;

import com.medicine.platform.catalog.domain.Department;
import com.medicine.platform.catalog.domain.Doctor;
import com.medicine.platform.common.api.PageResponse;

import org.springframework.stereotype.Service;

/**
 * 基础数据查询应用服务。
 * <p>S0 返回内存种子数据（数据库表按 Sprint 增量，S1 才建 department/doctor）；
 * S1 起改为委托 catalog 的仓储端口读取真实数据。</p>
 */
@Service
public class CatalogQueryService {

    private static final List<Department> DEPARTMENTS = List.of(
            new Department(10L, "呼吸内科", 1),
            new Department(11L, "皮肤科", 2),
            new Department(12L, "心血管内科", 3),
            new Department(13L, "儿科", 4));

    private static final List<Doctor> DOCTORS = List.of(
            new Doctor(2001L, "李医生", 10L, "呼吸内科", "主治医师", "慢性咳嗽、哮喘", new BigDecimal("49.00"), true),
            new Doctor(2002L, "王医生", 10L, "呼吸内科", "副主任医师", "慢阻肺、肺部感染", new BigDecimal("79.00"), true),
            new Doctor(2003L, "张医生", 11L, "皮肤科", "主治医师", "湿疹、痤疮", new BigDecimal("39.00"), false),
            new Doctor(2004L, "刘医生", 12L, "心血管内科", "主任医师", "高血压、冠心病", new BigDecimal("99.00"), true),
            new Doctor(2005L, "陈医生", 13L, "儿科", "主治医师", "小儿呼吸、消化", new BigDecimal("59.00"), true));

    public List<Department> listDepartments() {
        return DEPARTMENTS;
    }

    public PageResponse<Doctor> listDoctors(Long departmentId, int page, int size) {
        List<Doctor> filtered = DOCTORS.stream()
                .filter(d -> departmentId == null || d.departmentId().equals(departmentId))
                .toList();
        int from = (page - 1) * size;
        if (from >= filtered.size()) {
            return PageResponse.of(List.of(), page, size, filtered.size());
        }
        int to = Math.min(from + size, filtered.size());
        return PageResponse.of(filtered.subList(from, to), page, size, filtered.size());
    }
}
