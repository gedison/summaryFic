package com.pastelpunk.summaryfic.core.features.intake.job;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.pastelpunk.summaryfic.core.models.IntakeJob;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class IntakeJobService {

    private IntakeJobRepository intakeJobRepository;

    public IntakeJobService(IntakeJobRepository intakeJobRepository){
        this.intakeJobRepository = intakeJobRepository;
    }

    public IntakeJob createIntakeJob(IntakeJob intakeJob) {
        intakeJob.setId(UuidUtil.getTimeBasedUuid().toString());
        intakeJob.setDeleted(false);
        intakeJobRepository.createIntakeJob(intakeJob);
        return intakeJobRepository.getIntakeJob(intakeJob.getId());
    }


    public IntakeJob getIntakeJob(String id){
        return intakeJobRepository.getIntakeJob(id);
    }


    public IntakeJob updateIntakeJob(IntakeJob toUpdate){
        return intakeJobRepository.updateIntakeJob(toUpdate);
    }
}
