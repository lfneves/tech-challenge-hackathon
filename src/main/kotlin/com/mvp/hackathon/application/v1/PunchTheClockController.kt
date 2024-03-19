package com.mvp.hackathon.application.v1

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
    fun startWork(@RequestParam userId: String): PunchTheClockEntity {
        return service.recordStartTime(userId, LocalDateTime.now())
    }

    @PostMapping("/end")
    fun endWork(@RequestParam userId: String): PunchTheClockEntity {
        return service.recordEndTime(userId, LocalDateTime.now())
    }

    @PostMapping("/break-start")
    fun addBreakStart(@RequestParam userId: String): PunchTheClockEntity {
        return service.startBreak(userId, LocalDateTime.now())
    }

    @PostMapping("/break-end")
    fun addBreakEnd(@PathVariable userId: String, @RequestBody breakStart: LocalDateTime): PunchTheClockEntity {
        return service.endBreak(userId, breakStart, LocalDateTime.now())
    }

    @GetMapping("/view-punch-in")
    fun viewEntries(@RequestParam userId: String, @RequestParam date: LocalDate): Double {
        return service.calculateTotalHours(userId)
    }
}
