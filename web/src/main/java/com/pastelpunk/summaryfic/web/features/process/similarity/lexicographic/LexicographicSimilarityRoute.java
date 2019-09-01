package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic;

import com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors.CalculateTFIDF;
import com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors.GetJobCorpus;
import com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors.PersistReducedDimensionalityBooks;
import com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors.ReduceDimensionality;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class LexicographicSimilarityRoute extends RouteBuilder {

    private final GetJobCorpus getJobCorpus;
    private final CalculateTFIDF calculateTdIdf;
    private final ReduceDimensionality reduceDimensionality;
    private final PersistReducedDimensionalityBooks persistReducedDimensionalityBooks;

    public LexicographicSimilarityRoute(GetJobCorpus getJobCorpus,
                                        CalculateTFIDF calculateTfIdf,
                                        ReduceDimensionality reduceDimensionality,
                                        PersistReducedDimensionalityBooks persistReducedDimensionalityBooks){

        this.getJobCorpus = getJobCorpus;
        this.calculateTdIdf = calculateTfIdf;
        this.reduceDimensionality = reduceDimensionality;
        this.persistReducedDimensionalityBooks = persistReducedDimensionalityBooks;
    }

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("direct:globalExceptionHandling"));

        from("direct:lexicographicSimilarity")
                .log("Starting similarity")
                .process(getJobCorpus)
                .process(calculateTdIdf)
                .process(reduceDimensionality)
                .process(persistReducedDimensionalityBooks)
                .log("Finishing similarity");
    }
}
