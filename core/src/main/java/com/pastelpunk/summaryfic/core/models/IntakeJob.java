package com.pastelpunk.summaryfic.core.models;

import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;

@Data
@Table(name = "intakeJob")
public class IntakeJob extends Entity{

    private String tag;
    private String source;
    private String status;
    private String statusMessage;

}
