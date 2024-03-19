package com.mvp.hackathon.domain.service.punch

import com.mvp.hackathon.infrastructure.entity.time.BreakPeriod
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import com.mvp.hackathon.infrastructure.repository.time.PunchTheClockRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class PunchTheClockService(
    private val repository: PunchTheClockRepository
) {

    fun createTimeEntry(userId: String): PunchTheClockEntity {
        return repository.save(PunchTheClockEntity(userId = userId, date = LocalDate.now()))
    }

    fun recordStartTime(userId: String, dateTime: LocalDateTime): PunchTheClockEntity {
        val entry = repository.findByUserIdAndDate(userId, LocalDate.now()) ?: createTimeEntry(userId)
        entry.startTime = dateTime
        return repository.save(entry)
    }

    fun recordEndTime(userId: String, dateTime: LocalDateTime): PunchTheClockEntity {
        val entry = repository.findByUserIdAndDate(userId, LocalDate.now())
            ?: throw IllegalStateException("Time entry does not exist for today.")
        entry.endTime = dateTime
        return repository.save(entry)
    }

    fun startBreak(userId: String, dateTime: LocalDateTime): PunchTheClockEntity {
        val today = LocalDate.now()
        val entry = repository.findByUserIdAndDate(userId, today)
            ?: throw IllegalStateException("Time entry does not exist for today.")

        entry.breaks.add(BreakPeriod(startTime = dateTime, endTime = dateTime)) // Temporary endTime, to be updated
        return repository.save(entry)
    }

    fun endBreak(userId: String, breakStartTime: LocalDateTime, dateTime: LocalDateTime): PunchTheClockEntity {
        val today = LocalDate.now()
        val entry = repository.findByUserIdAndDate(userId, today)
            ?: throw IllegalStateException("Time entry does not exist for today.")
        val breakPeriod = entry.breaks.find { it.startTime == breakStartTime }
            ?: throw IllegalStateException("Break start time not found.")
        breakPeriod.endTime = dateTime
        return repository.save(entry)
    }

    fun calculateTotalHours(userId: String): Double {
        val entry = repository.findById(userId).orElseThrow {
            IllegalArgumentException("Entry not found with id: $userId")
        }
        entry.startTime ?: return 0.0
        entry.endTime ?: return 0.0

        val totalWorkDuration = Duration.between(entry.startTime, entry.endTime).toMinutes()

        val totalBreaksDuration = entry.breaks.sumOf { breakPeriod ->
            Duration.between(breakPeriod.startTime, breakPeriod.endTime).toMinutes()
        }

        val effectiveWorkDuration = totalWorkDuration - totalBreaksDuration
        return effectiveWorkDuration / 60.0
    }
}