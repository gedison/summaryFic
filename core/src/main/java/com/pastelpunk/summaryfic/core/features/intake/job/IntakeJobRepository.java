package com.pastelpunk.summaryfic.core.features.intake.job;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.gson.Gson;
import com.pastelpunk.summaryfic.core.features.intake.book.BookRepository;
import com.pastelpunk.summaryfic.core.models.IntakeJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public class IntakeJobRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookRepository.class);

    private final Session session;
    private final Mapper<IntakeJob> mapper;
    private final Gson gson = new Gson();

    private final PreparedStatement create;


    public IntakeJobRepository(Session session,
                               MappingManager mappingManager) {
        this.session = session;
        this.mapper = mappingManager.mapper(IntakeJob.class);
        this.create = session.prepare(CREATE);
    }

    private static final String CREATE = "INSERT INTO intakeJob json ?";

    public void createIntakeJob(IntakeJob intakeJob) {
        BatchStatement batch = new BatchStatement();
        batch.add(create.bind(gson.toJson(intakeJob)));
        session.execute(batch);
    }

    private static final String GET = "SELECT id, tag, source, status, statusMessage FROM intakeJob WHERE id = ?";

    public IntakeJob getIntakeJob(String id){
        ResultSet resultSet = session.execute(GET, id);
        return mapper.map(resultSet).one();
    }

    private static final String UPDATE = "UPDATE intakeJob " +
            "SET updated = toTimestamp(now()), status = ?, statusMessage = ? " +
            "WHERE id = ?";

    public IntakeJob updateIntakeJob(IntakeJob toUpdate){
        session.execute(UPDATE, toUpdate.getStatus(), toUpdate.getStatusMessage(), toUpdate.getId());
        return toUpdate;
    }
}