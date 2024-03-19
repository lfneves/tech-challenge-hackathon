package com.mvp.hackathon.infrastructure.repository.time

import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDate

interface PunchTheClockRepository : MongoRepository<PunchTheClockEntity, String> {
    fun findByUserIdAndDate(userId: String, date: LocalDate): PunchTheClockEntity?
}