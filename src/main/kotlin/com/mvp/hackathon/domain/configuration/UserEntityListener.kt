package com.mvp.hackathon.domain.configuration

import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.infrastructure.entity.user.UserEntity
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

//@Component
//class UserEntityListener(private val encryptionService: EncryptionService) : ApplicationListener<BeforeSaveEvent<UserEntity>> {
//
//    override fun onApplicationEvent(event: BeforeSaveEvent<UserEntity>) {
//        val user = event.source
//        if (user.username.isNotEmpty()) {
//            user.username = encryptionService.encrypt(user.username)
//        }
//    }
//
//    @EventListener
//    fun handleAfterLoad(event: AfterLoadEvent<UserEntity>) {
//        val document = event.document
//        val encryptedUsername = document?.getString("username")
//        if (encryptedUsername != null) {
//            val decryptedUsername = encryptionService.decrypt(encryptedUsername)
//            document.put("username", decryptedUsername)
//        }
//    }
//}
