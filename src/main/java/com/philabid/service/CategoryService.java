package com.philabid.service;

import com.philabid.database.CategoryRepository;
import com.philabid.model.Category;
import com.philabid.ui.control.FilterCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Categories.
 */
public class CategoryService implements CrudService<Category> {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAll(Collection<FilterCondition> filterConditions) {
        try {
            return categoryRepository.findAll();
        } catch (SQLException e) {
            logger.error("Failed to retrieve all categories", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Category create() {
        return new Category();
    }

    @Override
    public Optional<Category> save(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty() ||
                category.getCode() == null || category.getCode().trim().isEmpty()) {
            logger.warn("Attempted to save a category with an empty name or code.");
            return Optional.empty();
        }

        try {
            return Optional.of(categoryRepository.save(category));
        } catch (SQLException e) {
            logger.error("Failed to save category: {}", category.getName(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            return categoryRepository.deleteById(id);
        } catch (SQLException e) {
            logger.error("Failed to delete category with ID: {}", id, e);
            return false;
        }
    }

    public Optional<Category> getCategoryById(long id) {
        try {
            return categoryRepository.findById(id);
        } catch (SQLException e) {
            logger.error("Failed to get category with ID: {}", id, e);
            return Optional.empty();
        }
    }
}
