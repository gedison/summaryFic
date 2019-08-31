package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors;

import com.pastelpunk.summaryfic.core.models.processed.ProcessedBook;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.spark.mllib.linalg.Vector;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dimensionalityreduction.PCA;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReduceDimensionality2 implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReduceDimensionality2.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        RestExchange<Map<ProcessedBook, double[]>, Void> restExchange = new RestExchange<>(exchange);

        try {
            Map<ProcessedBook, double[]> input = restExchange.getInputObject();

           // List<INDArray> values = input.values().stream().map(Nd4j::create).collect(Collectors.toList());

            List<INDArray> values = new ArrayList<>();
            for(double[] vector : input.values()){
                values.add(Nd4j.create(vector, new int[]{1, vector.length}));
            }

            INDArray matrix = Nd4j.create(values, new int[]{values.size(), values.get(0).columns()});

            INDArray factors = PCA.pca_factor(matrix, 2, false);
            LOGGER.info("{}", factors);
        }catch (Exception e){
            LOGGER.error("{}", e.getMessage(), e);
        }

    }
}
