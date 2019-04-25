package com.pastelpunk.summaryfic.core.models.intake;

import com.datastax.driver.mapping.annotations.Table;
import com.pastelpunk.summaryfic.core.models.Entity;
import lombok.Data;

@Data
@Table(name="intakeJobTask")
public class IntakeJobTask extends Entity {

    private String intakeJobId;
    private String source;
    private String uri;
    private String status;
    private String statusMessage;

}
