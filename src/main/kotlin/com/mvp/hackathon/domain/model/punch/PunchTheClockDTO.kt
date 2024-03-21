package com.mvp.hackathon.domain.model.punch

import com.mvp.hackathon.infrastructure.entity.time.BreakPeriod
import java.time.LocalDate
import java.time.LocalDateTime

data class PunchTheClockDTO(
    var date: LocalDate = LocalDate.now(),
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null,
    var breaks: MutableList<BreakPeriod> = mutableListOf(),
    var totalHoursDay: String = ""
)