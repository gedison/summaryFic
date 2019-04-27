package com.pastelpunk.summaryfic.core.models.processed;

import com.datastax.driver.mapping.annotations.UDT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@UDT(keyspace = "summaryfic", name = "ngram")
public class NGram {
    String stringValue;
    int count;
    int n;
}
