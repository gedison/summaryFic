package com.pastelpunk.summaryfic.web.features.intake.processors.input;

import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetTaggedBooks extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetTaggedBooks.class);

    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to get tagged books {}", e.getMessage(), e);
    }

    protected void execute(Exchange exchange) throws Exception {
        RestExchange<Void, String> restExchange = new RestExchange<>(exchange);
        var tag = restExchange.get(IntakeConstants.SEARCH_TAG).toString();


        List<String> output = new ArrayList<>();




        restExchange.setOutputList(output);

    }
}
