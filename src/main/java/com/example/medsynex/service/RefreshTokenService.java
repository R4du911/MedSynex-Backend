package com.example.medsynex.service;

import com.example.medsynex.config.JwtUtils;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.RefreshToken;
import com.example.medsynex.model.User;
import com.example.medsynex.repository.RefreshTokenRepository;
import com.example.medsynex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public void deleteRefreshTokenForUser(Long userId) {
        refreshTokenRepository.deleteRefreshTokenFromUser(userId);
    }


    public void createRefreshToken(String uuid, Long userId) {
        RefreshToken rt = new RefreshToken();
        rt.setRefreshToken(uuid);
        rt.setExpiryDate(Instant.now().plusSeconds(84000));

        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            rt.setUser(user.get());
            refreshTokenRepository.save(rt);
        }
    }


    public String exchangeRefreshToken(String refreshToken) throws BusinessException {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(refreshToken);
        if(refreshTokenOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionCode.INVALID_REFRESH_TOKEN);
        }
        RefreshToken rt = refreshTokenOptional.get();
        if(rt.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(rt);
            throw new BusinessException(BusinessExceptionCode.EXPIRED_REFRESH_TOKEN);
        }
        return jwtUtils.generateJwtToken(userDetailsService.loadUserByUsername(rt.getUser().getUsername()));
    }

}
