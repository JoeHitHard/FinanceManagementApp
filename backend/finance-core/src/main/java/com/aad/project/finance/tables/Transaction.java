package com.aad.project.finance.tables;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.StringJoiner;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String type; // income or expense

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private Double amount;

    private long date;

    public Transaction() {
    }

    public Transaction(User user, String type, Category category, Double amount, long date) {
        this.user = user;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    // Getters and Setters


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Transaction.class.getSimpleName() + "[", "]")
                .add("transactionId='" + transactionId + "'")
                .add("type='" + type + "'")
                .add("amount=" + amount)
                .add("date=" + date)
                .toString();
    }
}
