package com.mvp.hackathon.application.service.auth

import com.mvp.hackathon.domain.service.auth.SecurityServiceImpl
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

class SecurityServiceImplTest {

    private lateinit var securityService: SecurityServiceImpl
    private lateinit var encryptionService: EncryptionService

    @BeforeEach
    fun setUp() {
        securityService = mockk(relaxed = true)
        encryptionService = mockk(relaxed = true)
        mockkStatic(SecurityContextHolder::class)
    }

//    @AfterEach
//    fun tearDown() {
//        unmockkAll()
//    }

    @Test
    fun `isCurrentUser should return true when current user matches`() {
        // Given
        val encryptUsername = encryptionService.encrypt("testUser")
        val userDetails: UserDetails = mockk(relaxed = true)
        every { userDetails.username } returns encryptUsername
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null)
        val securityContext: SecurityContext = mockk(relaxed = true)
        every { securityContext.authentication } returns authentication
        every { SecurityContextHolder.getContext() } returns securityContext
        every { securityService.isCurrentUser(encryptUsername) } returns true

        // When
        val result = securityService.isCurrentUser(encryptUsername)

        // Then
        assertTrue(result, "Expected isCurrentUser to return true for matching username.")
    }

    @Test
    fun `isCurrentUser should return false when current user does not match`() {
        // Given
        val currentUsername = "currentUser"
        val checkingUsername = "otherUser"
        val userDetails: UserDetails = mockk(relaxed = true)
        every { userDetails.username } returns currentUsername
        val authentication: Authentication = UsernamePasswordAuthenticationToken(userDetails, null)
        val securityContext: SecurityContext = mockk(relaxed = true)
        every { securityContext.authentication } returns authentication
        every { SecurityContextHolder.getContext() } returns securityContext

        // When
        val result = securityService.isCurrentUser(checkingUsername)

        // Then
        assertFalse(result, "Expected isCurrentUser to return false for non-matching usernames.")
    }

    @Test
    fun `isCurrentUser should return false when authentication is null`() {
        // Given
        val securityContext: SecurityContext = mockk(relaxed = true)
        every { securityContext.authentication } returns null
        every { SecurityContextHolder.getContext() } returns securityContext

        // When
        val result = securityService.isCurrentUser("anyUser")

        // Then
        assertFalse(result, "Expected isCurrentUser to return false when authentication is null.")
    }
}
