package com.mvp.hackathon.application.v1

import com.mvp.hackathon.domain.model.exception.Exceptions
import com.mvp.hackathon.domain.service.auth.SecurityService
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.domain.service.report.ReportServiceImpl
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/report")
class ReportPushTheClockController(
    private val service: ReportServiceImpl,
    private val securityService: SecurityService,
    private val encryptionService: EncryptionService
) {

    @PostMapping("/generate-random")
    @PreAuthorize("isAuthenticated()")
    fun generateRandomPunchTheClockEntities(@RequestParam username: String, enties: Int = 30) {
        if (!securityService.isCurrentUser(encryptionService.encrypt(username))) {
            throw Exceptions.AccessDeniedException("You do not have permission to access this resource.")
        }
        service.generateRandomPunchTheClockEntities(username, enties)
    }

    @GetMapping("/last-month")
    @PreAuthorize("isAuthenticated()")
    fun findEntriesFromLastMonth(@RequestParam username: String): List<PunchTheClockEntity> {
        if (!securityService.isCurrentUser(encryptionService.encrypt(username))) {
            throw Exceptions.AccessDeniedException("You do not have permission to access this resource.")
        }
        return service.findEntriesFromLastMonth(username)
    }
}
