package com.pastelpunk.summaryfic.core.models.raw;

import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;

import java.util.Date;
import java.util.List;

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
