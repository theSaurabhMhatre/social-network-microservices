package com.example.auth.repository;

import com.example.auth.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository
        extends JpaRepository<Account, String> {

    Account findByHandle(String handle);

    Account findByIdAndHandle(String id, String handle);

}
