package com.sbl.demo.sblproject.controller.api;

import com.sbl.demo.sblproject.common.Base;
import com.sbl.demo.sblproject.common.config.filter.JwtAuthenticationFilter;
import com.sbl.demo.sblproject.common.config.security.TokenProvider;
import com.sbl.demo.sblproject.common.config.security.direct.PrincipalDetails;
import com.sbl.demo.sblproject.domain.User;
import com.sbl.demo.sblproject.domain.web.dto.LoginDto;
import com.sbl.demo.sblproject.domain.web.dto.TokenDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginResource extends Base {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Validated @RequestBody LoginDto loginDto,
        HttpSession session) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginDto.getUserId(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final TokenDto tokenDto = tokenProvider.generateTokenDTO(authentication);
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        user.setUsiPwd("");
        session.setAttribute("user", user);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationFilter.AUTHORIZATION_HEADER,
            tokenDto.getGrantType() + " " + tokenDto.getAccessToken());
        httpHeaders.add(JwtAuthenticationFilter.REFRESH_TOKEN_HEADER,
            tokenDto.getGrantType() + " " + tokenDto.getRefreshToken());

        return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
    }
}
