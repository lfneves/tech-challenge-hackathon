package com.mvp.hackathon.application.bdd

import com.mvp.hackathon.PunchTheClockApplication
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@CucumberContextConfiguration
@SpringBootTest(classes = [PunchTheClockApplication::class])
@ActiveProfiles("test")
class CucumberSpringConfiguration
