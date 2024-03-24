package com.mvp.hackathon.application.service.encrypt

import com.mvp.hackathon.domain.service.encryption.EncryptionService
import org.apache.commons.codec.binary.Base64
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EncryptionServiceTest {

    private lateinit var encryptionService: EncryptionService

    @BeforeEach
    fun setUp() {
        val key = ByteArray(32) { 0 }
        val encodedKey = Base64.encodeBase64String(key)
        encryptionService = EncryptionService(encodedKey)
    }

    @Test
    fun `encrypt should return a different string`() {
        val originalData = "This is a test string"

        // Encrypt the data
        val encryptedData = encryptionService.encrypt(originalData)

        // Check that the encrypted string is not the same as the original
        assertNotEquals(originalData, encryptedData, "Encrypted data should not match the original data.")
    }

    @Test
    fun `encrypt and decrypt should return original data`() {
        val originalData = "This is a test string"

        // Encrypt the data
        val encryptedData = encryptionService.encrypt(originalData)

        // Decrypt the data
        val decryptedData = encryptionService.decrypt(encryptedData)

        // Verify the decrypted data matches the original data
        assertEquals(originalData, decryptedData, "Decrypted data should match the original data.")
    }
}
