package com.mvp.hackathon.domain.service.email

fun interface IEmailService {
    fun sendSimpleMessage(to: String, subject: String, text: String)
}