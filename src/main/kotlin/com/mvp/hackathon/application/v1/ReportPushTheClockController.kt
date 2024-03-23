package com.mvp.hackathon.application.v1

import com.mvp.hackathon.domain.model.exception.Exceptions
import com.mvp.hackathon.domain.service.auth.ISecurityService
import com.mvp.hackathon.domain.service.encryption.IEncryptionService
import com.mvp.hackathon.domain.service.report.IReportService
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/report")
class ReportPushTheClockController(
    private val service: IReportService,
    private val securityServiceImpl: ISecurityService,
    private val encryptionService: IEncryptionService
) {

    @PostMapping("/generate-random")
    @PreAuthorize("isAuthenticated()")
    fun generateRandomPunchTheClockEntities(@RequestParam username: String, enties: Int = 30) {
        if (!securityServiceImpl.isCurrentUser(encryptionService.encrypt(username))) {
            throw Exceptions.AccessDeniedException("You do not have permission to access this resource.")
        }
        service.generateRandomPunchTheClockEntities(username, enties)
    }

    @GetMapping("/last-month")
    @PreAuthorize("isAuthenticated()")
    fun findEntriesFromLastMonth(@RequestParam username: String): List<PunchTheClockEntity> {
        if (!securityServiceImpl.isCurrentUser(encryptionService.encrypt(username))) {
            throw Exceptions.AccessDeniedException("You do not have permission to access this resource.")
        }
        return service.findEntriesFromLastMonth(username)
    }
}
