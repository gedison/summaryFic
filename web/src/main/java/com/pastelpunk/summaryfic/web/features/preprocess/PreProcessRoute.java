package com.pastelpunk.summaryfic.web.features.preprocess;

import com.pastelpunk.summaryfic.web.features.preprocess.processors.aggregate.AggregateCounts;
import com.pastelpunk.summaryfic.web.features.preprocess.processors.aggregate.PersistAggregatedCounts;
import com.pastelpunk.summaryfic.web.features.preprocess.processors.preprocess.PersistProcessedBook;
import com.pastelpunk.summaryfic.web.features.preprocess.processors.preprocess.UnigramProcessor;
import com.pastelpunk.summaryfic.web.features.preprocess.processors.preprocess.UpdateTaskStatus;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PreProcessRoute extends RouteBuilder {

    private final UpdateTaskStatus updateTaskStatus;
    private final UnigramProcessor unigramProcessor;
    private final PersistProcessedBook persistProcessedBook;
    private final AggregateCounts aggregateCounts;
    private final PersistAggregatedCounts persistAggregatedCounts;

    public PreProcessRoute(UpdateTaskStatus updateTaskStatus,
                           UnigramProcessor unigramProcessor,
                           PersistProcessedBook persistProcessedBook,
                           AggregateCounts aggregateCounts,
                           PersistAggregatedCounts persistAggregatedCounts){

        this.updateTaskStatus = updateTaskStatus;
        this.unigramProcessor = unigramProcessor;
        this.persistProcessedBook = persistProcessedBook;
        this.aggregateCounts = aggregateCounts;
        this.persistAggregatedCounts = persistAggregatedCounts;
    }

    @Override
    public void configure() throws Exception {

        errorHandler(deadLetterChannel("direct:globalExceptionHandling"));

        from("direct:preProcessBook")
                .log("Starting preprocess")
                .process(updateTaskStatus)
                .process(unigramProcessor)
                .process(persistProcessedBook)
                .log("Finishing PreProcessing");

        from("direct:aggregate")
                .process(aggregateCounts)
                .process(persistAggregatedCounts);
    }
}
