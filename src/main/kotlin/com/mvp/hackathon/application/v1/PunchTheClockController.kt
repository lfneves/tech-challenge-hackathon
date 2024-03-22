package com.mvp.hackathon.application.v1

import com.mvp.hackathon.domain.model.exception.Exceptions
import com.mvp.hackathon.domain.model.punch.PunchTheClockDTO
import com.mvp.hackathon.domain.service.auth.ISecurityService
import com.mvp.hackathon.domain.service.encryption.IEncryptionService
import com.mvp.hackathon.domain.service.punch.IPunchTheClockService
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/punch-the-clock")
class PunchTheClockController(
    private val service: IPunchTheClockService,
    private val securityService: ISecurityService,
    private val encryptionService: IEncryptionService
) {

    @PostMapping("/start")
    @PreAuthorize("isAuthenticated()")
    fun startWork(@RequestParam username: String): PunchTheClockEntity {
        if (!securityService.isCurrentUser(encryptionService.encrypt(username))) {
            throw Exceptions.AccessDeniedException("You do not have permission to access this resource.")
        }
        return service.recordStartTime(username, LocalDateTime.now())
    }

    @PostMapping("/end")
    @PreAuthorize("isAuthenticated()")
    fun endWork(@RequestParam username: String): PunchTheClockEntity {
        if (!securityService.isCurrentUser(encryptionService.encrypt(username))) {
            throw Exceptions.AccessDeniedException("You do not have permission to access this resource.")
        }
        return service.recordEndTime(username, LocalDateTime.now())
    }

    @PostMapping("/break-start")
    @PreAuthorize("isAuthenticated()")
    fun addBreakStart(@RequestParam username: String): PunchTheClockEntity {
        if (!securityService.isCurrentUser(encryptionService.encrypt(username))) {
            throw Exceptions.AccessDeniedException("You do not have permission to access this resource.")
        }
        return service.startBreak(username, LocalDateTime.now())
    }

    @PostMapping("/break-end")
    @PreAuthorize("isAuthenticated()")
    fun addBreakEnd(@RequestParam username: String): PunchTheClockEntity {
        if (!securityService.isCurrentUser(encryptionService.encrypt(username))) {
            throw Exceptions.AccessDeniedException("You do not have permission to access this resource.")
        }
        return service.endBreak(username, LocalDateTime.now())
    }


    @Operation(summary = "View-punch-in-today")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @GetMapping("/view-punch-in-today")
    @PreAuthorize("isAuthenticated()")
    fun viewEntries(@RequestParam username: String): MutableList<PunchTheClockDTO> {
        if (!securityService.isCurrentUser(encryptionService.encrypt(username))) {
            throw Exceptions.AccessDeniedException("You do not have permission to access this resource.")
        }
        return service.calculateTotalHours(username)
    }
}
