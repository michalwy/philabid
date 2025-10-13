package com.philabid.database.util;

import java.util.Collection;

public interface FilterCondition {
    String getSqlText();

    Collection<Object> getSqlParams();
}
