package com.pastelpunk.summaryfic.web.features.intake.download;

import com.pastelpunk.summaryfic.core.models.Book;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.features.intake.processors.download.DownloadBook;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DownloadBookTest {

    private DownloadBook downloadBook;

    private Exchange exchange = mock(Exchange.class);
    private Message in = mock(Message.class);
    private Message out = mock(Message.class);

    public DownloadBookTest(){
        when(exchange.getIn()).thenReturn(in);
        when(exchange.getOut()).thenReturn(out);

        this.downloadBook = new DownloadBook();
    }

    @Test
    public void test() throws Exception{
        var input = "/works/15376113";

        when(in.getHeader(IntakeConstants.JOB_ID)).thenReturn(UuidUtil.getTimeBasedUuid().toString());
        when(in.getBody()).thenReturn(input);

        downloadBook.process(exchange);

        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(out).setBody(argumentCaptor.capture());

        Book output = argumentCaptor.getValue();
    }

    @Test
    public void test2() throws Exception{
        var input = "/works/18466000";

        when(in.getHeader(IntakeConstants.JOB_ID)).thenReturn(UuidUtil.getTimeBasedUuid().toString());
        when(in.getBody()).thenReturn(input);

        downloadBook.process(exchange);

        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(out).setBody(argumentCaptor.capture());

        Book output = argumentCaptor.getValue();

        assertEquals(1, output.getChapters().size());
    }

}
