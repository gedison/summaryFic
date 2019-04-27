package com.pastelpunk.summaryfic.web.features.intake.processors.input;

import com.pastelpunk.summaryfic.core.features.intake.task.IntakeJobTaskRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.core.models.intake.IntakeStatus;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersistJobTasks extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistJobTasks.class);

    private IntakeJobTaskRepository intakeJobTaskRepository;

    public PersistJobTasks(IntakeJobTaskRepository intakeJobTaskRepository){
        this.intakeJobTaskRepository = intakeJobTaskRepository;
    }

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to persist input {}", e.getMessage());
        throw e;
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<String, IntakeJobTask> restExchange = new RestExchange<>(exchange);

        List<String> uris = restExchange.getInputList();
        IntakeJob intakeJob = (IntakeJob) restExchange.get(IntakeConstants.JOB);

        List<IntakeJobTask> intakeJobTasks = mapJobToTasks(uris, intakeJob);
        intakeJobTaskRepository.createIntakeJobTasks(intakeJobTasks);

        restExchange.syncHeaders();
        restExchange.setOutputList(intakeJobTasks);
    }

    private static List<IntakeJobTask> mapJobToTasks(List<String> uris, IntakeJob intakeJob){

        List<IntakeJobTask> ret = new ArrayList<>();
        for(String uri : uris){
            IntakeJobTask task = new IntakeJobTask();
            task.setId(UuidUtil.getTimeBasedUuid().toString());
            task.setIntakeJobId(intakeJob.getId());
            task.setSource(intakeJob.getSource());
            task.setUri(uri);
            task.setStatus(IntakeStatus.PENDING.name());
            ret.add(task);
        }

        return ret;
    }
}
