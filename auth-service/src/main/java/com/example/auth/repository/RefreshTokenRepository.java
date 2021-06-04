package com.example.auth.repository;

import com.example.auth.model.entity.Account;
import com.example.auth.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, String> {

    RefreshToken findByToken(String token);

    void deleteByAccount(Account account);

}
