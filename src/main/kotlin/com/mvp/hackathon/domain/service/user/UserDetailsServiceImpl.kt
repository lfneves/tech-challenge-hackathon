package com.mvp.hackathon.domain.service.user

import com.mvp.hackathon.domain.model.auth.AuthClientDTO
import com.mvp.hackathon.infrastructure.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl @Autowired constructor(
    private val repository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val userEntity = repository.findByUsername(username)
        if(!userEntity.isPresent) {
            throw UsernameNotFoundException("User does not exist with username: $username")
        }
        return AuthClientDTO(
            userEntity.get().username,
            userEntity.get().password
        )
    }
}