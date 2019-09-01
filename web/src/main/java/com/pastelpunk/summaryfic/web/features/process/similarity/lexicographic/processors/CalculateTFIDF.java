package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors;

import com.pastelpunk.summaryfic.core.features.preprocess.book.ProcessedBookRepository;
import com.pastelpunk.summaryfic.core.features.preprocess.book.ProcessedBookRowMapper;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.processed.similarity.JobCorpus;
import com.pastelpunk.summaryfic.core.models.processed.NGram;
import com.pastelpunk.summaryfic.core.models.processed.similarity.lexicographical.ProcessedBookVector;
import com.pastelpunk.summaryfic.core.models.raw.AO3TagKey;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CalculateTFIDF implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateTFIDF.class);

    private final ProcessedBookRepository processedBookRepository;
    private final ProcessedBookRowMapper rowMapper = new ProcessedBookRowMapper();


    public CalculateTFIDF(ProcessedBookRepository processedBookRepository){
        this.processedBookRepository = processedBookRepository;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        RestExchange<JobCorpus, ProcessedBookVector> restExchange = new RestExchange<>(exchange);

        var intakeJobId = restExchange.get(IntakeConstants.JOB_ID).toString();
        var jobCorpus = restExchange.getInputObject();
        var language = restExchange.get(IntakeConstants.LANGUAGE).toString();

        var intakeJob = new IntakeJob();
        intakeJob.setId(intakeJobId);

        Map<String, NGram> corpusMap = jobCorpus.getUnigrams().stream()
                .collect(Collectors.toMap(NGram::getStringValue, ngram -> ngram));

        List<ProcessedBookVector> collect = processedBookRepository.getProcessedBookStream(intakeJob)
                .parallel()
                .map(rowMapper::map)
                .filter(book -> book.getTags().stream()
                        .anyMatch(tag ->
                                AO3TagKey.LANGUAGE.name().equalsIgnoreCase(tag.getTagKey()) &&
                                        language.equalsIgnoreCase(tag.getTagValue())))
                .map(book -> {

                            double[] values = new double[corpusMap.keySet().size()];

                            Map<String, NGram> bookMap = book.getUnigrams().stream()
                                    .collect(Collectors.toMap(NGram::getStringValue, ngram -> ngram));

                            int count = book.getUnigrams().stream()
                                    .map(NGram::getCount).reduce(0, Integer::sum);

                            int i = 0;
                            for (String key : corpusMap.keySet()) {
                                NGram corpusNgram = corpusMap.get(key);
                                NGram bookNgram = bookMap.getOrDefault(key, new NGram());

                                double weight = (((double) bookNgram.getCount() / count)) *
                                        Math.log(((double) jobCorpus.getDocumentCount() / corpusNgram.getDocumentCount()));

                                values[i++] = Double.isNaN(weight) ? 0 : weight;
                            }

                            int[] indices = new int[corpusMap.keySet().size()];
                            for (int j = 0; j < corpusMap.keySet().size(); j++) {
                                indices[j] = j;
                            }
                            Vector vector = new SparseVector(values.length, indices, values);
                            return new ProcessedBookVector(book, vector);
                        }
                ).collect(Collectors.toList());

        restExchange.setOutputList(collect);
        restExchange.syncHeaders();
    }
}
