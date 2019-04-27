package com.pastelpunk.summaryfic.web.features.intake.processors.cleanup;

import com.pastelpunk.summaryfic.core.features.intake.job.IntakeJobRepository;
import com.pastelpunk.summaryfic.core.features.intake.task.IntakeJobTaskRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.core.models.intake.IntakeStatus;
import com.pastelpunk.summaryfic.core.models.raw.Book;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class UpdateJobStatus implements Processor {

    private final IntakeJobTaskRepository intakeJobTaskRepository;
    private final IntakeJobRepository intakeJobRepository;

    public UpdateJobStatus(IntakeJobRepository intakeJobRepository,
                           IntakeJobTaskRepository intakeJobTaskRepository){
        this.intakeJobRepository = intakeJobRepository;
        this.intakeJobTaskRepository = intakeJobTaskRepository;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        RestExchange<IntakeJobTask, Void> restExchange = new RestExchange<>(exchange);
        var intakeJob = (IntakeJob) restExchange.get(IntakeConstants.JOB);

        List<IntakeJobTask> intakeJobTasks = intakeJobTaskRepository.getIntakeJobTasks(intakeJob.getId());
        var allTasksAreComplete =intakeJobTasks.stream()
                .allMatch(intakeJobTask -> (Objects.nonNull(intakeJobTask.getStatus()) &&
                        intakeJobTask.getStatus().contains(IntakeStatus.COMPLETE.name())));

        if(allTasksAreComplete){
            intakeJob.setStatus(IntakeStatus.COMPLETE.name());
        }else{
            intakeJob.setStatus(IntakeStatus.ERROR.name());
        }

        intakeJobRepository.updateIntakeJob(intakeJob);
    }
}
