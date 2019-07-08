package com.example.repository;

import com.example.domain.BaseShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseShareRepository extends JpaRepository<BaseShare, Integer> {
    BaseShare getByCode(String code);
}
