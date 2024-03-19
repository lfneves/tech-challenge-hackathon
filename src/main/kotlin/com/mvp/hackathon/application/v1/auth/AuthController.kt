package com.mvp.hackathon.application.v1.auth

import com.mvp.hackathon.domain.model.auth.ApiErrorResponse
import com.mvp.hackathon.domain.model.auth.LoginAttempt
import com.mvp.hackathon.domain.model.user.UserDTO
import com.mvp.hackathon.domain.model.auth.LoginRequest
import com.mvp.order.domain.model.auth.LoginResponse
import com.mvp.order.domain.model.auth.ResponseSignupDTO
import com.mvp.hackathon.domain.service.auth.AuthService
import com.mvp.hackathon.domain.service.auth.LoginService
import com.mvp.hackathon.domain.service.encryption.EncryptionService
import com.mvp.hackathon.domain.configuration.jwt.JWTUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/auth/")
@Profile("!test")
class AuthController @Autowired constructor(
    private val authService: AuthService,
    private val authenticationManager: AuthenticationManager,
    private val loginService: LoginService,
    private val encryptionService: EncryptionService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "Cadastro de Usuário",
        description = "Cadastro usuário usado quando não possui usuário e senha",
        tags = ["Usuários"]
    )
    @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = LoginResponse::class))])
    @ApiResponse(responseCode = "401", content = [Content(schema = Schema(implementation = ApiErrorResponse::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ApiErrorResponse::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ApiErrorResponse::class))])
    @PostMapping("/signup")
    fun signup(@RequestBody request: @Valid UserDTO): ResponseEntity<ResponseSignupDTO> {
        logger.info("/signup")
        return ResponseEntity.ok(authService.signup(request))
    }

    @Operation(summary = "Authenticate user and return token")
    @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = LoginResponse::class))])
    @ApiResponse(responseCode = "401", content = [Content(schema = Schema(implementation = ApiErrorResponse::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ApiErrorResponse::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ApiErrorResponse::class))])
    @PostMapping(value = ["/login"])
    fun login(@RequestBody request: @Valid LoginRequest): ResponseEntity<out Record> {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(encryptionService.encrypt(request.username), request.password))
            val token: String = JWTUtils.generateToken(encryptionService.encrypt(request.username))
            loginService.addLoginAttempt(request.username, true)
            return ResponseEntity.ok(LoginResponse(token))
        } catch (e: BadCredentialsException) {
            loginService.addLoginAttempt(request.username, false)
            throw e
        }  catch (e: Exception) {
            loginService.addLoginAttempt(request.username, false)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"))
        }
    }

    @Operation(summary = "Authenticate get user login attempts")
    @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = LoginResponse::class))])
    @ApiResponse(responseCode = "401", content = [Content(schema = Schema(implementation = ApiErrorResponse::class))])
    @ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ApiErrorResponse::class))])
    @ApiResponse(responseCode = "500", content = [Content(schema = Schema(implementation = ApiErrorResponse::class))])
    @GetMapping(value = ["/get-login-attempts"])
    fun getLoginAttempts(@RequestBody request: @Valid LoginRequest): ResponseEntity<List<LoginAttempt>> {
        return  ResponseEntity.ok(loginService.findRecentLoginAttempts(request.username))
    }
}