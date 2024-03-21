package com.mvp.hackathon.application.v1

import com.mvp.hackathon.domain.service.report.ReportServiceImpl
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/report")
class ReportPushTheClockController(
    private val service: ReportServiceImpl
) {

    @PostMapping("/generate-random")
    fun generateRandomPunchTheClockEntities(@RequestParam username: String, enties: Int = 30) {
        service.generateRandomPunchTheClockEntities(username, enties)
    }

    @GetMapping("/last-month")
    fun findEntriesFromLastMonth(@RequestParam username: String): List<PunchTheClockEntity> {
        return service.findEntriesFromLastMonth(username)
    }
}
