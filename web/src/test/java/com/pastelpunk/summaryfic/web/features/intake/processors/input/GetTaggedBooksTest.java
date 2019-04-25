package com.pastelpunk.summaryfic.web.features.intake.processors.input;

import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class GetTaggedBooksTest {

    private GetTaggedBooks getTaggedBooks;

    private Exchange exchange = mock(Exchange.class);
    private Message in = mock(Message.class);
    private Message out = mock(Message.class);

    public GetTaggedBooksTest(){
        when(exchange.getIn()).thenReturn(in);
        when(exchange.getOut()).thenReturn(out);
        getTaggedBooks = new GetTaggedBooks();
    }

    @Test
    public void test() throws Exception{
        when(in.getHeader(IntakeConstants.SOURCE)).thenReturn("AO3");
        when(in.getHeader(IntakeConstants.SEARCH_TAG)).thenReturn("Simon Hurt");

        getTaggedBooks.process(exchange);

        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(out).setBody(argumentCaptor.capture());

        List<String> output = argumentCaptor.getValue();
        assertTrue(!output.isEmpty());
    }
}
