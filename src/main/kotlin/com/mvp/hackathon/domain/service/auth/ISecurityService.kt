package com.mvp.hackathon.domain.service.auth

interface ISecurityService {
    fun isCurrentUser(username: String): Boolean
}