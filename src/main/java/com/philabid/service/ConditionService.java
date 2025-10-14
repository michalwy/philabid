package com.philabid.service;

import com.philabid.database.ConditionRepository;
import com.philabid.model.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service layer for managing Conditions.
 */
public class ConditionService extends AbstractCrudService<Condition> {

    private static final Logger logger = LoggerFactory.getLogger(ConditionService.class);

    public ConditionService(ConditionRepository conditionRepository) {
        super(conditionRepository);
    }

    @Override
    protected boolean validate(Condition condition) {
        if (condition.getName() == null || condition.getName().trim().isEmpty() ||
                condition.getCode() == null || condition.getCode().trim().isEmpty()) {
            logger.warn("Attempted to save a condition with an empty name or code.");
            return false;
        }
        return true;
    }
}
