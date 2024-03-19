package com.mvp.hackathon.domain.model.auth


import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(

    @NotBlank(message = "Username cannot be blank")
    val username: String = "",

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    val password: String,
)