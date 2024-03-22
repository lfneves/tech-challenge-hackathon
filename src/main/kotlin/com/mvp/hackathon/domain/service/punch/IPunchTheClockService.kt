package com.mvp.hackathon.domain.service.punch

import com.mvp.hackathon.domain.model.punch.PunchTheClockDTO
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import java.time.LocalDateTime

interface IPunchTheClockService {

    fun recordStartTime(username: String, dateTime: LocalDateTime): PunchTheClockEntity

    fun recordEndTime(username: String, dateTime: LocalDateTime): PunchTheClockEntity

    fun startBreak(username: String, dateTime: LocalDateTime): PunchTheClockEntity

    fun endBreak(username: String, dateTime: LocalDateTime): PunchTheClockEntity

    fun calculateTotalHours(username: String): MutableList<PunchTheClockDTO>
}