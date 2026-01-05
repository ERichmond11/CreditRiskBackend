package com.emmanuel.creditrisk.backend.repository;

import com.emmanuel.creditrisk.backend.entity.CreditApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditApplicationRepository
        extends JpaRepository<CreditApplication, Long> {

    List<CreditApplication> findByUserEmailOrderByCreatedAtDesc(String userEmail);
}


