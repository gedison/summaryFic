package com.pastelpunk.summaryfic.web.features.preprocess;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PreProcessRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {
        from("direct:preProcessData");


    }
}
