package com.mvp.hackathon.infrastructure.repository.time

import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDate

interface PunchTheClockRepository : MongoRepository<PunchTheClockEntity, String> {
    fun findByUsernameAndDate(username: String, date: LocalDate): PunchTheClockEntity?

    fun findListByUsernameAndDate(username: String, date: LocalDate): List<PunchTheClockEntity>

    fun findByDateBetween(start: LocalDate, end: LocalDate): List<PunchTheClockEntity>
}