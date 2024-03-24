package com.mvp.hackathon.application.service.punch

import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.domain.service.punch.PunchTheClockService
import com.mvp.hackathon.infrastructure.entity.time.BreakPeriod
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import com.mvp.hackathon.infrastructure.repository.time.IPunchTheClockRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime


@ActiveProfiles("test")
class PunchTheClockServiceTest {

    private val repository: IPunchTheClockRepository = mockk(relaxed = true)
    private val encryptionService: EncryptionService = mockk(relaxed = true)

    private var punchTheClockEntity: PunchTheClockEntity = mockk(relaxed = true)

    private lateinit var punchTheClockService: PunchTheClockService

    @BeforeEach
    fun setUp() {
        punchTheClockService = PunchTheClockService(repository, encryptionService)
        punchTheClockEntity = PunchTheClockEntity(null,
            username = encryptionService.encrypt("testUser"),
            date = LocalDate.now(),
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            breaks = mutableListOf())
    }

    @Test
    fun `recordStartTime should save new entry when no existing entry for today`() {
        // Given
        val username = "testUser"
        val dateTime = LocalDateTime.now()
        val encryptedUsername = "encryptedUsername"
        every { encryptionService.encrypt(username) } returns encryptedUsername
        every { repository.findListByUsernameAndDate(encryptedUsername, LocalDate.now()) } returns emptyList()
        every { repository.save(any()) } answers { it.invocation.args[0] as PunchTheClockEntity } // Simulate saving by returning the entity

        // When
        val savedEntry = punchTheClockService.recordStartTime(username, dateTime)

        // Then
        verify(exactly = 1) { repository.save(any()) }
        assertEquals(encryptedUsername, savedEntry.username)
        assertEquals(LocalDate.now(), savedEntry.date)
        assertEquals(dateTime, savedEntry.startTime)
    }

    @Test
    fun `recordStartTime should update most recent entry's start time if it has no end time`() {
        // Given
        val username = "testUser"
        val dateTime = LocalDateTime.now()
        val encryptedUsername = "encryptedUsername"
        val existingEntry = PunchTheClockEntity(username = encryptedUsername, date = LocalDate.now(), startTime = LocalDateTime.MIN, endTime = null)
        every { encryptionService.encrypt(username) } returns encryptedUsername
        every { repository.findListByUsernameAndDate(encryptedUsername, LocalDate.now()) } returns listOf(existingEntry)
        every { repository.save(any()) } answers { it.invocation.args[0] as PunchTheClockEntity }

        // When
        val updatedEntry = punchTheClockService.recordStartTime(username, dateTime)

        // Then
        verify(exactly = 1) { repository.save(any()) }
        assertEquals(dateTime, updatedEntry.startTime) // Verify the start time was updated
    }

    @Test
    fun `recordEndTime should set endTime for the latest entry with null endTime`() {
        // Given
        val username = "testUser"
        val encryptedUsername = "encryptedTestUser"
        val dateTime = LocalDateTime.now()
        val date = LocalDate.now()
        val entryWithNullEndTime = PunchTheClockEntity(
            username = encryptedUsername,
            date = date,
            startTime = dateTime.minusHours(1),
            endTime = null
        )

        val slot = slot<PunchTheClockEntity>()

        every { encryptionService.encrypt(username) } returns encryptedUsername
        every { repository.findListByUsernameAndDate(encryptedUsername, date) } returns listOf(entryWithNullEndTime)
        every { repository.save(capture(slot)) } answers { slot.captured }

        // When
        val result = punchTheClockService.recordEndTime(username, dateTime)

        // Then
        verify(exactly = 1) { repository.save(any()) }
        assertEquals(dateTime, slot.captured.endTime)
        assertEquals(encryptedUsername, slot.captured.username)
        assertEquals(date, slot.captured.date)
        assertEquals(result, slot.captured)
    }

    @Test
    fun `startBreak should add a break period to the latest entry without an endTime`() {
        // Given
        val username = "testUser"
        val dateTime = LocalDateTime.now()
        val encryptedUsername = "encryptedUsername"
        val today = LocalDate.now()
        val entry = PunchTheClockEntity(
            username = encryptedUsername,
            date = today,
            startTime = dateTime.minusHours(1),
            endTime = null, // signifies that the break hasn't ended
            breaks = mutableListOf() // initially, no breaks
        )

        // Setup mocks
        every { encryptionService.encrypt(username) } returns encryptedUsername
        every { repository.findListByUsernameAndDate(encryptedUsername, today) } returns listOf(entry)
        val slot = slot<PunchTheClockEntity>()
        every { repository.save(capture(slot)) } answers { it.invocation.args[0] as PunchTheClockEntity }

        // When
        punchTheClockService.startBreak(username, dateTime)

        // Then
        verify(exactly = 1) { repository.save(any()) }
        assertTrue {
            // Check if a break period was added correctly
            slot.captured.breaks.any {
                it.startTime == dateTime && it.endTime == null
            }
        }
    }

    @Test
    fun `endBreak should set endTime for the last break period without an endTime`() {
        // Given
        val username = "testUser"
        val startBreakTime = LocalDateTime.now().minusMinutes(30)
        val endBreakTime = LocalDateTime.now()
        val encryptedUsername = "encryptedTestUser"
        val today = LocalDate.now()
        val breakPeriod = BreakPeriod(startTime = startBreakTime, endTime = null)
        val entryWithOngoingBreak = PunchTheClockEntity(
            username = encryptedUsername,
            date = today,
            startTime = LocalDateTime.now().minusHours(2),
            endTime = null,
            breaks = mutableListOf(breakPeriod)
        )

        every { encryptionService.encrypt(username) } returns encryptedUsername
        every { repository.findListByUsernameAndDate(encryptedUsername, today) } returns listOf(entryWithOngoingBreak)

        val slot = slot<PunchTheClockEntity>()
        every { repository.save(capture(slot)) } answers { slot.captured }

        // When
        val result = punchTheClockService.endBreak(username, endBreakTime)

        // Then
        verify(exactly = 1) { repository.save(any()) }
        val savedEntry = slot.captured
        assertEquals(endBreakTime, savedEntry.breaks.first().endTime)
        assertEquals(result, savedEntry)
    }

    @Test
    fun `calculateTotalHours should correctly calculate total work hours excluding breaks`() {
        // Given
        val username = "testUser"
        val encryptedUsername = "encryptedTestUser"
        val today = LocalDate.now()
        val startTime = LocalDateTime.now().minusHours(8)
        val endTime = LocalDateTime.now() // Work ended now
        val breakStartTime = LocalDateTime.now().minusHours(4)
        val breakEndTime = LocalDateTime.now().minusHours(3)

        every { encryptionService.encrypt(username) } returns encryptedUsername
        every { repository.findListByUsernameAndDate(encryptedUsername, today) } returns listOf(
            PunchTheClockEntity(
                username = encryptedUsername,
                date = today,
                startTime = startTime,
                endTime = endTime,
                breaks = mutableListOf(BreakPeriod(startTime = breakStartTime, endTime = breakEndTime))
            )
        )

        val expectedTotalHours = "7 hours and 0 minutes"

        // When
        val result = punchTheClockService.calculateTotalHours(username)

        // Then
        assertTrue(result.isNotEmpty(), "Result should not be empty")
        assertEquals(1, result.size, "Expected result size to be 1")
        assertEquals(expectedTotalHours, result.first().totalHoursDay, "Total hours calculated incorrectly")
    }
}
