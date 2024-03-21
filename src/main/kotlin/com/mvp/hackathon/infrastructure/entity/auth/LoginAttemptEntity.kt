package com.mvp.hackathon.infrastructure.entity.auth

import com.mvp.hackathon.domain.model.auth.LoginAttempt
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Document(collection = "login_attempt")
data class LoginAttemptEntity(
    @Id
    var id: String? = null,
    var username: String,
    var success: Boolean,
    var createdAt: LocalDateTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime()
) {
    fun toDTO(): LoginAttempt {
        return LoginAttempt(
            username = this.username,
            success = this.success,
            createdAt = this.createdAt
        )
    }
}