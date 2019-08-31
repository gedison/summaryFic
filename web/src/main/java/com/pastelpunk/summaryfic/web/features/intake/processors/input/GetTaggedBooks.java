package com.pastelpunk.summaryfic.web.features.intake.processors.input;

import com.google.common.net.UrlEscapers;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.xsoup.Xsoup;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Component
public class GetTaggedBooks extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetTaggedBooks.class);

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to retrieve input {}", e.getMessage());
        throw e;
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<Integer, String> restExchange = new RestExchange<>(exchange);
        int pageNumber = restExchange.getInputObject();

        var tag = restExchange.get(IntakeConstants.SEARCH_TAG).toString();
        var url = "https://archiveofourown.org/tags/${tag}/works";
        var escapedTag = UrlEscapers.urlFragmentEscaper().escape(tag);
        escapedTag = escapedTag.replace(".", "*d*");
        url = url.replace("${tag}", escapedTag);

        URL mUrl = new URL(url + "?page=" + pageNumber);
        URLConnection connection = mUrl.openConnection();
        String out = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
        Document document = Jsoup.parse(out);
        List<String> output = getOutput(document);

        restExchange.setOutputList(output);
        restExchange.syncHeaders();
    }


    List<String> getOutput(Document document) {
        List<String> output = new ArrayList<>();

        var elements = Xsoup.compile("/html/body/div/div/div/ol[@class='work index group']/li/div/h4/a[1]")
                .evaluate(document).getElements();

        elements.forEach(element -> {
            String link = element.attributes().get("href");
            if (Objects.nonNull(link)) {
                output.add(link);
            }
        });

        return output;
    }
}
