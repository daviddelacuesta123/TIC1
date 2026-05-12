package com.logistica.logistica_urbana.infrastructure.config;

import com.logistica.logistica_urbana.infrastructure.persistence.mapper.VehiculoMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public VehiculoMapper vehiculoMapper() {
        return Mappers.getMapper(VehiculoMapper.class);
    }
}
