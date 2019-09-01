package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors;

import com.pastelpunk.summaryfic.core.features.preprocess.corpus.CorpusRepository;
import com.pastelpunk.summaryfic.core.models.processed.similarity.JobCorpus;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class GetJobCorpus implements Processor {

    private final CorpusRepository corpusRepository;

    public GetJobCorpus(CorpusRepository corpusRepository){
        this.corpusRepository = corpusRepository;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        RestExchange<Void, JobCorpus> restExchange = new RestExchange<>(exchange);

        var jobId = restExchange.get(IntakeConstants.JOB_ID).toString();
        var language = restExchange.get(IntakeConstants.LANGUAGE).toString();

        var jobCorpus = corpusRepository.getJobCorpus(jobId, language);

        restExchange.setOutputObject(jobCorpus);
        restExchange.syncHeaders();
    }
}
