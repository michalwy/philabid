package com.philabid.database.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiTextFilterCondition implements FilterCondition {
    private final Collection<String> fields;
    private final String text;

    public MultiTextFilterCondition(Collection<String> fields, String text) {
        this.fields = fields;
        this.text = text;
    }

    @Override
    public String getSqlText() {
        if (text == null || text.isBlank() || fields.isEmpty()) {
            return "";
        }

        // For each word, create a condition like "(field1 LIKE ? OR field2 LIKE ?)"
        String singleWordCondition = fields.stream()
                .map(field -> field + " LIKE ?")
                .collect(Collectors.joining(" OR ", "(", ")"));

        // Split the search text into words and repeat the condition for each word, joined by AND
        return Stream.of(text.trim().split("\\s+"))
                .map(word -> singleWordCondition)
                .collect(Collectors.joining(" AND "));
    }

    @Override
    public Collection<Object> getSqlParams() {
        if (text == null || text.isBlank() || fields.isEmpty()) {
            return List.of();
        }

        String[] words = text.trim().split("\\s+");
        List<Object> params = new ArrayList<>();

        // For each word, add a parameter for each field
        for (String word : words) {
            String searchTerm = "%" + word + "%";
            for (int i = 0; i < fields.size(); i++) {
                params.add(searchTerm);
            }
        }
        return params;
    }
}
