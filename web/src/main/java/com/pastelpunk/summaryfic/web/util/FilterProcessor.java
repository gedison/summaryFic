package com.pastelpunk.summaryfic.web.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public abstract class FilterProcessor implements Processor {

    protected abstract void postProcess(Exchange exchange, Exception e) throws Exception;
    protected abstract void execute(Exchange exchange) throws Exception;

    public void process(Exchange exchange) throws Exception {
        try{
            execute(exchange);
        }catch (Exception e){
            postProcess(exchange, e);
        }
    }
}
