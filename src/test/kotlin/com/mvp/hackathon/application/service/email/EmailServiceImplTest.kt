package com.mvp.hackathon.application.service.email

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mvp.hackathon.domain.service.email.EmailServiceImpl
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

class EmailServiceImplTest {

    private lateinit var emailSender: JavaMailSender
    private lateinit var emailServiceImpl: EmailServiceImpl

    @BeforeEach
    fun setUp() {
        emailSender = mockk(relaxed = true)
        emailServiceImpl = EmailServiceImpl(emailSender)
    }

    @Test
    fun `sendSimpleMessage should correctly configure and send email`() {
        // Given
        val to = "test@example.com"
        val subject = "Test Subject"
        val text = "This is a test email."

        // When
        emailServiceImpl.sendSimpleMessage(to, subject, text)

        // Then
        verify { emailSender.send(match<SimpleMailMessage> {
            it.to?.contains(to) == true &&
                    it.subject == subject &&
                    it.text == jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(text)
        })}
    }
}
