package com.pastelpunk.summaryfic.web.features.intake.processors.download;

import com.pastelpunk.summaryfic.core.features.intake.book.BookRepository;
import com.pastelpunk.summaryfic.core.models.Book;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PersistBook extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistBook.class);
    private BookRepository bookRepository;

    public PersistBook(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to persist book {}", e.getMessage(), e);
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<Book, Void> restExchange = new RestExchange<>(exchange);
        Book input = restExchange.getInputObject();
        bookRepository.createBooks(Collections.singletonList(input));
    }
}
