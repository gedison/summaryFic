package com.pastelpunk.summaryfic.web.features.intake.processors.download;

import com.pastelpunk.summaryfic.core.features.intake.book.BookService;
import com.pastelpunk.summaryfic.core.models.Book;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PersistBook extends FilterProcessor {

    private BookService bookService;

    public PersistBook(BookService bookService){
        this.bookService = bookService;
    }

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {

    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<Book, Void> restExchange = new RestExchange<>(exchange);


        Book input = restExchange.getInputObject();
        bookService.createBooks(Collections.singletonList(input));

    }
}
