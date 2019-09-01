package com.pastelpunk.summaryfic.web.features.preprocess.processors.preprocess;

import com.pastelpunk.summaryfic.core.features.intake.task.IntakeJobTaskRepository;
import com.pastelpunk.summaryfic.core.features.preprocess.book.ProcessedBookRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.core.models.intake.IntakeStatus;
import com.pastelpunk.summaryfic.core.models.processed.similarity.ProcessedBook;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PersistProcessedBook extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistProcessedBook.class);

    private final ProcessedBookRepository processedBookRepository;
    private final IntakeJobTaskRepository intakeJobTaskRepository;

    public PersistProcessedBook(ProcessedBookRepository processedBookRepository,
                                IntakeJobTaskRepository intakeJobTaskRepository){
        this.processedBookRepository = processedBookRepository;
        this.intakeJobTaskRepository = intakeJobTaskRepository;
    }

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to persist pre-processed data {}", e.getMessage(), e);

        RestExchange<Void, Void> restExchange = new RestExchange<>(exchange);
        var intakeJob = (IntakeJobTask) restExchange.get(IntakeConstants.JOB_STATUS);
        intakeJob.setStatus(IntakeStatus.PREPROCESS_FAILED.name());
        intakeJob.setStatusMessage(e.getMessage());
        intakeJobTaskRepository.updateIntakeJobTask(intakeJob);
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<ProcessedBook, Void> restExchange = new RestExchange<>(exchange);
        ProcessedBook input = restExchange.getInputObject();
        processedBookRepository.createBooks(Collections.singletonList(input));

        var intakeJob = (IntakeJobTask) restExchange.get(IntakeConstants.JOB_STATUS);
        intakeJob.setStatus(IntakeStatus.PREPROCESS_COMPLETE.name());
        intakeJobTaskRepository.updateIntakeJobTask(intakeJob);
    }
}
