package com.mvp.hackathon.domain.service.email

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service


@Service
class EmailServiceImpl(private val emailSender: JavaMailSender) : IEmailService {

    override fun sendSimpleMessage(to: String, subject: String, text: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = subject
        message.text = jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(text)
        emailSender.send(message)
    }
}
