package com.pastelpunk.summaryfic.core.models;

import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Table(name = "book")
public class Book extends Entity{

    private String intakeJobId;
    private String title;
    private String author;
    private Date updated;
    private Date published;
    private List<Tag> tags;
    private List<Chapter> chapters;
}
