package com.pastelpunk.summaryfic.web.features.intake.processors.input;

import com.pastelpunk.summaryfic.core.features.intake.job.IntakeJobRepository;
import com.pastelpunk.summaryfic.core.features.intake.job.IntakeJobService;
import com.pastelpunk.summaryfic.core.models.IntakeJob;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
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


        IntakeJob toCreate = new IntakeJob();





        toCreate = intakeJobService.createIntakeJob(toCreate);
        restExchange.setOutputObject(toCreate);
        restExchange.syncHeaders();
    }
}
