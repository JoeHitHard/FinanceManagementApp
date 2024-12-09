package com.aad.project.finance.access;

import com.aad.project.finance.tables.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccess extends JpaRepository<User, String> {
    User findByEmailAndPassword(String userEmail, String password);
}
