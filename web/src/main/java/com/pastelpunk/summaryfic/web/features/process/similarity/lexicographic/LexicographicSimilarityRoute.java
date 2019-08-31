package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic;

import com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors.CalculateTFIDF;
import com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors.GetJobCorpus;
import com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors.ReduceDimensionality;
import com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors.ReduceDimensionality2;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class LexicographicSimilarityRoute extends RouteBuilder {

    private final GetJobCorpus getJobCorpus;
    private final CalculateTFIDF calculateTdIdf;
    private final ReduceDimensionality2 reduceDimensionality;

    public LexicographicSimilarityRoute(GetJobCorpus getJobCorpus,
                                        CalculateTFIDF calculateTfIdf,
                                        ReduceDimensionality2 reduceDimensionality){

        this.getJobCorpus = getJobCorpus;
        this.calculateTdIdf = calculateTfIdf;
        this.reduceDimensionality = reduceDimensionality;
    }

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("direct:globalExceptionHandling"));

        from("direct:lexicographicSimilarity")
                .log("Starting similarity")
                .process(getJobCorpus)
                .process(calculateTdIdf)
                .process(reduceDimensionality)
                .log("Finishing similarity");
    }
}
