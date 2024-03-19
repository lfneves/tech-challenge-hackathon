package com.mvp.hackathon.domain.service.auth

import com.mvp.hackathon.domain.model.auth.LoginAttempt
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.infrastructure.repository.auth.LoginAttemptRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class LoginService @Autowired constructor(
    private val repository: LoginAttemptRepository,
    private val encryptionService: EncryptionService
) {

    @Transactional
    fun addLoginAttempt(username: String, success: Boolean) {
        val loginAttempt = LoginAttempt(encryptionService.encrypt(username), success, LocalDateTime.now())
        repository.save(loginAttempt.toEntity())
    }

    fun findRecentLoginAttempts(username: String): List<LoginAttempt> {
        val loginAttemptEntityList = repository.findByUsername(encryptionService.encrypt(username))

        return loginAttemptEntityList.flatMap { listOf(it.toDTO()) }
    }
}