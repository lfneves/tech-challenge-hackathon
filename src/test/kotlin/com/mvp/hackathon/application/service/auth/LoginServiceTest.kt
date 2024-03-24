package com.mvp.hackathon.application.service.auth

import com.mvp.hackathon.domain.service.auth.LoginService
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.infrastructure.entity.auth.LoginAttemptEntity
import com.mvp.hackathon.infrastructure.repository.auth.ILoginAttemptRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime


class LoginServiceTest {

    private lateinit var repository: ILoginAttemptRepository
    private lateinit var encryptionService: EncryptionService
    private lateinit var loginService: LoginService

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        encryptionService = mockk(relaxed = true)
        loginService = LoginService(repository, encryptionService)
    }

    @Test
    fun `addLoginAttempt should save encrypted username and login attempt details`() {
        // Given
        var time = LocalDateTime.now().plusSeconds(1)
        val username = "testUser"
        val success = true
        val encryptedUsername = "encryptedUsername"
        every { encryptionService.encrypt(username) } returns encryptedUsername
        every { repository.save(any()) } returns LoginAttemptEntity(
            username = encryptedUsername, success = true, createdAt = time)

        // When
        loginService.addLoginAttempt(username, success)

        // Then
        verify { repository.save(withArg {
            assertEquals(encryptedUsername, it.username)
            assertEquals(success, it.success)
        })}
    }

    @Test
    fun `findRecentLoginAttempts should return login attempts for given username`() {
        // Given
        val username = "testUser"
        val encryptedUsername =  encryptionService.encrypt("testUser")
        val loginAttemptEntities = listOf(LoginAttemptEntity("1", username, true))
        every { encryptionService.encrypt(username) } returns encryptedUsername
        every { repository.findByUsername(encryptedUsername) } returns loginAttemptEntities

        // When
        val result = loginService.findRecentLoginAttempts(username)

        // Then
        verify { repository.findByUsername(encryptedUsername) }
        verify { encryptionService.encrypt(username) }
        assertEquals(1, result.size)
        assertEquals(true, result.first().success)
    }
}
