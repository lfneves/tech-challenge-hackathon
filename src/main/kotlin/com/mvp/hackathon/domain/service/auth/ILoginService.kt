package com.mvp.hackathon.domain.service.auth

import com.mvp.hackathon.domain.model.auth.LoginAttempt

interface ILoginService {

    fun addLoginAttempt(username: String, success: Boolean)

    fun findRecentLoginAttempts(username: String): List<LoginAttempt>
}