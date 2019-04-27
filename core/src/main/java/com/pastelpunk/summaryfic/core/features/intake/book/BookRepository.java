package com.pastelpunk.summaryfic.core.features.intake.book;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.pastelpunk.summaryfic.core.models.raw.Book;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class BookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookRepository.class);

    private final Session session;
    private final Mapper<Book> mapper;

    private final PreparedStatement create;

    public BookRepository(Session session,
                          MappingManager mappingManager) {
        this.session = session;
        this.mapper = mappingManager.mapper(Book.class);
        this.create = session.prepare(CREATE);
    }

    private static final String CREATE = "INSERT INTO book " +
            "(id, created, modified, deleted, intakeJobId, source, uri, " +
            "title, author, updated, published, tags, chapters) " +
            "VALUES " +
            "(?, toTimestamp(now()), toTimestamp(now()), false, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public void createBooks(Collection<Book> books) {
        BatchStatement batch = new BatchStatement();
        for(Book book : books) {
            batch.add(create.bind(UuidUtil.getTimeBasedUuid().toString(), book.getIntakeJobId(),
                book.getSource(), book.getUri(), book.getTitle(),
                book.getAuthor(),book.getUpdated(), book.getPublished(),
                book.getTags(),book.getChapters()
            ));
        }
        session.execute(batch);
    }

}
