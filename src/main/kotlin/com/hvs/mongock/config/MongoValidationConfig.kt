package com.hvs.mongock.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import jakarta.validation.Validator
import org.springframework.data.mongodb.core.mapping.event.ValidatingEntityCallback

@Configuration
class MongoValidationConfig {
    @Bean
    fun validatingMongoEventListener(validator: Validator) =
        ValidatingEntityCallback(validator)
}