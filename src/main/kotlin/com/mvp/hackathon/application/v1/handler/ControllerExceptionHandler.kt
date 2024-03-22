package com.mvp.hackathon.application.v1.handler

import com.mvp.hackathon.domain.model.auth.ApiErrorResponse
import com.mvp.hackathon.domain.model.exception.Exceptions
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.util.function.Consumer

@ControllerAdvice
class ControllerExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(Exceptions.NotFoundException::class)
    fun handleNotFoundException(e: Exceptions.NotFoundException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse(HttpStatus.NOT_FOUND.value(), e.message!!))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleRequestNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        val errors: MutableList<String> = ArrayList()
        e.bindingResult
            .fieldErrors.forEach(Consumer { error: FieldError -> errors.add(error.field + ": " + error.defaultMessage) })
        e.bindingResult
            .globalErrors
            .forEach(Consumer { error: ObjectError -> errors.add(error.objectName + ": " + error.defaultMessage) })

        val message = "Validation of request failed: ${errors.joinToString(", ")}"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), message))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"))
    }

    @ExceptionHandler(Exceptions.DuplicateException::class)
    fun handleDuplicateException(e: Exceptions.DuplicateException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                e.message!!
            )
        )
    }

    @ExceptionHandler(InternalAuthenticationServiceException::class)
    fun handleInternalAuthenticationServiceException(e: InternalAuthenticationServiceException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                e.message!!
            )
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException, request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        logger.info("Bad Request: ${ex.message}")
        val apiErrorResponse = ApiErrorResponse(
            errorCode = 400,
            description = "BAD_REQUEST: The request is malformed or invalid."
        )
        return ResponseEntity(apiErrorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                e.message!!
            )
        )
    }
}