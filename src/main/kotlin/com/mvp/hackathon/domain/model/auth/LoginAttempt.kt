package com.mvp.hackathon.domain.model.auth

import com.mvp.hackathon.infrastructure.entity.auth.LoginAttemptEntity
import java.time.LocalDateTime

data class LoginAttempt(
    val username: String,
    val success: Boolean,
    val createdAt: LocalDateTime
) {
    fun toEntity(): LoginAttemptEntity {
        return LoginAttemptEntity(
            username = this.username,
            success = this.success,
            createdAt = this.createdAt
        )
    }
}