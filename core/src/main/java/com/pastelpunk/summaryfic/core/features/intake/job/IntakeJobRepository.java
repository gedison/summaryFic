package com.pastelpunk.summaryfic.core.features.intake.job;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.pastelpunk.summaryfic.core.features.intake.book.BookRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IntakeJobRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntakeJobRepository.class);

    private final Session session;
    private final Mapper<IntakeJob> mapper;

    private final PreparedStatement create;


    public IntakeJobRepository(Session session,
                               MappingManager mappingManager) {
        this.session = session;
        this.mapper = mappingManager.mapper(IntakeJob.class);
        this.create = session.prepare(CREATE);
    }

    private static final String CREATE = "INSERT INTO intakeJob " +
            "(id, created, modified, deleted, tag, source, status, statusMessage) " +
            "VALUES (?,toTimestamp(now()),toTimestamp(now()),false,?,?,?,?)";

    public IntakeJob createIntakeJob(IntakeJob intakeJob) {
        BatchStatement batch = new BatchStatement();
        String id = UuidUtil.getTimeBasedUuid().toString();
        batch.add(create.bind(id, intakeJob.getTag(), intakeJob.getSource(),
                intakeJob.getStatus(), intakeJob.getStatusMessage()));
        session.execute(batch);
        return getIntakeJob(id);
    }

    private static final String GET = "SELECT id, created, modified, tag, source, status, statusMessage " +
            "FROM intakeJob WHERE id = ?";

    public IntakeJob getIntakeJob(String id){
        ResultSet resultSet = session.execute(GET, id);
        return mapper.map(resultSet).one();
    }

    private static final String GET_ALL = "SELECT id, created, modified, tag, source, status, statusMessage " +
            "FROM intakeJob";

    public List<IntakeJob> getIntakeJobs(){
        ResultSet resultSet = session.execute(GET_ALL);
        return mapper.map(resultSet).all();
    }

    private static final String UPDATE = "UPDATE intakeJob " +
            "SET modified = toTimestamp(now()), status = ?, statusMessage = ? " +
            "WHERE id = ? AND tag = ? AND source = ?";

    public IntakeJob updateIntakeJob(IntakeJob toUpdate){
        session.execute(UPDATE, toUpdate.getStatus(), toUpdate.getStatusMessage(),
                toUpdate.getId(), toUpdate.getTag(), toUpdate.getSource());
        return toUpdate;
    }
}