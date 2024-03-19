package com.mvp.hackathon.domain.service.auth

import com.mvp.hackathon.domain.model.user.UserDTO
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.infrastructure.entity.user.UserEntity
import com.mvp.hackathon.infrastructure.repository.user.UserRepository
import com.mvp.hackathon.shared.ErrorMsgConstants
import com.mvp.order.domain.model.auth.ResponseSignupDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
@Profile("!test")
class AuthServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val encryptionService: EncryptionService
): AuthService {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun signup(user: UserDTO): ResponseSignupDTO {
        val existingUser: Optional<UserEntity> = userRepository.findByUsername(encryptionService.encrypt(user.username))
        return if (existingUser.isPresent) {
            ResponseSignupDTO(success = false, message = ErrorMsgConstants.ERROR_USER_ALREADY_EXIST)
        } else {
            user.password = passwordEncoder.encode(user.password)
            user.username = encryptionService.encrypt(user.username)
            user.email = encryptionService.encrypt(user.email)
            userRepository.save(user.toEntity())
            ResponseSignupDTO(success = true, message = "Usuário criado com sucesso.")
        }
    }
}