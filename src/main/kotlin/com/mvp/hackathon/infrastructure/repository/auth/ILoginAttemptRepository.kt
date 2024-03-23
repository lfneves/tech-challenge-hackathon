package com.mvp.hackathon.infrastructure.repository.auth

import com.mvp.hackathon.infrastructure.entity.auth.LoginAttemptEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface ILoginAttemptRepository : MongoRepository<LoginAttemptEntity, String> {

    fun findByUsername(username: String): List<LoginAttemptEntity>
}