package com.mvp.hackathon.domain.configuration

import com.mvp.hackathon.domain.configuration.jwt.JWTUtils
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SecretsConfig {

    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    @PostConstruct
    fun init() {
        JWTUtils.initializeSecret(jwtSecret)
    }
}