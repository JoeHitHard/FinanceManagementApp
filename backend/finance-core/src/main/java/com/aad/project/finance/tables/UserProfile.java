package com.aad.project.finance.tables;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.StringJoiner;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    private String profileId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "monthly_income", nullable = false)
    private Double monthlyIncome;

    @Column(name = "savings_goal", nullable = false)
    private Double savingsGoal;

    public UserProfile() {
    }

    public UserProfile(User user, Double monthlyIncome, Double savingsGoal) {
        this.user = user;
        this.monthlyIncome = monthlyIncome;
        this.savingsGoal = savingsGoal;
    }

    // Getters and Setters


    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Double getSavingsGoal() {
        return savingsGoal;
    }

    public void setSavingsGoal(Double savingsGoal) {
        this.savingsGoal = savingsGoal;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserProfile.class.getSimpleName() + "[", "]")
                .add("profileId='" + profileId + "'")
                .add("monthlyIncome=" + monthlyIncome)
                .add("savingsGoal=" + savingsGoal)
                .toString();
    }
}
