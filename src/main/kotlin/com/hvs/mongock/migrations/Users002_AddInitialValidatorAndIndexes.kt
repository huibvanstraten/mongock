package com.hvs.mongock.migrations

import com.mongodb.client.MongoDatabase
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.bson.Document
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@ChangeUnit(id = "users-002-initial-validator-indexes", order = "002", author = "hvs")
class Users002_AddInitialValidatorAndIndexes(private val mongoTemplate: MongoTemplate) {

    @Execution
    fun execute() {
        val db: MongoDatabase = mongoTemplate.db

        val jsonSchema = Document(mapOf(
            "bsonType" to "object",
            "required" to listOf("username", "email", "externalId"),
            "properties" to mapOf(
                "_id" to mapOf("bsonType" to listOf("objectId", "string")),
                "_class" to mapOf("bsonType" to "string"),
                "username" to mapOf("bsonType" to "string", "minLength" to 1),
                "email"    to mapOf("bsonType" to "string", "pattern" to "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"),
                "externalId" to mapOf("bsonType" to "string"),
                "newsletterOptIn" to mapOf("bsonType" to listOf("bool", "null"))
            ),
            "additionalProperties" to false
        ))
        db.runCommand(Document(mapOf(
            "collMod" to "users",
            "validator" to Document("\$jsonSchema", jsonSchema),
            "validationLevel" to "moderate",
            "validationAction" to "warn"
        )))

        mongoTemplate.indexOps("users").createIndex(
            Index().on("externalId", Sort.Direction.ASC).unique()
        )
        mongoTemplate.indexOps("users").createIndex(
            Index().on("email", Sort.Direction.ASC)
        )
    }

    @RollbackExecution
    fun rollback() {
        val db = mongoTemplate.db
        db.runCommand(
            Document(
                mapOf(
                    "collMod" to "users",
                    "validator" to Document(),
                    "validationLevel" to "off",
                    "validationAction" to "warn"
                )
            )
        )
    }
}