package com.hvs.mongock

import io.mongock.runner.springboot.EnableMongock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableMongock
@SpringBootApplication
class MongockApplication

fun main(args: Array<String>) {
    runApplication<MongockApplication>(*args)
}
