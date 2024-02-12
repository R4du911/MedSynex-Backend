package com.example.medsynex.repository;

import com.example.medsynex.model.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    @Query("delete from RefreshToken where user.id = :id")
    @Modifying
    void deleteRefreshTokenFromUser(Long id);
}
