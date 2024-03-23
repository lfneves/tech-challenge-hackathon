package com.mvp.hackathon.domain.service.encryption

import javax.crypto.SecretKey

interface IEncryptionService {
    val algorithm: String
    val secretKey: SecretKey
    fun decodeSecretKey(encodedKey: String): SecretKey
    fun encrypt(data: String): String
    fun decrypt(data: String): String
}
