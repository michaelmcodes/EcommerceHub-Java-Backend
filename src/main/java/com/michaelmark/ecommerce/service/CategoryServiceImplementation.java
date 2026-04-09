package com.michaelmark.ecommerce.service;

import com.michaelmark.ecommerce.exceptions.APIException;
import com.michaelmark.ecommerce.exceptions.ResourceNotFoundException;
import com.michaelmark.ecommerce.model.Category;
import com.michaelmark.ecommerce.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImplementation implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        if (categoryRepository.findAll().isEmpty()) {
            throw new APIException("No categories found!");
        }
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) {
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists!");
        }
        categoryRepository.save(category);
    }
    @Override
    public String deleteCategory(Long categoryId) {
        Category foundCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepository.delete(foundCategory);
        return "Category with id: " + categoryId + "deleted successfully";
    }

    @Override
    public String updateCategory(Category category, Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        category.setCategoryName(category.getCategoryName());
        Category savedCategory = categoryRepository.save(category);
        return "Category with id: " + savedCategory.getCategoryId() + " updated successfully";
    }
}
