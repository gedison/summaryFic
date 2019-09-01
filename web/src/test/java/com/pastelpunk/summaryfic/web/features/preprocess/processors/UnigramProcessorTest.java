package com.pastelpunk.summaryfic.web.features.preprocess.processors;


import com.google.gson.Gson;
import com.pastelpunk.summaryfic.core.models.processed.similarity.ProcessedBook;
import com.pastelpunk.summaryfic.core.models.raw.Book;
import com.pastelpunk.summaryfic.web.features.preprocess.AnalyzerConfiguration;
import com.pastelpunk.summaryfic.web.features.preprocess.processors.preprocess.UnigramProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AnalyzerConfiguration.class, UnigramProcessor.class})
public class UnigramProcessorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnigramProcessor.class);

    @Autowired
    private UnigramProcessor unigramProcessor;
    private Gson gson = new Gson();

    private Exchange exchange = mock(Exchange.class);
    private Message in = mock(Message.class);
    private Message out = mock(Message.class);

    public UnigramProcessorTest(){
        when(exchange.getIn()).thenReturn(in);
        when(exchange.getOut()).thenReturn(out);

    }

    @Test
    public void test() throws Exception {
        var file = new File(getClass().getClassLoader().getResource("book.json").getFile());
        String content = FileUtils.readFileToString(file);
        LOGGER.info(content);

        Book input = gson.fromJson(content, Book.class);
        when(in.getBody()).thenReturn(input);

        unigramProcessor.process(exchange);

        ArgumentCaptor<ProcessedBook> argumentCaptor = ArgumentCaptor.forClass(ProcessedBook.class);
        verify(out).setBody(argumentCaptor.capture());

        ProcessedBook output = argumentCaptor.getValue();

        LOGGER.info("{}", output);
        assertEquals(1178, output.getUnigrams().size());
    }
}
