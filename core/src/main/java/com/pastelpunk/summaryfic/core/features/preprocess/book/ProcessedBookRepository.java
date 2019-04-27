package com.pastelpunk.summaryfic.core.features.preprocess.book;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.pastelpunk.summaryfic.core.features.intake.book.BookRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.processed.ProcessedBook;
import com.pastelpunk.summaryfic.core.models.raw.Book;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
public class ProcessedBookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessedBookRepository.class);

    private final Session session;
    private final Mapper<ProcessedBook> mapper;

    private final PreparedStatement create;

    public ProcessedBookRepository(Session session,
                                   MappingManager mappingManager) {
        this.session = session;
        this.mapper = mappingManager.mapper(ProcessedBook.class);
        this.create = session.prepare(CREATE);
    }

    private static final String CREATE = "INSERT INTO processedBook " +
            "(id, created, modified, deleted, intakeJobId, source, uri, " +
            "title, author, updated, published, tags, unigrams) " +
            "VALUES " +
            "(?, toTimestamp(now()), toTimestamp(now()), false, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public void createBooks(Collection<ProcessedBook> books) {
        BatchStatement batch = new BatchStatement();
        for (ProcessedBook book : books) {
            batch.add(create.bind(UuidUtil.getTimeBasedUuid().toString(), book.getIntakeJobId(),
                    book.getSource(), book.getUri(), book.getTitle(), book.getAuthor(),
                    book.getUpdated(), book.getPublished(), book.getTags(),
                    book.getUnigrams()
            ));
        }
        session.execute(batch);
    }

    private static final String SELECT_ALL = "SELECT id, created, modified, deleted, " +
            "intakeJobId, source, uri, title, author, updated, published, tags, unigrams " +
            "FROM processedBook WHERE intakeJobId = ?";

    public Stream<Row> getUnigramStream(IntakeJob intakeJob){
        ResultSet resultSet = session.execute(SELECT_ALL, intakeJob.getId());


        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(resultSet.iterator(), Spliterator.ORDERED),
                false);
    }
}
