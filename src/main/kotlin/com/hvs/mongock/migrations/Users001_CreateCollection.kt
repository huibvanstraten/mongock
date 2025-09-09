package com.hvs.mongock.migrations

import com.mongodb.client.MongoDatabase
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "users-001-create-collection", order = "001", author = "hvs")
class Users001_CreateCollection(private val mongoTemplate: MongoTemplate) {

    @Execution
    fun execute() {
        val db: MongoDatabase = mongoTemplate.db
        val collections = db.listCollectionNames().toList()
        if ("users" !in collections) {
            db.createCollection("users")
        }
    }

    @RollbackExecution
    fun rollback() {
        val db: MongoDatabase = mongoTemplate.db
        val collections = db.listCollectionNames().toList()
        if ("users" in collections) {
            db.getCollection("users").drop()
        }
    }
}