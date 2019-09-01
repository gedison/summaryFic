package com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic;

import com.pastelpunk.summaryfic.core.models.processed.similarity.lexicographical.LexicographicJob;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("api/v1/lexicographic")
public class LexicographicController {

    private final CamelContext camelContext;

    public LexicographicController(CamelContext camelContext){
        this.camelContext = camelContext;
    }

    @Produce(uri = "direct:lexicographicSimilarity")
    private ProducerTemplate template;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> startIntake(@RequestBody LexicographicJob lexicographicJob){

        Exchange exchange = new ExchangeBuilder(camelContext).build();
        exchange.getIn().setHeader(IntakeConstants.JOB_ID, lexicographicJob.getJobId());
        exchange.getIn().setHeader(IntakeConstants.LANGUAGE, lexicographicJob.getLanguage());

        Exchange output = template.send(exchange);

        //RestExchange<IntakeJob, IntakeJob> restExchange = new RestExchange<>(output);
        return ResponseEntity.ok("OK");
    }


}
