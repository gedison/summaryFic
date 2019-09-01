package com.pastelpunk.summaryfic.web.features.preprocess.processors.aggregate;

import com.pastelpunk.summaryfic.core.features.preprocess.book.ProcessedBookRepository;
import com.pastelpunk.summaryfic.core.features.preprocess.book.ProcessedBookRowMapper;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.core.models.processed.similarity.JobCorpus;
import com.pastelpunk.summaryfic.core.models.processed.NGram;
import com.pastelpunk.summaryfic.core.models.raw.AO3TagKey;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class AggregateCounts extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateCounts.class);

    private final ProcessedBookRepository processedBookRepository;
    private final ProcessedBookRowMapper rowMapper = new ProcessedBookRowMapper();

    public AggregateCounts(ProcessedBookRepository processedBookRepository){

        this.processedBookRepository = processedBookRepository;
    }

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed during aggregation {}", e.getMessage(), e);
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<IntakeJobTask, JobCorpus> restExchange = new RestExchange<>(exchange);
        var intakeJob = (IntakeJob) restExchange.get(IntakeConstants.JOB);
        var bookStream = processedBookRepository.getProcessedBookStream(intakeJob);

         Map<String, List<JobCorpus>> languageMap = bookStream
                .map(rowMapper::map)
                .map(book -> book.getTags().stream()
                        .filter(tag -> AO3TagKey.LANGUAGE.name().equalsIgnoreCase(tag.getTagKey()))
                        .findFirst().map(tag -> new JobCorpus(tag.getTagValue(), book.getUnigrams()))
                        .orElse(null)).filter(Objects::nonNull)
                .collect(Collectors.groupingBy(JobCorpus::getLanguage));

         Map<String, Integer> counts =
                 languageMap.entrySet().stream()
                 .collect(Collectors.toMap(Map.Entry::getKey, es -> es.getValue().size()));

        List<JobCorpus> collect = languageMap.values().stream().map(
                        entrySet -> entrySet.stream()
                                .reduce(this::aggregateJobCorpus)
                                .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        collect.forEach(jobCorpus->{
            int count = counts.getOrDefault(jobCorpus.getLanguage(),0);
            jobCorpus.setDocumentCount(count);
        });

        restExchange.setOutputList(collect);
        restExchange.syncHeaders();
    }

    private JobCorpus aggregateJobCorpus(JobCorpus a, JobCorpus b){
        Map<NGram, NGram> aMap = a.getUnigrams().stream().collect(Collectors.toMap(value -> value, value -> value));
        b.getUnigrams().forEach(unigram-> aMap.compute(unigram, (k, v) -> Objects.isNull(v) ? unigram : compute(unigram, v)));
        a.setUnigrams(new ArrayList<>(aMap.values()));
        return a;
    }

    private NGram compute(NGram a, NGram b){
        a.setCount(a.getCount() + b.getCount());
        a.setDocumentCount(a.getDocumentCount() + b.getDocumentCount());
        return a;
    }

}
