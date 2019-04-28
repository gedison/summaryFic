package com.pastelpunk.summaryfic.core.models.processed;

import com.datastax.driver.mapping.annotations.Table;
import com.pastelpunk.summaryfic.core.models.raw.Entity;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "jobCorpus")
public class JobCorpus extends Entity {
    private String intakeJobId;
    @NonNull
    private String language;
    @NonNull
    private List<NGram> unigrams;
}
