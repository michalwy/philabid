package com.philabid.ui.control;

import java.util.Collection;

public interface FilterCondition {
    String getSqlText();

    Collection<Object> getSqlParams();
}
