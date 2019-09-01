package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors;

import com.pastelpunk.summaryfic.core.models.processed.similarity.lexicographical.ProcessedBookVector;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReduceDimensionality implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReduceDimensionality.class);

    private final SparkConf sparkConf;

    public ReduceDimensionality(SparkConf sparkConf){
        this.sparkConf = sparkConf;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        RestExchange<ProcessedBookVector, ProcessedBookVector> restExchange = new RestExchange<>(exchange);

        List<Vector> vectors = restExchange.getInputList().stream()
                .map(ProcessedBookVector::getVector)
                .collect(Collectors.toList());

        List<Vector> reducedDimensionalityVectors = new ArrayList<>();
        try (JavaSparkContext sc = new JavaSparkContext(sparkConf)) {
            JavaRDD<Vector> distData = sc.parallelize(vectors);
            RowMatrix mat = new RowMatrix(distData.rdd());
            SingularValueDecomposition<RowMatrix, Matrix> svd = mat.computeSVD(2, true, 1.0E-9d);
            reducedDimensionalityVectors = svd.U().rows().toJavaRDD().collect();
        }catch (Exception e){
            LOGGER.error("Failed to reduce {}", e.getMessage(), e);
        }

        List<ProcessedBookVector> output = restExchange.getInputList();
        for(int i=0; i<output.size(); i++){
            output.get(i).setVector(reducedDimensionalityVectors.get(i));
        }

        restExchange.setOutputList(output);
    }
}
