package com.example.repository;

import com.example.domain.CoreTransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface CoreTransactionRecordRepository extends JpaRepository<CoreTransactionRecord, Integer> {

    @Query("SELECT MAX(recordDate) FROM CoreTransactionRecord WHERE code = ?1 ")
    Date maxRecordDateByStatus(String code);
}
