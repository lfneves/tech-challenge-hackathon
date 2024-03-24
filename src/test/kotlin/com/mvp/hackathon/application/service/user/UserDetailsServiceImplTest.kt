package com.mvp.hackathon.application.service.user

import com.mvp.hackathon.domain.service.user.UserDetailsServiceImpl
import com.mvp.hackathon.infrastructure.entity.user.UserEntity
import com.mvp.hackathon.infrastructure.repository.user.IUserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*

class UserDetailsServiceImplTest {

    private lateinit var userDetailsServiceImpl: UserDetailsServiceImpl
    private lateinit var repository: IUserRepository

    @BeforeEach
    fun setUp() {
        repository = mockk()
        userDetailsServiceImpl = UserDetailsServiceImpl(repository)
    }

    @Test
    fun `loadUserByUsername should return UserDetails when user exists`() {
        // Given
        val username = "testUser"
        val encryptedUsername = "encryptedTestUser"
        val encryptedPassword = "password"
        val userEntity = UserEntity(username = username, email = encryptedUsername, password = encryptedPassword)
        every { repository.findByUsername(username) } returns Optional.of(userEntity)

        // When
        val result = userDetailsServiceImpl.loadUserByUsername(username)

        // Then
        verify(exactly = 1) { repository.findByUsername(username) }
        assertEquals(username, result.username)
        assertEquals(encryptedPassword, result.password)
    }

    @Test
    fun `loadUserByUsername should throw UsernameNotFoundException when user does not exist`() {
        // Given
        val username = "nonExistentUser"
        every { repository.findByUsername(username) } returns Optional.empty()

        // When & Then
        assertThrows(UsernameNotFoundException::class.java) {
            userDetailsServiceImpl.loadUserByUsername(username)
        }
    }
}
