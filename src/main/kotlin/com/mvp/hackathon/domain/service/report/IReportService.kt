package com.mvp.hackathon.domain.service.report

import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import org.springframework.transaction.annotation.Transactional

interface IReportService {

    @Transactional
    fun findEntriesFromLastMonth(username: String): List<PunchTheClockEntity>

    fun generateRandomPunchTheClockEntities(username: String, numberOfEntries: Int)
}