package com.philabid.service;

import com.philabid.database.ConditionRepository;
import com.philabid.model.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Conditions.
 */
public class ConditionService {

    private static final Logger logger = LoggerFactory.getLogger(ConditionService.class);
    private final ConditionRepository conditionRepository;

    public ConditionService(ConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }

    public List<Condition> getAllConditions() {
        try {
            return conditionRepository.findAll();
        } catch (SQLException e) {
            logger.error("Failed to retrieve all conditions", e);
            return Collections.emptyList();
        }
    }

    public Optional<Condition> saveCondition(Condition condition) {
        if (condition.getName() == null || condition.getName().trim().isEmpty() ||
            condition.getCode() == null || condition.getCode().trim().isEmpty()) {
            logger.warn("Attempted to save a condition with an empty name or code.");
            return Optional.empty();
        }

        try {
            return Optional.of(conditionRepository.save(condition));
        } catch (SQLException e) {
            logger.error("Failed to save condition: {}", condition.getName(), e);
            return Optional.empty();
        }
    }

    public boolean deleteCondition(long id) {
        try {
            return conditionRepository.deleteById(id);
        } catch (SQLException e) {
            logger.error("Failed to delete condition with ID: {}", id, e);
            return false;
        }
    }
}
