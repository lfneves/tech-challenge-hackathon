package com.mvp.hackathon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication(excludeName = ["de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration"])
@EnableMongoRepositories(basePackages = ["com.mvp.hackathon.infrastructure.repository"])
class PunchTheClockApplication

fun main(args: Array<String>) {
    runApplication<PunchTheClockApplication>(*args)
}
