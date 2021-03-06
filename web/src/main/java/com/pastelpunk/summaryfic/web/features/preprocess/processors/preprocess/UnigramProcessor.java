package com.pastelpunk.summaryfic.web.features.preprocess.processors.preprocess;

import com.pastelpunk.summaryfic.core.features.intake.task.IntakeJobTaskRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.core.models.intake.IntakeStatus;
import com.pastelpunk.summaryfic.core.models.processed.NGram;
import com.pastelpunk.summaryfic.core.models.processed.similarity.ProcessedBook;
import com.pastelpunk.summaryfic.core.models.raw.Book;
import com.pastelpunk.summaryfic.core.models.raw.Chapter;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class UnigramProcessor extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnigramProcessor.class);

    private final Analyzer analyzer;
    private final IntakeJobTaskRepository intakeJobTaskRepository;


    public UnigramProcessor(@Qualifier("basicPreprocessor") Analyzer analyzer,
                            IntakeJobTaskRepository intakeJobTaskRepository){

        this.analyzer = analyzer;
        this.intakeJobTaskRepository = intakeJobTaskRepository;
    }

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed to preprocess book {}", e.getMessage(), e);

        RestExchange<Void, Void> restExchange = new RestExchange<>(exchange);
        var intakeJob = (IntakeJobTask) restExchange.get(IntakeConstants.JOB_STATUS);
        intakeJob.setStatus(IntakeStatus.PREPROCESS_FAILED.name());
        intakeJob.setStatusMessage(e.getMessage());
        intakeJobTaskRepository.updateIntakeJobTask(intakeJob);
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<Book, ProcessedBook> restExchange = new RestExchange<>(exchange);
        Book book = restExchange.getInputObject();
        String input = book.getChapters().stream()
                .map(Chapter::getContent)
                .collect(Collectors.joining(" "));

        var output = analyze(input, analyzer);

        List<NGram> unigrams = output.stream()
                .collect(Collectors.groupingBy(value -> value))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entrySet -> entrySet.getValue().size()))
                .entrySet().stream().map(entrySet -> new NGram(entrySet.getKey(), entrySet.getValue(), 1, 1))
                .collect(Collectors.toList());

        var processedBook = new ProcessedBook();
        processedBook.setIntakeJobId(book.getIntakeJobId());
        processedBook.setSource(book.getSource());
        processedBook.setUri(book.getUri());
        processedBook.setAuthor(book.getAuthor());
        processedBook.setTitle(book.getTitle());
        processedBook.setPublished(book.getPublished());
        processedBook.setUpdated(book.getUpdated());
        processedBook.setTags(book.getTags());
        processedBook.setUnigrams(unigrams);

        restExchange.setOutputObject(processedBook);
        restExchange.syncHeaders();
    }

    private ArrayList<String> analyze(String text, Analyzer analyzer) throws IOException {
        var result = new ArrayList<String>();
        try(TokenStream tokenStream = analyzer.tokenStream("", text)) {
            var charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                result.add(charTermAttribute.toString());
            }
        }
        return result;
    }
}
