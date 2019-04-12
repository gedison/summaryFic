package com.pastelpunk.summaryfic.web.exchange;

import org.apache.camel.Exchange;

import java.util.List;

public class RestExchange<T,S> {

    private Exchange exchange;

    public RestExchange(Exchange exchange){
        this.exchange = exchange;
    }

    public T getInputObject(){
        return (T) exchange.getIn().getBody();
    }

    public List<T> getInputList(){
        return (List) exchange.getIn().getBody();
    }

    public void setOutputObject(S output){
        exchange.getOut().setBody(output);
    }

    public void setOutputList(List<S> output){
        exchange.getOut().setBody(output);
    }

    public Object get(String key){
        return exchange.getIn().getHeader(key);
    }

    public void set(String key, Object value){
        exchange.getIn().setHeader(key, value);
    }

    public void syncHeaders(){
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }



}
