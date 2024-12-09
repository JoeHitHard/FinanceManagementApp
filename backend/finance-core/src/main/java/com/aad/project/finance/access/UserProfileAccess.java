package com.aad.project.finance.access;

import com.aad.project.finance.tables.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileAccess extends JpaRepository<UserProfile, String> {
}
