package com.mvp.hackathon.domain.service.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service


@Service
class SecurityServiceImpl : ISecurityService {

    override fun isCurrentUser(username: String): Boolean {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            return false
        }
        return when (val principal = authentication.principal) {
            is UserDetails -> principal.username == username
            else -> principal.toString() == username
        }
    }
}