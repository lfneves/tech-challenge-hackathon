package com.mvp.hackathon.domain.service.encryption

import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


@Service
class EncryptionService(
    @Value("\${encryption.secret-key}") encodedKey: String
) : IEncryptionService {

    final override val algorithm = "AES"
    override val secretKey: SecretKey = decodeSecretKey(encodedKey)

    final override fun decodeSecretKey(encodedKey: String): SecretKey {
        val decodedKey = Base64.decodeBase64(encodedKey)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    override fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeBase64String(encryptedBytes)
    }

    override fun decrypt(data: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.decodeBase64(data))
        return String(decryptedBytes)
    }
}
