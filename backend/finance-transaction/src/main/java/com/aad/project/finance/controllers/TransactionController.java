package com.aad.project.finance.controllers;

import com.aad.project.finance.access.TransactionAccess;
import com.aad.project.finance.tables.Category;
import com.aad.project.finance.tables.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    @Autowired
    private TransactionAccess transactionAccess;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = transactionAccess.save(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionAccess.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String id) {
        return transactionAccess.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String id, @RequestBody Transaction transactionDetails) {
        return transactionAccess.findById(id)
                .map(transaction -> {
                    transaction.setType(transactionDetails.getType());
                    transaction.setCategory(transactionDetails.getCategory());
                    transaction.setAmount(transactionDetails.getAmount());
                    transaction.setDate(transactionDetails.getDate());
                    return ResponseEntity.ok(transactionAccess.save(transaction));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String id) {
        if (transactionAccess.existsById(id)) {
            transactionAccess.deleteById(id);
            return ResponseEntity.ok("Transaction deleted successfully");
        }
        return ResponseEntity.ok("Transaction not found");
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<Double> getTotalTransactionAmount(@PathVariable String userId) {
        List<Transaction> transactions = transactionAccess.findByUser_UserId(userId);
        if (transactions.isEmpty()) {
            return ResponseEntity.ok(0.0);
        }

        // Calculate total transaction amount: add incomes and subtract expenses
        double totalAmount = transactions.stream()
                .mapToDouble(transaction -> {
                    if ("income".equalsIgnoreCase(transaction.getType())) {
                        return transaction.getAmount(); // Add income
                    } else if ("expense".equalsIgnoreCase(transaction.getType())) {
                        return -transaction.getAmount(); // Subtract expense
                    }
                    return 0.0; // Default for invalid type
                })
                .sum();
        return ResponseEntity.ok(totalAmount);
    }


    @GetMapping("/user/{userId}")
    public List<Transaction> getUserTransactions(@PathVariable String userId) {
        return transactionAccess.findByUser_UserId(userId);
    }

    // API to get transaction amounts by category for a user
    @GetMapping("/category/{userId}")
    public ResponseEntity<List<TransactionAmountByCategory>> getTransactionAmountByCategory(@PathVariable String userId) {
        List<Transaction> transactions = transactionAccess.findByUser_UserId(userId);
        if (transactions.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        // Group by category and calculate net amount (income - expense) for each category
        List<TransactionAmountByCategory> transactionAmountsByCategory = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(transaction ->
                                "income".equalsIgnoreCase(transaction.getType())
                                        ? transaction.getAmount()
                                        : -transaction.getAmount()
                        )
                ))
                .entrySet().stream()
                .map(entry -> new TransactionAmountByCategory(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactionAmountsByCategory);
    }

    // Data transfer object for transaction amount by category
    public static class TransactionAmountByCategory {
        private Category category;
        private double totalAmount;

        public TransactionAmountByCategory(Category category, double totalAmount) {
            this.category = category;
            this.totalAmount = totalAmount;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
}
