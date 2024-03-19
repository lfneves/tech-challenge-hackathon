package com.mvp.hackathon.domain.configuration.jwt


import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.SignatureException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object JWTUtils {
    private var logger: Logger = LoggerFactory.getLogger(javaClass)

    private const val MINUTES = 60

    private lateinit var SECRET_STRING: String
    private lateinit var SECRET_KEY: SecretKey

    fun initializeSecret(secretString: String) {
        SECRET_STRING = secretString
        SECRET_KEY = SECRET_STRING.createSecretKeyFromCustomString()
    }

    fun String.createSecretKeyFromCustomString(): SecretKey {
        val decodedKey = Base64.getDecoder().decode(this)
        return SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.jcaName)
    }

    fun generateToken(username: String?): String {
        val now = Instant.now()
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(MINUTES.toLong(), ChronoUnit.MINUTES)))
            .signWith(SECRET_KEY)
            .compact()
    }

    fun extractUsername(token: String): String {
        return getTokenBody(token).subject
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun validateTokenSecret(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (e: Exception) {
            logger.error("Error - Expired Jwt Exception: ${e.stackTraceToString()}")
            return false
        }
    }

    private fun getTokenBody(token: String): Claims {
        try {
            return Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: SignatureException) {
            logger.error("Error - Signature Exception: ${e.stackTraceToString()}")
            throw AccessDeniedException("Access denied: ${e.message}")
        } catch (e: ExpiredJwtException) {
            logger.error("Error - Expired Jwt Exception: ${e.stackTraceToString()}")
            throw AccessDeniedException("Access denied: ${e.message}" )
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        val claims = getTokenBody(token)
        return claims.expiration.before(Date())
    }
}