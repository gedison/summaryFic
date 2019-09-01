package com.pastelpunk.summaryfic.core.models.processed.similarity;

import com.datastax.driver.mapping.annotations.Table;
import com.pastelpunk.summaryfic.core.models.processed.NGram;
import com.pastelpunk.summaryfic.core.models.raw.Entity;
import com.pastelpunk.summaryfic.core.models.raw.Tag;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Table(name = "processedBook")
public class ProcessedBook extends Entity {

    private String intakeJobId;
    private String source;
    private String uri;
    private String title;
    private String author;
    private Date updated;
    private Date published;
    private List<Tag> tags;
    private List<NGram> unigrams;
}
