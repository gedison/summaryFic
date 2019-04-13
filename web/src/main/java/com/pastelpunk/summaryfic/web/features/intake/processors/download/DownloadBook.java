package com.pastelpunk.summaryfic.web.features.intake.processors.download;

import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DownloadBook extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadBook.class);

    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to download book {}", e);
    }

    protected void execute(Exchange exchange) throws Exception {
        RestExchange<String, String> restExchange = new RestExchange<>(exchange);

        var uri = restExchange.getInputObject();
        var url = "https://archiveofourown.org"+uri;

    }
}
