package com.pastelpunk.summaryfic.core.models.raw;

import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.UDT;
import lombok.Data;

@Data
@UDT(keyspace = "summaryfic", name = "chapter")
public class Chapter{

    private int ordering;
    private String description;
    private String content;
}
