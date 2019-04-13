package com.pastelpunk.summaryfic.core.features.intake.book;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.gson.Gson;
import com.pastelpunk.summaryfic.core.models.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

//@Repository
public class BookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookRepository.class);

    private final Session session;
    private final Mapper<Book> mapper;
    private final Gson gson = new Gson();

    private final PreparedStatement create;


    public BookRepository(Session session,
                          MappingManager mappingManager) {
        this.session = session;
        this.mapper = mappingManager.mapper(Book.class);
        this.create = session.prepare(CREATE);
    }

    private static final String CREATE = "INSERT INTO book json ?";

    public void createBooks(Collection<Book> books) {
        BatchStatement batch = new BatchStatement();
        books.forEach(book -> batch.add(create.bind(gson.toJson(book))));
        session.execute(batch);
    }

}
