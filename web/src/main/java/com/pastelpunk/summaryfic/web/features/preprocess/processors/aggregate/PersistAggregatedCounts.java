package com.pastelpunk.summaryfic.web.features.preprocess.processors.aggregate;

import com.pastelpunk.summaryfic.core.features.preprocess.corpus.CorpusRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.processed.JobCorpus;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistAggregatedCounts extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistAggregatedCounts.class);

    private final CorpusRepository corpusRepository;

    public PersistAggregatedCounts(CorpusRepository corpusRepository){
        this.corpusRepository = corpusRepository;
    }

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to persist aggregated counts {}",e.getMessage(), e);
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        var restExchange = new RestExchange<JobCorpus, Void>(exchange);
        var intakeJob = (IntakeJob) restExchange.get(IntakeConstants.JOB);
        List<JobCorpus> corpusList = restExchange.getInputList();
        corpusRepository.createJobCorpus(intakeJob, corpusList);
    }
}
