package com.mvp.hackathon.infrastructure.entity.time

import com.mvp.hackathon.domain.model.punch.PunchTheClockDTO
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document(collection = "punch_the_clock")
data class PunchTheClockEntity(
    @Id
    var id: String? = null,
    var username: String,
    var date: LocalDate = LocalDate.now(),
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null,
    var breaks: MutableList<BreakPeriod> = mutableListOf()
) {
    fun toDTO(): PunchTheClockDTO {
        return PunchTheClockDTO(
            date = this.date,
            startTime = this.startTime,
            endTime = this.endTime,
            breaks = this.breaks
        )
    }
}

data class BreakPeriod(
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null
)