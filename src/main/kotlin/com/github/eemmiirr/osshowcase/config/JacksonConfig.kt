package com.github.eemmiirr.osshowcase.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JacksonConfig {
    @Bean
    fun jsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                .modules(
                    Jdk8Module(),
                    JavaTimeModule(),
                    KotlinModule.Builder()
                        .enable(KotlinFeature.StrictNullChecks)
                        .build(),
                )
        }
    }
}
