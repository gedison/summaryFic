package com.pastelpunk.summaryfic.core.models.raw;

import com.datastax.driver.mapping.annotations.UDT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@UDT(keyspace = "summaryfic", name = "tag")
public class Tag {

    private String tagKey;
    private String tagValue;
}
