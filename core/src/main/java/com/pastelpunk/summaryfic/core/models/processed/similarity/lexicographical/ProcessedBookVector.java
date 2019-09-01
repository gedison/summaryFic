package com.pastelpunk.summaryfic.core.models.processed.similarity.lexicographical;

import com.pastelpunk.summaryfic.core.models.processed.similarity.ProcessedBook;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.spark.mllib.linalg.Vector;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedBookVector {
    private ProcessedBook processedBook;
    private Vector vector;
}
