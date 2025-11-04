package com.philabid.ui.control;

import com.philabid.database.util.FilterCondition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public abstract class CrudTableViewFilter {

    private final ObjectProperty<FilterCondition> filterCondition =
            new SimpleObjectProperty<FilterCondition>(null);

    public abstract Node getControl();

    public abstract void initialize();

    public ObjectProperty<FilterCondition> filterConditionProperty() {
        return filterCondition;
    }

    public abstract void clear();
}
