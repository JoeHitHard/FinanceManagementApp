package com.aad.project.finance.access;

import com.aad.project.finance.tables.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryAccess extends JpaRepository<Category, String> {
}
