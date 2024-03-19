package com.mvp.hackathon.infrastructure.entity.time

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document(collection = "punch_the_clock")
data class PunchTheClockEntity(
    @Id var id: String? = null,
    val userId: String,
    val date: LocalDate = LocalDate.now(),
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null,
    var breaks: MutableList<BreakPeriod> = mutableListOf()
)

data class BreakPeriod(
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null
)