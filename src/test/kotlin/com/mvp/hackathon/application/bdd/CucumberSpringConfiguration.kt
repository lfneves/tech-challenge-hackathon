package com.mvp.hackathon.application.bdd

import com.mvp.hackathon.PaymentApplication
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@CucumberContextConfiguration
@SpringBootTest(classes = [PaymentApplication::class])
@ActiveProfiles("test")
class CucumberSpringConfiguration
