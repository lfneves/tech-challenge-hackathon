package com.mvp.hackathon.domain.service.auth

import com.mvp.hackathon.domain.model.user.UserDTO
import com.mvp.order.domain.model.auth.ResponseSignupDTO

fun interface IAuthService {

    fun signup(user: UserDTO): ResponseSignupDTO
}