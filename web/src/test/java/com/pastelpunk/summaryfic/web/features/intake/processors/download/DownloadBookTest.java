package com.pastelpunk.summaryfic.web.features.intake.processors.download;

import com.google.gson.Gson;
import com.pastelpunk.summaryfic.core.features.intake.task.IntakeJobTaskRepository;
import com.pastelpunk.summaryfic.core.models.raw.Book;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DownloadBookTest {

    private IntakeJobTaskRepository intakeJobTaskRepository = mock(IntakeJobTaskRepository.class);
    private DownloadBook downloadBook;

    private Exchange exchange = mock(Exchange.class);
    private Message in = mock(Message.class);
    private Message out = mock(Message.class);

    public DownloadBookTest(){
        when(exchange.getIn()).thenReturn(in);
        when(exchange.getOut()).thenReturn(out);

        this.downloadBook = new DownloadBook(intakeJobTaskRepository);
    }

    @Test
    public void test() throws Exception{
        var input = "/works/15376113";
        IntakeJobTask task = new IntakeJobTask();
        task.setId(UuidUtil.getTimeBasedUuid().toString());
        task.setUri(input);
        when(in.getBody()).thenReturn(task);

        downloadBook.process(exchange);

        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(out).setBody(argumentCaptor.capture());

        Book output = argumentCaptor.getValue();
        Gson gson = new Gson();
        System.out.println(gson.toJson(output));
    }

    @Test
    public void test2() throws Exception{
        var input = "/works/18466000";
        IntakeJobTask task = new IntakeJobTask();
        task.setId(UuidUtil.getTimeBasedUuid().toString());
        task.setUri(input);
        when(in.getBody()).thenReturn(task);

        downloadBook.process(exchange);

        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(out).setBody(argumentCaptor.capture());

        Book output = argumentCaptor.getValue();

        assertEquals(1, output.getChapters().size());
    }

    @Test
    public void test3() throws Exception{
        var input = "/works/18590839";
        IntakeJobTask task = new IntakeJobTask();
        task.setId(UuidUtil.getTimeBasedUuid().toString());
        task.setUri(input);
        when(in.getBody()).thenReturn(task);

        downloadBook.process(exchange);

        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(out).setBody(argumentCaptor.capture());

        Book output = argumentCaptor.getValue();

        assertEquals(1, output.getChapters().size());
        assertTrue(!output.getTags().isEmpty());
    }

}
