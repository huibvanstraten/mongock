package com.hvs.mongock.user

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.containers.MongoDBContainer
import com.mongodb.MongoWriteException
import com.mongodb.assertions.Assertions.assertTrue
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.junit.jupiter.SpringExtension


/**
 * Integration test using:
 * - Testcontainers MongoDB (real database)
 * - Mongock migrations (applied automatically on Spring Boot startup)
 * - Spring Data MongoDB repositories
 *
 * Flow:
 * 1. Testcontainers starts a clean MongoDB container
 * 2. DynamicPropertySource injects container URI into Spring Boot
 * 3. Spring Boot context starts -> Mongock runs all @ChangeUnit migrations
 * 4. Tests run against the migrated database
 */

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension::class)
class UsersSchemaIT(
    @Autowired private val repo: UserRepository
) {

    companion object {
        @Container
        @JvmStatic
        val mongo: MongoDBContainer = MongoDBContainer("mongo:7")

        @JvmStatic
        @DynamicPropertySource
        fun mongoProps(reg: DynamicPropertyRegistry) {
            reg.add("spring.data.mongodb.uri") { "${mongo.connectionString}/testdb" }
        }
    }

    @Test
    fun `should reject invalid document`() {
        val ex = assertThrows(DataIntegrityViolationException::class.java) {
            repo.save(
                UserDocument(
                    username = "alice",
                    email = "alice@example.com",
                    externalId = "ext-999",
                    newsletterOptIn = null,
                )
            )
        }
        // checking if it's actually the expected Mongo exception
        assertTrue(ex.cause is MongoWriteException)
        val exception = ex.cause as MongoWriteException
        assertTrue(exception.error.code == 121) // 121 = Document failed validation
    }

    @Test
    fun `should accept valid document (schema compliant)`() {
        val saved = repo.save(
            UserDocument(
                username = "bob",
                email = "bob@example.com",
                externalId = "ext-123",
                newsletterOptIn = false,
            )
        )
        assert(saved.id != null) { "Expected document to be saved with generated id" }
    }
}
