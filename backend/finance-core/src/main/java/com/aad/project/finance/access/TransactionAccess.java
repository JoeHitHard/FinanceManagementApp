package com.aad.project.finance.access;

import com.aad.project.finance.tables.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionAccess extends JpaRepository<Transaction, String> {

    // Find transactions by userId
    List<Transaction> findByUser_UserId(String userId);
}
