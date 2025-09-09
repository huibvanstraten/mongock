package com.hvs.mongock.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Document("users")
data class UserDocument(
    @Id val id: String? = null,
    @field:NotBlank val username: String,
    @field:Email val email: String,
    @Indexed(unique = true) val externalId: String,
    val newsletterOptIn: Boolean?
)