package com.pastelpunk.summaryfic.core.models;

import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;

@Data
@Table(name = "chapter")
public class Chapter{

    private int ordering;
    private String description;
    private String content;
}
