-- 智慧医疗在线问诊与处方流转平台 · S0 数据库初始化脚本（identity 模块）
-- 只建 sys_user 表；其余表按 Sprint 增量创建（见《系统设计规格说明书》§7.1）。
-- 本脚本可重复执行。

CREATE TABLE IF NOT EXISTS sys_user (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    phone      VARCHAR(20)  NOT NULL COMMENT '手机号（登录账号）',
    password   VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码摘要',
    name       VARCHAR(50)  NOT NULL COMMENT '姓名',
    role       VARCHAR(20)  NOT NULL COMMENT '角色：PATIENT/DOCTOR/PHARMACIST/ADMIN',
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/DISABLED',
    created_at DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    updated_at DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_phone (phone)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

-- 种子账号：四个角色各一个，密码均为 123456（BCrypt 存储，各自独立盐）。
INSERT IGNORE INTO sys_user (phone, password, name, role, status) VALUES
    ('13800138001', '$2a$10$Yw8DSuoJ0Q95ITXKk5gQeOzF6KGGYXNn5TlbS0aSusbaYtcpwUAGa', '张患者',   'PATIENT',     'ACTIVE'),
    ('13800138002', '$2a$10$kd5lSIBCTlq7Ao8E5Vx0Se.HRIQoTQn8hnMSVN6RkXxyqn.AF9NdG', '李医生',   'DOCTOR',      'ACTIVE'),
    ('13800138003', '$2a$10$Eg5BfFPz6Wvu7/ShOZhAp..uIquNBrhTrbcj9huAi.qiqjyPv.g76', '王药师',   'PHARMACIST',  'ACTIVE'),
    ('13800138004', '$2a$10$QC0TQnprtMIU5dsxPqSP1eCKmCryMzhlzyUNIQzhoOQrGZFrEiWOm', '赵管理员', 'ADMIN',       'ACTIVE');
