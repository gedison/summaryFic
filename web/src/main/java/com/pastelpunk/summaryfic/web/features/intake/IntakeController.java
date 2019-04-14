package com.pastelpunk.summaryfic.web.features.intake;

import com.pastelpunk.summaryfic.core.features.intake.job.IntakeJobService;
import com.pastelpunk.summaryfic.core.models.IntakeJob;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("api/v1/intake")
public class IntakeController {

    private IntakeJobService intakeJobService;
    private CamelContext camelContext;

    public IntakeController(IntakeJobService intakeJobService, CamelContext camelContext){
        this.intakeJobService = intakeJobService;
        this.camelContext = camelContext;
    }

    @Produce(uri = "direct:startIntake")
    private ProducerTemplate template;

    @RequestMapping(value = "/source/{source}", method = RequestMethod.POST)
    public ResponseEntity<IntakeJob> startIntake(@PathVariable("source") String source,
                                                 @RequestBody String tag){

        Exchange exchange = new ExchangeBuilder(camelContext).build();
        exchange.getIn().setHeader(IntakeConstants.SEARCH_TAG, tag);
        exchange.getIn().setHeader(IntakeConstants.SOURCE, source);

        Exchange output = template.send(exchange);
        RestExchange<Void, IntakeJob> restExchange = new RestExchange<>(output);
        return ResponseEntity.ok(restExchange.getOutputObject());
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<IntakeJob>> getIntakeJobs(){
        List<IntakeJob> intakeJobs = intakeJobService.getIntakeJobs();
        return ResponseEntity.ok(intakeJobs);
    }
}
