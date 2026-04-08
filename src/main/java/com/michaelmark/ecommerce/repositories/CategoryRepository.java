package com.michaelmark.ecommerce.repositories;

import com.michaelmark.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
