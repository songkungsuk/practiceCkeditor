package com.sbl.demo.sblproject.common.config.filter;

import com.sbl.demo.sblproject.common.NullHelper;
import com.sbl.demo.sblproject.common.config.security.TokenProvider;
import com.sbl.demo.sblproject.domain.web.enums.TokenGrantType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "refreshToken";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);
        String requestURI = request.getRequestURI();

        // 토큰 유효성 검사
        if (!NullHelper.isEmpty(accessToken)) {
            // accessToken 검증
            final boolean validateAccessToken = tokenProvider.validateToken(accessToken);
            if (validateAccessToken) { // accessToken 유효
                this.setAuthentication(accessToken, requestURI);
            } else if (!NullHelper.isEmpty(refreshToken)) { // accessToken 만료, refreshToken 존재
                // refreshToken 검증
                final boolean validateRefreshToken = tokenProvider.validateToken(refreshToken);
                if (validateRefreshToken) {
                    final Claims claims = tokenProvider.parseClaims(accessToken);
                    final long exp = Long.parseLong(claims.get(Claims.EXPIRATION).toString());
                    final String newAccessToken = tokenProvider.createToken(claims.getSubject(), claims.get("auth").toString(), exp);
                    // response header에 jwt token에 넣어줌
                    response.setHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER, TokenGrantType.Bearer.name() + " " + newAccessToken);
                    this.setAuthentication(newAccessToken, requestURI);
                }
            }
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: " + requestURI);
        }

        filterChain.doFilter(request, response);
    }

    // SecurityContext 에 Authentication 객체 저장
    private void setAuthentication(String accessToken, String requestURI) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("Security Context 에 '" + authentication.getName() + "' 인증 정보를 저장했습니다, uri: " + requestURI);
    }

    // Request Header 에서 accessToken 정보 추출
    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TokenGrantType.Bearer.name() + " ")) {
            return bearerToken.substring(TokenGrantType.Bearer.name().length() + 1);
        }

        return null;
    }

    // Request Header 에서 refreshToken 정보 추출
    private String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(REFRESH_TOKEN_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TokenGrantType.Bearer.name() + " ")) {
            return bearerToken.substring(TokenGrantType.Bearer.name().length() + 1);
        }

        return null;
    }
}
