package com.pastelpunk.summaryfic.web.features.preprocess.processors.aggregate;

import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.pastelpunk.summaryfic.core.features.preprocess.book.ProcessedBookRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.core.models.intake.IntakeStatus;
import com.pastelpunk.summaryfic.core.models.processed.NGram;
import com.pastelpunk.summaryfic.core.models.processed.ProcessedBook;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.intake.IntakeConstants;
import com.pastelpunk.summaryfic.web.util.FilterProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class AggregateCounts extends FilterProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateCounts.class);

    private final ProcessedBookRepository processedBookRepository;
    private final Mapper<ProcessedBook> mapper;

    public AggregateCounts(ProcessedBookRepository processedBookRepository,
                           MappingManager mappingManager){

        this.processedBookRepository = processedBookRepository;
        this.mapper = mappingManager.mapper(ProcessedBook.class);
    }

    @Override
    protected void postProcess(Exchange exchange, Exception e) throws Exception {
        LOGGER.info("Failed during aggregation {}", e.getMessage(), e);
    }

    @Override
    protected void execute(Exchange exchange) throws Exception {
        RestExchange<IntakeJobTask, Void> restExchange = new RestExchange<>(exchange);
        var intakeJob = (IntakeJob) restExchange.get(IntakeConstants.JOB);
        var unigramStream = processedBookRepository.getUnigramStream(intakeJob);


        unigramStream
                .map(
                        row -> {

                            LOGGER.info("{}", row);
                            return row;
                        })
                .collect(Collectors.toList());

        LOGGER.info("SDF");
    }

    private List<NGram> getNGrams(Row row){
        return row.getList(0, NGram.class);
    }
}
