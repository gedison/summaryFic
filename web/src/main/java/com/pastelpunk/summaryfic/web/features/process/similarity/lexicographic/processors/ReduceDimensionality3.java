package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors;

import com.pastelpunk.summaryfic.core.models.processed.ProcessedBook;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dimensionalityreduction.PCA;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ReduceDimensionality3 implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReduceDimensionality3.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        RestExchange<Map<ProcessedBook, double[]>, Void> restExchange = new RestExchange<>(exchange);

        Map<ProcessedBook, double[]> input = restExchange.getInputObject();



    }
}
