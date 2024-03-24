package com.mvp.hackathon.application.service.auth

import com.mvp.hackathon.domain.model.user.UserDTO
import com.mvp.hackathon.domain.service.auth.AuthServiceImpl
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.infrastructure.entity.user.UserEntity
import com.mvp.hackathon.infrastructure.repository.user.IUserRepository
import com.mvp.hackathon.shared.ErrorMsgConstants
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AuthServiceImplTest {

    private lateinit var IUserRepository: IUserRepository
    private lateinit var encryptionService: EncryptionService
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var authService: AuthServiceImpl

    @BeforeEach
    fun setUp() {
        IUserRepository = mockk()
        encryptionService = mockk(relaxed = true)
        passwordEncoder = mockk(relaxed = true)
        authService = AuthServiceImpl(IUserRepository, encryptionService)
    }

    @Test
    fun `signup should return error when user already exists`() {
        // Given
        val encryptedUsername = encryptionService.encrypt("encryptedTestUser")
        val encryptedPassword = encryptionService.encrypt("password")
        val encryptedEmail = encryptionService.encrypt("test@example.com")
        val userDTO = UserDTO("123456789", encryptedUsername, encryptedEmail, encryptedPassword)
        every { encryptionService.encrypt(userDTO.username) } returns encryptedUsername
        every { IUserRepository.findByUsername(encryptedUsername) } returns Optional.of(UserEntity(
            id = "123456789",
            username = encryptedUsername,
            password = encryptedPassword,
            email = encryptedEmail
        ))

        // When
        val result = authService.signup(userDTO)

        // Then
        assertFalse(result.success)
        assertEquals(ErrorMsgConstants.ERROR_USER_ALREADY_EXIST, result.message)
    }

    @Test
    fun `signup should check new user when user does exist`() {
        // Given
        val encryptedUsername = encryptionService.encrypt("encryptedTestUser")
        val encryptedPassword = encryptionService.encrypt("password")
        val encryptedEmail = encryptionService.encrypt("test@example.com")
        val userDTO = UserDTO("123456789", encryptedUsername, encryptedEmail, encryptedPassword)
        val userEntity = UserEntity(username = encryptedUsername, email = encryptedEmail, password = encryptedPassword)

        every { encryptionService.encrypt(userDTO.username) } returns encryptedUsername
        every { encryptionService.encrypt(userDTO.email) } returns encryptedEmail
        every { passwordEncoder.encode(userDTO.password) } returns encryptedPassword
        every { IUserRepository.findByUsername(encryptedUsername) } returns Optional.of(userEntity)
        every { IUserRepository.save(any()) } returnsArgument 0

        // When
        val result = authService.signup(userDTO)

        // Then
        assertFalse(result.success)
        assertEquals("Usuário já está cadastrado.", result.message)
    }
}
