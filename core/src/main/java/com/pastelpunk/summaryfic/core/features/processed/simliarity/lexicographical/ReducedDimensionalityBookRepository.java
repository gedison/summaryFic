package com.pastelpunk.summaryfic.core.features.processed.simliarity.lexicographical;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.processed.similarity.JobCorpus;
import com.pastelpunk.summaryfic.core.models.processed.similarity.ProcessedBook;
import com.pastelpunk.summaryfic.core.models.processed.similarity.ReducedDimensionalityBook;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
public class ReducedDimensionalityBookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReducedDimensionalityBookRepository.class);

    private final Session session;
    private final Mapper<ReducedDimensionalityBook> mapper;


    private final PreparedStatement create;

    public ReducedDimensionalityBookRepository(Session session,
                                               MappingManager mappingManager) {
        this.session = session;
        this.create = session.prepare(CREATE);
        this.mapper = mappingManager.mapper(ReducedDimensionalityBook.class);

    }

    private static final String CREATE = "INSERT INTO reducedDimensionalityBook " +
            "(id, created, modified, deleted, intakeJobId, source, uri, " +
            "title, author, updated, published, values) " +
            "VALUES " +
            "(?, toTimestamp(now()), toTimestamp(now()), false, ?, ?, ?, ?, ?, ?, ?, ?)";

    public void createBooks(Collection<ReducedDimensionalityBook> books) {
        BatchStatement batch = new BatchStatement();
        for (ReducedDimensionalityBook book : books) {
            batch.add(create.bind(UuidUtil.getTimeBasedUuid().toString(), book.getIntakeJobId(),
                    book.getSource(), book.getUri(), book.getTitle(), book.getAuthor(),
                    book.getUpdated(), book.getPublished(), book.getValues()
            ));
        }
        session.execute(batch);
    }

    private static final String SELECT_ALL = "SELECT id, created, modified, deleted, " +
            "intakeJobId, source, uri, title, author, updated, published,  values " +
            "FROM reducedDimensionalityBook WHERE intakeJobId = ?";

    public List<ReducedDimensionalityBook> getReducedDimensionalityBook(String intakeJobId){
        ResultSet resultSet = session.execute(SELECT_ALL, intakeJobId);
        return mapper.map(resultSet).all();
    }
}