package com.pastelpunk.summaryfic.web.features.intake.processors.input;

import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.List;
import java.util.Objects;

public class TaggedBookAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        RestExchange<String, String> newRestExchange = new RestExchange<>(newExchange);

        List<String> output = newRestExchange.getInputList();
        if(Objects.nonNull(oldExchange)){
            RestExchange<String, Void> oldRestExchange = new RestExchange<>(oldExchange);
            output.addAll(oldRestExchange.getInputList());
        }

        newRestExchange.setOutputList(output);
        newRestExchange.syncHeaders();
        return newExchange;
    }
}
