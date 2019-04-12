package com.pastelpunk.summaryfic.web.features.intake;

import com.pastelpunk.summaryfic.core.models.IntakeJob;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.concurrent.Future;

@Controller
@RequestMapping("api/v1/intake")
public class IntakeController {

    private CamelContext camelContext;

    public IntakeController(CamelContext camelContext){
        this.camelContext = camelContext;
    }

    @Produce(uri = "direct:startProcess")
    private ProducerTemplate template;

    @RequestMapping(method = RequestMethod.POST)
    public IntakeJob startIntake(String tag){
        Exchange exchange = new ExchangeBuilder(camelContext).build();
        exchange.getIn().setHeader(IntakeConstants.SEARCH_TAG, tag);
        Exchange output = template.send(exchange);
        RestExchange<IntakeJob, Void> restExchange = new RestExchange<>(output);
        return restExchange.getInputObject();
    }
}
