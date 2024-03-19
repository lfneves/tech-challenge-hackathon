package com.mvp.hackathon.domain.model.user

import com.mvp.hackathon.infrastructure.entity.user.UserEntity

data class UserDTO (
    var id: String? = null,
    var username: String,
    var email:String,
    var password: String,
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            id = this.id,
            username = this.username,
            password = this.password,
            email = this.email
        )
    }
}