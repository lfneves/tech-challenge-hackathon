package com.mvp.hackathon.domain.service.report

import com.mvp.hackathon.domain.service.email.EmailServiceImpl
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.infrastructure.entity.time.BreakPeriod
import com.mvp.hackathon.infrastructure.entity.time.PunchTheClockEntity
import com.mvp.hackathon.infrastructure.repository.time.IPunchTheClockRepository
import com.mvp.hackathon.infrastructure.repository.user.IUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random


@Service
class ReportService(
    private val repository: IPunchTheClockRepository,
    private val emailServiceImpl: EmailServiceImpl,
    private val encryptionService: EncryptionService,
    private val IUserRepository: IUserRepository
) : IReportService {

    @Transactional
    override fun findEntriesFromLastMonth(username: String): List<PunchTheClockEntity> {
        val user = IUserRepository.findByUsername(encryptionService.encrypt(username)).get()
        val today = LocalDate.now()
        val firstDayOfLastMonth = today.minusMonths(1).withDayOfMonth(1)
        val lastDayOfLastMonth = today.withDayOfMonth(1).minusDays(1)

        val report = repository.findByDateBetween(firstDayOfLastMonth, lastDayOfLastMonth)
        val decryptedReport = decryptUsernamesInEntities(report, encryptionService)
        try {
            emailServiceImpl
                .sendSimpleMessage(encryptionService.decrypt(user.email), "Report Last Month", decryptedReport.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        return report
    }

    private fun decryptUsernamesInEntities(punchTheClockEntities: List<PunchTheClockEntity>, encryptionService: EncryptionService): List<PunchTheClockEntity> {
        return punchTheClockEntities.map { entity ->
            val decryptedUsername = encryptionService.decrypt(entity.username)
            entity.copy(username = decryptedUsername)
        }
    }

    override fun generateRandomPunchTheClockEntities(username: String, numberOfEntries: Int) {
        val lastMonth = LocalDate.now().minusMonths(1)
        val startOfLastMonth = lastMonth.withDayOfMonth(1)
        val endOfLastMonth = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth())

        repeat(numberOfEntries) {
            val randomDate = LocalDate.ofEpochDay(
                Random.nextLong(startOfLastMonth.toEpochDay(), endOfLastMonth.toEpochDay())
            )

            val startTime = LocalDateTime.of(randomDate, LocalTime.of(Random.nextInt(9, 12), Random.nextInt(0, 59)))
            val endTime = LocalDateTime.of(randomDate, LocalTime.of(Random.nextInt(13, 18), Random.nextInt(0, 59)))

            val breaks = List(Random.nextInt(3)) {
                val breakStart = startTime.plusMinutes(Random.nextLong(Duration.between(startTime, endTime).toMinutes() / 3))
                val breakEnd = breakStart.plusMinutes(Random.nextLong(15, 60))
                BreakPeriod(breakStart, breakEnd)
            }.toMutableList()

            val entity = PunchTheClockEntity(
                username = encryptionService.encrypt(username),
                date = randomDate,
                startTime = startTime,
                endTime = endTime,
                breaks = breaks
            )
            repository.save(entity)
        }
    }
}