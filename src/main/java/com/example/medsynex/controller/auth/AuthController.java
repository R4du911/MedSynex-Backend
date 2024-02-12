package com.example.medsynex.controller.auth;

import com.example.medsynex.config.JwtUtils;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.dto.LoginRequestDTO;
import com.example.medsynex.model.dto.RefreshTokenResponseDTO;
import com.example.medsynex.model.dto.SignInResponseDTO;
import com.example.medsynex.service.RefreshTokenService;
import com.example.medsynex.service.UserDetailsImpl;
import com.example.medsynex.service.UserDetailsServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshTokenCookie";

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) throws BusinessException {

        try {

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String jwt = jwtUtils.generateJwtToken(userDetails);

            String refreshToken = UUID.randomUUID().toString();

            refreshTokenService.deleteRefreshTokenForUser(userDetails.getId());
            refreshTokenService.createRefreshToken(refreshToken, userDetails.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, createCookie(refreshToken).toString());
            SignInResponseDTO response = new SignInResponseDTO(jwt, "", userDetails.getId(),
                    userDetails.getUsername(), userDetails.getEmail(), roles);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } catch (AuthenticationException e) {
            throw new BusinessException(BusinessExceptionCode.INVALID_CREDENTIALS);
        }
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> checkCookie(HttpServletRequest request) throws BusinessException {
        Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals(REFRESH_TOKEN_COOKIE_NAME)).findFirst();
        if (cookie.isPresent()) {
            return ResponseEntity.ok(new RefreshTokenResponseDTO(refreshTokenService.exchangeRefreshToken(cookie.get().getValue())));
        }
        throw new BusinessException(BusinessExceptionCode.MISSING_COOKIE);
    }

    private ResponseCookie createCookie(String token) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .maxAge(Duration.ofDays(1))
                .sameSite("None")
                .path("/auth/refreshToken")
                .build();
    }
}
