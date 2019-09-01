package com.pastelpunk.summaryfic.core.features.preprocess.corpus;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.processed.similarity.JobCorpus;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CorpusRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpusRepository.class);

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
            "(id, created, modified, deleted, intakeJobId, language, documentCount, unigrams) " +
            "VALUES " +
            "(?, toTimestamp(now()), toTimestamp(now()), false, ?, ?, ?, ?)";

    public void createJobCorpus(IntakeJob intakeJob, List<JobCorpus> jobCorpusList) {
        BatchStatement batch = new BatchStatement();
        for (JobCorpus jobCorpus : jobCorpusList) {
            batch.add(create.bind(UuidUtil.getTimeBasedUuid().toString(), intakeJob.getId(),
                    jobCorpus.getLanguage(), jobCorpus.getDocumentCount(), jobCorpus.getUnigrams()));
        }
        session.execute(batch);
    }

    private static final String SELECT = "SELECT id, created, modified, deleted, " +
            "intakeJobId, language, documentCount, unigrams FROM " +
            "jobCorpus WHERE intakeJobId = ? AND language = ?";

    public JobCorpus getJobCorpus(String intakeJobId, String language){
        ResultSet resultSet = session.execute(SELECT, intakeJobId, language);
        return mapper.map(resultSet).one();
    }

    private static final String SELECT_ALL = "SELECT id, created, modified, deleted, " +
            "intakeJobId, language, documentCount, unigrams FROM " +
            "jobCorpus WHERE intakeJobId = ?";

    public List<JobCorpus> getJobCorpus(String intakeJobId){
        ResultSet resultSet = session.execute(SELECT_ALL, intakeJobId);
        return mapper.map(resultSet).all();
    }
}
