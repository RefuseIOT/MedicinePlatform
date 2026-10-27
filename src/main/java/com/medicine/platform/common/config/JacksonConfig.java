package com.medicine.platform.common.config;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JSON 序列化约定：Long 转字符串（避免 JS 精度丢失）、BigDecimal 转字符串（金额精度）。
 * 见《系统设计规格说明书》§5.2。
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longAndDecimalToStringCustomizer() {
        return builder -> {
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            builder.serializerByType(BigDecimal.class, ToStringSerializer.instance);
        };
    }
}
