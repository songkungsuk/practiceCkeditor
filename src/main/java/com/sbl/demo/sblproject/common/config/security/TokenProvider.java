package com.sbl.demo.sblproject.common.config.security;

import com.sbl.demo.sblproject.common.NullHelper;
import com.sbl.demo.sblproject.common.config.security.direct.CustomUserDetailsService;
import com.sbl.demo.sblproject.domain.web.dto.TokenDto;
import com.sbl.demo.sblproject.domain.web.enums.TokenGrantType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final String secret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliSeconds;
    private final CustomUserDetailsService customUserDetailsService;
    private Key key;

    public TokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
        @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
        CustomUserDetailsService customUserDetailsService) {
        this.secret = secret;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliSeconds = refreshTokenValidityInSeconds * 1000;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDTO(Authentication authentication) {
        // 권한 조회
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        // 토큰 expire 시간 설정
        long now = (new Date()).getTime();
        Date accessTokenValidity = new Date(now + this.accessTokenValidityInMilliseconds);
        Date refreshTokenValidity = new Date(now + this.refreshTokenValidityInMilliSeconds);
        // Access Token
        String accessToken = this.createToken(authentication, authorities, accessTokenValidity);
        // Refresh Token
        String refreshToken = this.createToken(authentication, authorities, refreshTokenValidity);
        return TokenDto
            .builder()
            .grantType(TokenGrantType.Bearer.name())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public String createToken(Authentication authentication, String authorities, Date validity) {
        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities) // 정보 저장
            .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret 값 세팅
            .setExpiration(validity) // set Expire Time 해당 옵션 안넣으면 expire 안함
            .compact();
    }

    public String createToken(String sub, String authorities, long exp) {
        // 토큰 expire 시간 설정
        long now = (new Date()).getTime();
        Date refreshTokenValidity = new Date(now + this.refreshTokenValidityInMilliSeconds);
        System.out.println("newAccessToken validity = " + refreshTokenValidity);
        return Jwts.builder()
            .setSubject(sub)
            .claim(AUTHORITIES_KEY, authorities) // 정보 저장
            .signWith(key, SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘과 , signature 에 들어갈 secret 값 세팅
            .setExpiration(refreshTokenValidity) // set Expire Time 해당 옵션 안넣으면 expire 안함
            .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (NullHelper.isNull(claims.get(AUTHORITIES_KEY))) {
            throw new RuntimeException(
                "The Token has no authorization information."); // 권한 정보 없음 예외처리
        }

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        final UserDetails principal = customUserDetailsService.loadUserByUsername(
            claims.getSubject());
        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("Invalid JWT Token", e); // 잘못된 서명
        } catch (ExpiredJwtException e) {
            final Date expiration = e.getClaims().getExpiration();
            logger.info("accessToken = " + token);
            logger.info("accessToken expiration = " + expiration);
            logger.info("Expired JWT Token", e); // 토큰 만료
        } catch (UnsupportedJwtException e) {
            logger.info("Unsupported JWT Token", e); // 지원되지 않는 토큰
        } catch (IllegalArgumentException e) {
            logger.info("JWT claims string is empty", e); // 잘못된 토큰
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
