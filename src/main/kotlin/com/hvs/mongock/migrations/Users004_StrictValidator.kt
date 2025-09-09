package com.hvs.mongock.migrations

import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "users-004-strict-validator", order = "004", author = "sample")
class Users004_StrictValidator(private val mongoTemplate: MongoTemplate) {

    @Execution
    fun execute() {
        val db = mongoTemplate.db

        val jsonSchema = Document(mapOf(
            "bsonType" to "object",
            "required" to listOf("username","email","externalId","newsletterOptIn"),
            "properties" to mapOf(
                "_id" to mapOf("bsonType" to listOf("objectId", "string")),
                "_class" to mapOf("bsonType" to "string"),
                "username" to mapOf("bsonType" to "string", "minLength" to 1),
                "email"    to mapOf("bsonType" to "string", "pattern" to "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"),
                "externalId" to mapOf("bsonType" to "string"),
                "newsletterOptIn" to mapOf("bsonType" to "bool")
            ),
            "additionalProperties" to false
        ))
        db.runCommand(Document(mapOf(
            "collMod" to "users",
            "validator" to Document("\$jsonSchema", jsonSchema),
            "validationLevel" to "strict",
            "validationAction" to "error"
        )))
    }

    @RollbackExecution
    fun rollback() {
        val db = mongoTemplate.db

        // Revert to the looser schema used in Users002
        val jsonSchema = Document(mapOf(
            "bsonType" to "object",
            "required" to listOf("username", "email", "externalId"),
            "properties" to mapOf(
                "username" to mapOf("bsonType" to "string", "minLength" to 1),
                "email" to mapOf("bsonType" to "string", "pattern" to "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"),
                "externalId" to mapOf("bsonType" to "string"),
                "newsletterOptIn" to mapOf("bsonType" to listOf("bool", "null"))
            ),
            "additionalProperties" to false
        ))

        db.runCommand(
            Document(
                mapOf(
                    "collMod" to "users",
                    "validator" to Document("\$jsonSchema", jsonSchema),
                    "validationLevel" to "moderate",
                    "validationAction" to "warn"
                )
            )
        )
    }
}
