package com.michaelmark.ecommerce.service;

import com.michaelmark.ecommerce.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);

    String updateCategory(Category category, Long categoryId);
}
