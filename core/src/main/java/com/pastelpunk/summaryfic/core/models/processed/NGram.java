package com.pastelpunk.summaryfic.core.models.processed;

import com.datastax.driver.mapping.annotations.UDT;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@UDT(keyspace = "summaryfic", name = "ngram")
public class NGram {
    @EqualsAndHashCode.Include
    String stringValue;
    @EqualsAndHashCode.Exclude
    int count = 0;
    @EqualsAndHashCode.Include
    int n;
    @EqualsAndHashCode.Exclude
    int documentCount = 1;
}
