package com.mvp.hackathon.application.service.reports

import com.mvp.hackathon.domain.service.email.EmailServiceImpl
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.domain.service.report.ReportService
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import com.mvp.hackathon.infrastructure.entity.user.UserEntity
import com.mvp.hackathon.infrastructure.repository.time.IPunchTheClockRepository
import com.mvp.hackathon.infrastructure.repository.user.IUserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ReportServiceTest {

    private lateinit var reportService: ReportService
    private lateinit var repository: IPunchTheClockRepository
    private lateinit var emailServiceImpl: EmailServiceImpl
    private lateinit var encryptionService: EncryptionService
    private lateinit var iUserRepository: IUserRepository

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        emailServiceImpl = mockk(relaxed = true)
        encryptionService = mockk(relaxed = true)
        iUserRepository = mockk(relaxed = true)
        reportService = ReportService(repository, emailServiceImpl, encryptionService, iUserRepository)
    }

    @Test
    fun `findEntriesFromLastMonth should fetch entries and send email`() {
        // Given
        val username = "testUser"
        val encryptedUsername = "encryptedTestUser"
        val decryptedEmail = "test@example.com"
        val encryptedPassword = "password"
        val user = UserEntity(username = username, email = encryptedUsername, password = encryptedPassword)
        val entries = listOf<PunchTheClockEntity>()

        every { encryptionService.encrypt(username) } returns encryptedUsername
        every { encryptionService.decrypt(encryptedUsername) } returns decryptedEmail
        every { iUserRepository.findByUsername(encryptedUsername) } returns Optional.of(user)
        every { repository.findByDateBetween(any(), any()) } returns entries

        // When
        val result = reportService.findEntriesFromLastMonth(username)

        // Then
        verify { emailServiceImpl.sendSimpleMessage(decryptedEmail, "Report Last Month", entries.toString()) }
        assertEquals(entries, result)
    }

    @Test
    fun `generateRandomPunchTheClockEntities should create specified number of entries`() {
        // Given
        val username = "testUser"
        val encryptedUsername = "encryptedTestUser"
        val numberOfEntries = 5

        every { encryptionService.encrypt(username) } returns encryptedUsername

        val slot = slot<PunchTheClockEntity>()
        every { repository.save(capture(slot)) } answers { slot.captured }

        // When
        reportService.generateRandomPunchTheClockEntities(username, numberOfEntries)

        // Then
        verify(exactly = numberOfEntries) { repository.save(any()) }
    }
}
