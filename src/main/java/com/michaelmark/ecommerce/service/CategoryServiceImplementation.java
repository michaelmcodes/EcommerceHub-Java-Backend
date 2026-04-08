package com.michaelmark.ecommerce.service;

import com.michaelmark.ecommerce.model.Category;
import com.michaelmark.ecommerce.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImplementation implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }
    @Override
    public String deleteCategory(Long categoryId) {
        Category foundCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        categoryRepository.delete(foundCategory);

        return "Category with id: " + categoryId + "deleted successfully";
    }

    @Override
    public String updateCategory(Category category, long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        category.setCategoryName(category.getCategoryName());
        Category savedCategory = categoryRepository.save(category);
        return "Category with id: " + savedCategory.getCategoryId() + " updated successfully";
    }
}
