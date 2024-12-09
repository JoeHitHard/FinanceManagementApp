package com.aad.project.finance.controllers;

import com.aad.project.finance.access.UserProfileAccess;
import com.aad.project.finance.tables.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userProfiles")
@CrossOrigin(origins = "http://localhost:3000")
public class UserProfileController {

    @Autowired
    private UserProfileAccess userProfileAccess;

    @PostMapping
    public ResponseEntity<UserProfile> createUserProfile(@RequestBody UserProfile userProfile) {
        UserProfile savedProfile = userProfileAccess.save(userProfile);
        return ResponseEntity.ok(savedProfile);
    }

    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllUserProfiles() {
        List<UserProfile> profiles = userProfileAccess.findAll();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUserProfileById(@PathVariable String id) {
        return userProfileAccess.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable String id, @RequestBody UserProfile profileDetails) {
        return userProfileAccess.findById(id)
                .map(profile -> {
                    profile.setMonthlyIncome(profileDetails.getMonthlyIncome());
                    profile.setSavingsGoal(profileDetails.getSavingsGoal());
                    return ResponseEntity.ok(userProfileAccess.save(profile));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable String id) {
        if (userProfileAccess.existsById(id)) {
            userProfileAccess.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
