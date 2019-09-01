package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors;

import com.pastelpunk.summaryfic.core.features.processed.simliarity.lexicographical.ReducedDimensionalityBookRepository;
import com.pastelpunk.summaryfic.core.models.processed.similarity.ReducedDimensionalityBook;
import com.pastelpunk.summaryfic.core.models.processed.similarity.lexicographical.ProcessedBookVector;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersistReducedDimensionalityBooks implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistReducedDimensionalityBooks.class);

    private final ReducedDimensionalityBookRepository reducedDimensionalityBookRepository;

    public PersistReducedDimensionalityBooks(ReducedDimensionalityBookRepository reducedDimensionalityBookRepository){
        this.reducedDimensionalityBookRepository = reducedDimensionalityBookRepository;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        RestExchange<ProcessedBookVector, Void> restExchange = new RestExchange<>(exchange);
        List<ProcessedBookVector> processedBookVectors = restExchange.getInputList();

        List<ReducedDimensionalityBook> collect = processedBookVectors.stream().map(processedBookVector -> {
            ReducedDimensionalityBook output = new ReducedDimensionalityBook();
            output.setAuthor(processedBookVector.getProcessedBook().getAuthor());
            output.setIntakeJobId(processedBookVector.getProcessedBook().getIntakeJobId());
            output.setPublished(processedBookVector.getProcessedBook().getPublished());
            output.setTitle(processedBookVector.getProcessedBook().getTitle());
            output.setSource(processedBookVector.getProcessedBook().getSource());
            output.setUpdated(processedBookVector.getProcessedBook().getUpdated());
            output.setUri(processedBookVector.getProcessedBook().getUri());

            List<Double> values = Arrays.asList(ArrayUtils.toObject(processedBookVector.getVector().toArray()));
            output.setValues(values);

            return output;

        }).collect(Collectors.toList());

        reducedDimensionalityBookRepository.createBooks(collect);
    }
}
