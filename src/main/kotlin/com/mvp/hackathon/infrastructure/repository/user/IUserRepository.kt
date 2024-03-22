package com.mvp.hackathon.infrastructure.repository.user

import com.mvp.hackathon.infrastructure.entity.user.UserEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface IUserRepository : MongoRepository<UserEntity, String> {

    fun findByUsername(username: String?): Optional<UserEntity>
}