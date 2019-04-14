package com.pastelpunk.summaryfic.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tag {

    private String tagKey;
    private Object tagValue;
}
