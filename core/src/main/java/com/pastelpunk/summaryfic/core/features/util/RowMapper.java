package com.pastelpunk.summaryfic.core.features.util;

import com.datastax.driver.core.Row;

public interface RowMapper<T> {

    public T map(Row row);
}
