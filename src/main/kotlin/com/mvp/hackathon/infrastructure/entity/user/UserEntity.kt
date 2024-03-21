package com.mvp.hackathon.infrastructure.entity.user

import com.mvp.hackathon.domain.model.user.UserDTO
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "user")
data class UserEntity(
    @Id var id: String? = null,
    var username: String,
    var password: String,
    var email: String,
    val className: String = "com.mvp.hackathon.infrastructure.entity.user.UserEntity"
) {
    fun toDTO(): UserDTO {
        return UserDTO(
            id = this.id,
            password = this.password,
            username = this.username,
            email = this.email
        )
    }
}
