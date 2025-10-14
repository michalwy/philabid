package com.philabid.service;

import com.philabid.database.CategoryRepository;
import com.philabid.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service layer for managing Categories.
 */
public class CategoryService extends AbstractCrudService<Category> {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository categoryRepository) {
        super(categoryRepository);
    }

    @Override
    protected boolean validate(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty() ||
                category.getCode() == null || category.getCode().trim().isEmpty()) {
            logger.warn("Attempted to save a category with an empty name or code.");
            return false;
        }
        return true;
    }
}
