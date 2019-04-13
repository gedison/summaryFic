package com.pastelpunk.summaryfic.web.features.intake.processors.input;

import com.pastelpunk.summaryfic.core.features.intake.job.IntakeJobService;
import com.pastelpunk.summaryfic.core.models.IntakeJob;
import com.pastelpunk.summaryfic.core.models.IntakeStatus;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class CreateIntakeJob implements Processor {

    private IntakeJobService intakeJobService;

    public CreateIntakeJob(IntakeJobService intakeJobService){
        this.intakeJobService = intakeJobService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        RestExchange<Void, IntakeJob> restExchange = new RestExchange<>(exchange);

        var source = restExchange.get(IntakeConstants.SOURCE).toString();
        var searchTag = restExchange.get(IntakeConstants.SEARCH_TAG).toString();

        IntakeJob toCreate = new IntakeJob();
        toCreate.setSource(source);
        toCreate.setTag(searchTag);
        toCreate.setStatus(IntakeStatus.IN_PROGRESS.name());

        toCreate = intakeJobService.createIntakeJob(toCreate);
        restExchange.set(IntakeConstants.JOB_ID, toCreate.getId());

        restExchange.setOutputObject(toCreate);
        restExchange.syncHeaders();
    }
}
