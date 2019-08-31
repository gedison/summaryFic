package com.pastelpunk.summaryfic.core.models.intake;

import com.datastax.driver.mapping.annotations.Table;
import com.pastelpunk.summaryfic.core.models.raw.Entity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Table(name = "intakeJob")
public class IntakeJob extends Entity {

    private String tag;
    private String source;
    private String status;
    private String statusMessage;

}
