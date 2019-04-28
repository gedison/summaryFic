package com.pastelpunk.summaryfic.core.features.preprocess.corpus;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.pastelpunk.summaryfic.core.features.preprocess.book.ProcessedBookRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.processed.JobCorpus;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CorpusRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessedBookRepository.class);

    private final Session session;
    private final Mapper<JobCorpus> mapper;

    private final PreparedStatement create;

    public CorpusRepository(Session session,
                                   MappingManager mappingManager) {
        this.session = session;
        this.mapper = mappingManager.mapper(JobCorpus.class);
        this.create = session.prepare(CREATE);
    }

    private static final String CREATE = "INSERT INTO jobCorpus " +
            "(id, created, modified, deleted, intakeJobId, language, unigrams) " +
            "VALUES " +
            "(?, toTimestamp(now()), toTimestamp(now()), false, ?, ?, ?)";

    public void createJobCorpus(IntakeJob intakeJob, List<JobCorpus> jobCorpusList) {
        BatchStatement batch = new BatchStatement();
        for (JobCorpus jobCorpus : jobCorpusList) {
            batch.add(create.bind(UuidUtil.getTimeBasedUuid().toString(), intakeJob.getId(),
                    jobCorpus.getLanguage(), jobCorpus.getUnigrams()));
        }
        session.execute(batch);
    }
}
