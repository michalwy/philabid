package com.philabid.ui.control;

import com.philabid.database.util.FilterCondition;
import com.philabid.database.util.MultiTextFilterCondition;

import java.util.ArrayList;
import java.util.List;

public class CrudTableViewMultiFilter extends CrudTableViewTextFilter {

    private final List<String> searchableFields = new ArrayList<>();

    public CrudTableViewMultiFilter(List<String> fields) {
        super("Search:");
        setSearchableFields(fields);
    }

    /**
     * Sets the database column names that this filter should search in.
     *
     * @param fields A list of column names (e.g., "name", "description").
     */
    public void setSearchableFields(List<String> fields) {
        this.searchableFields.clear();
        this.searchableFields.addAll(fields);
    }

    @Override
    public FilterCondition getFilterCondition() {
        String searchText = getText();
        if (searchText == null || searchText.isBlank()) {
            return null;
        }
        return new MultiTextFilterCondition(searchableFields, searchText);
    }
}
