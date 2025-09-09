package com.hvs.mongock.migrations

import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

@ChangeUnit(id = "users-003-backfill-newsletter", order = "003", author = "you")
class Users003_BackfillNewsletter(private val mongoTemplate: MongoTemplate) {

    @Execution
    fun execute() {
        val query = Query.query(Criteria.where("newsletterOptIn").`is`(null))
        val update = Update().set("newsletterOptIn", false)
        mongoTemplate.updateMulti(query, update, "users")
    }

    @RollbackExecution
    fun rollback() {
        // revert the backfill in a best-effort way
        val query = Query.query(Criteria.where("newsletterOptIn").`is`(false))
        val update = Update().unset("newsletterOptIn")
        mongoTemplate.updateMulti(query, update, "users")
    }
}