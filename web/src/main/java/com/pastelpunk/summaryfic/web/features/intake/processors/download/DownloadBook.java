package com.pastelpunk.summaryfic.web.features.intake.processors.download;

import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class DownloadBook extends FilterProcessor {


    protected void postProcess(Exchange exchange, Exception e) throws Exception {

    }

    protected void execute(Exchange exchange) throws Exception {
        RestExchange<String, String> restExchange = new RestExchange<>(exchange);

        var url = restExchange.getInputObject();

    }
}
