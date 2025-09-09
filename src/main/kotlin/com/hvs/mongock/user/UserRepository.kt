package com.hvs.mongock.user

import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<UserDocument, String> {
    fun existsByEmail(email: String): Boolean
}