package edu.school21.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.school21.adapters.UserServiceAdapter;
import edu.school21.dto.response.ErrorInfoRsDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthGatewayFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_NOT_FOUND_MESSAGE = "token is empty";
    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/api/v1/users",
            "/api/v1/images"
    );
    private final ObjectMapper objectMapper;
    private final UserServiceAdapter userServiceAdapter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!isProtectedPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.debug("AuthGatewayFilter.doFilterInternal(): Token not found, token: {}", authHeader);
            ErrorInfoRsDto errorInfoRsDto = new ErrorInfoRsDto(
                    request.getRequestURL().toString(),
                    TOKEN_NOT_FOUND_MESSAGE,
                    HttpStatus.UNAUTHORIZED.value());
            writeUnauthorizedResponse(response, objectMapper.writeValueAsString(errorInfoRsDto));
            return;
        }
        try {
            userServiceAdapter.isUserAuthorize(authHeader);
            filterChain.doFilter(request, response);
        } catch (WebClientResponseException ex) {
            log.debug("AuthGatewayFilter.doFilterInternal().WebClientResponseException: " +
                    "Unauthorized, token: {}, response: {}", authHeader, ex.getResponseBodyAsString());
            writeUnauthorizedResponse(response, ex.getResponseBodyAsString());
        } catch (WebClientRequestException ex) {
            log.debug("AuthGatewayFilter.doFilterInternal().WebClientRequestException: " +
                    "Unauthorized, token: {}, response: {}", authHeader, ex.getMessage());
            writeServerUnavailableResponse(request, response, ex.getMessage());
        }
    }

    private boolean isProtectedPath(String requestUri) {
        return PROTECTED_PATHS.stream().anyMatch(requestUri::startsWith);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String body) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(body);
        response.getWriter().flush();
    }

    private void writeServerUnavailableResponse(HttpServletRequest request,
                                                HttpServletResponse response,
                                                String body) throws IOException {
        ErrorInfoRsDto errorInfoRsDto = new ErrorInfoRsDto(
                request.getRequestURL().toString(),
                body,
                HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorInfoRsDto));
        response.getWriter().flush();
    }
}
