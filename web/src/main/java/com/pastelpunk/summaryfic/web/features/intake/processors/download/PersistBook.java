package com.pastelpunk.summaryfic.web.features.intake.processors.download;

import com.pastelpunk.summaryfic.core.features.intake.book.BookRepository;
import com.pastelpunk.summaryfic.core.features.intake.task.IntakeJobTaskRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.core.models.intake.IntakeStatus;
import com.pastelpunk.summaryfic.core.models.raw.Book;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PersistBook extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistBook.class);

    private final BookRepository bookRepository;
    private final IntakeJobTaskRepository intakeJobTaskRepository;

    public PersistBook(BookRepository bookRepository,
                       IntakeJobTaskRepository intakeJobTaskRepository){
        this.bookRepository = bookRepository;
        this.intakeJobTaskRepository = intakeJobTaskRepository;
    }

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to persist book {}", e.getMessage(), e);

        RestExchange<Book, Void> restExchange = new RestExchange<>(exchange);
        var intakeJob = (IntakeJobTask) restExchange.get(IntakeConstants.JOB_STATUS);
        intakeJob.setStatus(IntakeStatus.ERROR.name());
        intakeJob.setStatusMessage(e.getMessage());
        intakeJobTaskRepository.updateIntakeJobTask(intakeJob);
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<Book, Void> restExchange = new RestExchange<>(exchange);
        Book input = restExchange.getInputObject();
        bookRepository.createBooks(Collections.singletonList(input));

        var intakeJob = (IntakeJobTask) restExchange.get(IntakeConstants.JOB_STATUS);
        intakeJob.setStatus(IntakeStatus.DOWNLOAD_COMPLETE.name());
        intakeJobTaskRepository.updateIntakeJobTask(intakeJob);
    }
}
