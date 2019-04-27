package com.pastelpunk.summaryfic.web.features.preprocess.processors.preprocess;

import com.pastelpunk.summaryfic.core.features.intake.task.IntakeJobTaskRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.core.models.intake.IntakeStatus;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UpdateTaskStatus extends FilterProcessor {

    private Logger LOGGER = LoggerFactory.getLogger(UpdateTaskStatus.class);
    private final IntakeJobTaskRepository intakeJobTaskRepository;

    public UpdateTaskStatus(IntakeJobTaskRepository intakeJobTaskRepository){
        this.intakeJobTaskRepository = intakeJobTaskRepository;
    }

    @Override
    protected void postProcess(Exchange exchange, Exception ignored) throws Exception {
        LOGGER.info("Failed to update status {}",ignored.getMessage(), ignored);
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<Void, Void> restExchange = new RestExchange<>(exchange);
        var intakeJob = (IntakeJobTask) restExchange.get(IntakeConstants.JOB_STATUS);
        intakeJob.setStatus(IntakeStatus.PREPROCESS_IN_PROGRESS.name());
        intakeJobTaskRepository.updateIntakeJobTask(intakeJob);
    }
}
