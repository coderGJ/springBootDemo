package com.example.repository;

import com.example.domain.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  BaseUserRepository extends JpaRepository<BaseUser, Integer> {

    BaseUser getByUsername(String username);
}
