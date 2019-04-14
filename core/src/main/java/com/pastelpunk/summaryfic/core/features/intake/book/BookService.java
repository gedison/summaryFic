package com.pastelpunk.summaryfic.core.features.intake.book;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.gson.Gson;
import com.pastelpunk.summaryfic.core.models.Book;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;

@Service
public class BookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookRepository.class);

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void createBooks(Collection<Book> books) {
        LOGGER.debug("Entering creating books ~ creating {} books", books.size());

        books.forEach(book -> {
            book.setId(UuidUtil.getTimeBasedUuid().toString());
            book.setDeleted(false);
        });

        bookRepository.createBooks(books);
    }
}
