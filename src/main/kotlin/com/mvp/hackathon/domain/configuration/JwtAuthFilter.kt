package com.mvp.hackathon.domain.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.mvp.hackathon.domain.model.auth.ApiErrorResponse
import com.mvp.hackathon.domain.service.user.UserDetailsServiceImpl
import com.mvp.hackathon.domain.configuration.jwt.JWTUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
@Profile("!test")
class JwtAuthFilter(private val userDetailsService: UserDetailsServiceImpl, private val objectMapper: ObjectMapper) :
    OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")

            var token: String? = null
            var username: String? = null
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7)
                username = JWTUtils.extractUsername(token)
            }

            if (token == null) {
                filterChain.doFilter(request, response)
                return
            }

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(username)
                if (JWTUtils.validateToken(token, userDetails)) {
                    val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, null, null)
                    authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authenticationToken
                }
            }

            filterChain.doFilter(request, response)
        } catch (e: AccessDeniedException) {
            val errorResponse = ApiErrorResponse(HttpServletResponse.SC_FORBIDDEN, e.stackTraceToString())
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.writer.write(toJson(errorResponse))
        }
    }

    private fun toJson(response: ApiErrorResponse): String {
        return try {
            objectMapper.writeValueAsString(response)
        } catch (e: Exception) {
            "" // Return an empty string if serialization fails
        }
    }
}