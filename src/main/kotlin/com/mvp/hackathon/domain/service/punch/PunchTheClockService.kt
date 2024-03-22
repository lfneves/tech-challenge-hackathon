package com.mvp.hackathon.domain.service.punch

import com.mvp.hackathon.domain.model.punch.PunchTheClockDTO
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.infrastructure.entity.time.BreakPeriod
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import com.mvp.hackathon.infrastructure.repository.time.PunchTheClockRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class PunchTheClockService(
    private val repository: PunchTheClockRepository,
    private val encryptionService: EncryptionService
) {

    fun recordStartTime(username: String, dateTime: LocalDateTime): PunchTheClockEntity {
        val encryptedUsername = encryptionService.encrypt(username)
        val entries = repository.findListByUsernameAndDate(encryptedUsername, LocalDate.now())

        val mostRecentEntry = entries.maxByOrNull { it.startTime ?: LocalDateTime.MIN }

        val entry = if (mostRecentEntry == null || mostRecentEntry.endTime != null) {
            PunchTheClockEntity(
                username = encryptedUsername,
                date = LocalDate.now(),
                startTime = dateTime
            )
        } else {
            mostRecentEntry.apply { startTime = dateTime }
        }
        return repository.save(entry)
    }

    fun recordEndTime(username: String, dateTime: LocalDateTime): PunchTheClockEntity {
        val encryptedUsername = encryptionService.encrypt(username)
        val date = LocalDate.now()

        val latestEntryWithNullEndTime =
            repository.findListByUsernameAndDate(encryptedUsername, date)
                .first { it.endTime == null }

        latestEntryWithNullEndTime.endTime = dateTime
        return repository.save(latestEntryWithNullEndTime)
    }

    fun startBreak(username: String, dateTime: LocalDateTime): PunchTheClockEntity {
        val encryptedUsername = encryptionService.encrypt(username)
        val today = LocalDate.now()
        val entry = repository.findListByUsernameAndDate(encryptedUsername, today)
            .first { it.endTime == null }

        entry.breaks.add(BreakPeriod(startTime = dateTime, endTime = null))
        return repository.save(entry)
    }

    fun endBreak(username: String, dateTime: LocalDateTime): PunchTheClockEntity {
        val encryptedUsername = encryptionService.encrypt(username)
        val today = LocalDate.now()
        val entry =  repository.findListByUsernameAndDate(encryptedUsername, today)
            .first { it.endTime == null }
        val breakPeriod = entry.breaks
            .find { it.startTime == entry.breaks.last().startTime
                && entry.breaks.last().startTime!! < dateTime }
            ?: throw IllegalStateException("Break start time not found.")
        breakPeriod.endTime = dateTime
        return repository.save(entry)
    }

    fun calculateTotalHours(username: String): MutableList<PunchTheClockDTO> {
        val encryptedUsername = encryptionService.encrypt(username)
        val entries = repository.findListByUsernameAndDate(encryptedUsername, LocalDate.now())
            .takeIf { it.isNotEmpty() }
            ?: throw IllegalArgumentException("Entries not found with username: $encryptedUsername")

        var totalEffectiveWorkDurationMinutes = 0L

        var response = mutableListOf<PunchTheClockDTO>()
        entries.forEach { entry ->
            val startTime = entry.startTime ?: return@forEach
            val endTime = entry.endTime ?: return@forEach

            val workDuration = Duration.between(startTime, endTime).toMinutes()

            val breaksDuration = entry.breaks.sumOf { breakPeriod ->
                Duration.between(breakPeriod.startTime, breakPeriod.endTime).toMinutes()
            }

            totalEffectiveWorkDurationMinutes += (workDuration - breaksDuration)

            val hours = totalEffectiveWorkDurationMinutes / 60
            val minutes = totalEffectiveWorkDurationMinutes % 60
            response.add(
                PunchTheClockDTO(
                date = entry.date,
                startTime = entry.startTime,
                endTime = entry.endTime,
                breaks = entry.breaks,
                totalHoursDay = "$hours hours and $minutes minutes"
                )
            )
        }
        return response
    }
}