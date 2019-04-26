package com.pastelpunk.summaryfic.core.features.intake.task;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.pastelpunk.summaryfic.core.features.intake.book.BookRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IntakeJobTaskRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntakeJobTaskRepository.class);

    private final Session session;
    private final Mapper<IntakeJobTask> mapper;

    private final PreparedStatement create;


    public IntakeJobTaskRepository(Session session,
                                   MappingManager mappingManager) {
        this.session = session;
        this.mapper = mappingManager.mapper(IntakeJobTask.class);
        this.create = session.prepare(CREATE);
    }

    private static final String CREATE = "INSERT INTO intakeJobTask" +
            "(id, created, modified, deleted, intakeJobId, source, uri, status, statusMessage) " +
            "VALUES (?,toTimestamp(now()),toTimestamp(now()),false,?,?,?,?,?)";

    public void createIntakeJobTasks(List<IntakeJobTask> intakeJobTasks) {
        BatchStatement batch = new BatchStatement();
        for(IntakeJobTask intakeJobTask : intakeJobTasks) {
            batch.add(create.bind(UuidUtil.getTimeBasedUuid().toString(), intakeJobTask.getIntakeJobId(),
                    intakeJobTask.getSource(), intakeJobTask.getUri(), intakeJobTask.getStatus(),
                    intakeJobTask.getStatusMessage()));
        }

        session.execute(batch);
    }

    private static final String GET_ALL = "SELECT id,intakeJobId, created, modified, source, uri, status, statusMessage " +
            "FROM intakeJobTask WHERE intakeJobId = ?";

    public List<IntakeJobTask> getIntakeJobTasks(String intakeJobId){
        ResultSet resultSet = session.execute(GET_ALL, intakeJobId);
        return mapper.map(resultSet).all();
    }

    private static final String UPDATE = "UPDATE intakeJobTask " +
            "SET modified = toTimestamp(now()), status = ?, statusMessage = ? " +
            "WHERE intakeJobId = ? AND uri = ? AND source = ?";

    public IntakeJobTask updateIntakeJobTask(IntakeJobTask toUpdate){
        session.execute(UPDATE, toUpdate.getStatus(), toUpdate.getStatusMessage(), toUpdate.getIntakeJobId(),
                toUpdate.getUri(), toUpdate.getSource());

        return toUpdate;
    }
}