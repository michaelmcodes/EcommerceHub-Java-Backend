package com.michaelmark.ecommerce.service;

import com.michaelmark.ecommerce.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImplementation implements CategoryService {
    private List<Category> categories = new ArrayList<>();
    private Long categoryId = 0L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(++categoryId);
        categories.add(category);
    }
    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream()
                .filter(c -> c.getCategoryId() == categoryId)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        categories.remove(category);

        return "Category with id: " + categoryId + "deleted successfully";
    }

    @Override
    public String updateCategory(Category category, long categoryId) {
        Category category1 = categories.stream().filter(c -> c.getCategoryId() == categoryId)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        category1.setCategoryName(category.getCategoryName());
        return "Category with id: " + categoryId + " updated successfully";
    }
}
