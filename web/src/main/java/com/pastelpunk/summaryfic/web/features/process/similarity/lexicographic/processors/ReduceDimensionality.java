package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors;

import com.pastelpunk.summaryfic.core.models.processed.ProcessedBook;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.feature.PCA;
import org.apache.spark.mllib.feature.PCAModel;
import org.apache.spark.rdd.RDD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ReduceDimensionality implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReduceDimensionality.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        RestExchange<Map<ProcessedBook, Vector>, Void> restExchange = new RestExchange<>(exchange);

        Map<ProcessedBook, Vector> input = restExchange.getInputObject();
        SparkConf conf = new SparkConf().setAppName("PCAExample").setMaster("local");

        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            //Create points as Spark Vectors
            List<Vector> vectors = new ArrayList<>(input.values());

            //Create Spark MLLib RDD
            JavaRDD<Vector> distData = sc.parallelize(vectors);
            RDD<Vector> vectorRDD = distData.rdd();

            //Execute PCA Projection to 2 dimensions
            PCA pca = new PCA(2);
            PCAModel pcaModel = pca.fit(vectorRDD);
            Matrix matrix = pcaModel.pc();
            matrix.rowIter().foreach(vector ->{
                LOGGER.info(vector.toJson());
                return vector;
            });
        }
    }
}
