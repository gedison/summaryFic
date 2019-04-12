package com.pastelpunk.summaryfic.core.models;

import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;

@Data
@Table(name = "chapter")
public class Chapter extends Entity {

    private String intakeJobId;
    private String bookId;
    private String bookTitle;
    private String bookAuthor;

    private int order;
    private String chapterTitle;
    private String summary;
    private String notes;
    private String contents;
}
