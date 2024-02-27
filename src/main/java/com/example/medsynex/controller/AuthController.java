package com.example.medsynex.controller;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshTokenCookie";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${security.decipherKey}")
    private String key;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) throws BusinessException {

        try {
            String decryptedPassword = decrypt(loginRequest.getPassword(), key);

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), decryptedPassword));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            String jwt = jwtUtils.generateJwtToken(userDetails);

            String refreshToken = UUID.randomUUID().toString();

            refreshTokenService.deleteRefreshTokenForUser(userDetails.getId());
            refreshTokenService.createRefreshToken(refreshToken, userDetails.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, createCookie(refreshToken).toString());
            SignInResponseDTO response = new SignInResponseDTO(jwt, userDetails.isFirstLogin());

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } catch (AuthenticationException e) {
            throw new BusinessException(BusinessExceptionCode.INVALID_CREDENTIALS);
        }
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<RefreshTokenResponseDTO> checkCookie(HttpServletRequest request) throws BusinessException {
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
                .sameSite("Lax")
                .path("/auth/refreshToken")
                .build();
    }

    public static String decrypt(String toDecrypt, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(toDecrypt));
            return new String(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
