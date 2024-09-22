package com.backend.shoppingcart.service.category;

import com.backend.shoppingcart.exception.AlreadyExistsException;
import com.backend.shoppingcart.exception.ResourceNotFoundException;
import com.backend.shoppingcart.model.Category;
import com.backend.shoppingcart.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category)
                // If the category does exist, the filter fails, and the Optional becomes empty.
                .filter(c -> !categoryRepository.existsByName(c.getName()))
                // If the Optional is empty, this step is skipped.
                .map(categoryRepository::save)
                // If the Optional is empty (meaning the category already existed), throws an AlreadyExistsException.
                .orElseThrow(() -> new AlreadyExistsException(category.getName() + " already exists"));
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(getCategoryById(id))
                .map(existingCategory -> {
                    existingCategory.setName(category.getName());
                    return categoryRepository.save(existingCategory);
                })
                .orElseThrow(()-> new ResourceNotFoundException("Category not found!"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id).ifPresentOrElse(
                categoryRepository::delete,
                () -> {
                    throw new ResourceNotFoundException("Category not found");
                }
        );
    }
}
