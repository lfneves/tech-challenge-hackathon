package com.mvp.hackathon.application.v1

import com.mvp.hackathon.domain.model.punch.PunchTheClockDTO
import com.mvp.hackathon.domain.service.punch.PunchTheClockService
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/punch-the-clock")
class PunchTheClockController(
    private val service: PunchTheClockService
) {

    @PostMapping("/start")
    fun startWork(@RequestParam username: String): PunchTheClockEntity {
        return service.recordStartTime(username, LocalDateTime.now())
    }

    @PostMapping("/end")
    fun endWork(@RequestParam username: String): PunchTheClockEntity {
        return service.recordEndTime(username, LocalDateTime.now())
    }

    @PostMapping("/break-start")
    fun addBreakStart(@RequestParam username: String): PunchTheClockEntity {
        return service.startBreak(username, LocalDateTime.now())
    }

    @PostMapping("/break-end")
    fun addBreakEnd(@RequestParam username: String): PunchTheClockEntity {
        return service.endBreak(username, LocalDateTime.now())
    }

    @GetMapping("/view-punch-in-today")
    fun viewEntries(@RequestParam username: String): MutableList<PunchTheClockDTO> {
        return service.calculateTotalHours(username)
    }
}
