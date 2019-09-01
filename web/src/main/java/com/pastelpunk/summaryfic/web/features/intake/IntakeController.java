package com.pastelpunk.summaryfic.web.features.intake;

import com.pastelpunk.summaryfic.core.features.intake.job.IntakeJobRepository;
import com.pastelpunk.summaryfic.core.features.intake.task.IntakeJobTaskRepository;
import com.pastelpunk.summaryfic.core.features.preprocess.corpus.CorpusRepository;
import com.pastelpunk.summaryfic.core.features.processed.simliarity.lexicographical.ReducedDimensionalityBookRepository;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJob;
import com.pastelpunk.summaryfic.core.models.intake.IntakeJobTask;
import com.pastelpunk.summaryfic.core.models.intake.IntakeRequest;
import com.pastelpunk.summaryfic.core.models.processed.similarity.JobCorpus;
import com.pastelpunk.summaryfic.core.models.processed.similarity.ReducedDimensionalityBook;
import com.pastelpunk.summaryfic.core.models.processed.similarity.lexicographical.LexicographicJob;
import com.pastelpunk.summaryfic.web.exchange.RestExchange;
import com.pastelpunk.summaryfic.web.features.process.similarity.lexicographic.processors.PersistReducedDimensionalityBooks;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("api/v1/intakeJobs")
public class IntakeController {

    private final IntakeJobRepository intakeJobRepository;
    private final IntakeJobTaskRepository intakeJobTaskRepository;
    private final CorpusRepository corpusRepository;
    private final ReducedDimensionalityBookRepository reducedDimensionalityBookRepository;
    private final CamelContext camelContext;

    public IntakeController(IntakeJobRepository intakeJobRepository,
                            IntakeJobTaskRepository intakeJobTaskRepository,
                            CorpusRepository corpusRepository,
                            ReducedDimensionalityBookRepository reducedDimensionalityBookRepository,
                            CamelContext camelContext){

        this.intakeJobRepository = intakeJobRepository;
        this.intakeJobTaskRepository = intakeJobTaskRepository;
        this.corpusRepository = corpusRepository;
        this.reducedDimensionalityBookRepository = reducedDimensionalityBookRepository;
        this.camelContext = camelContext;
    }

    @Produce(uri = "direct:startIntake")
    private ProducerTemplate template;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IntakeJob> startIntake(@RequestBody IntakeRequest intakeRequest){

        Exchange exchange = new ExchangeBuilder(camelContext).build();
        exchange.getIn().setHeader(IntakeConstants.SEARCH_TAG, intakeRequest.getTag());
        exchange.getIn().setHeader(IntakeConstants.SOURCE, intakeRequest.getSource());

        Exchange output = template.send(exchange);

        RestExchange<IntakeJob, IntakeJob> restExchange = new RestExchange<>(output);
        return ResponseEntity.ok(restExchange.getInputObject());
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<IntakeJob>> getIntakeJobs(){
        List<IntakeJob> intakeJobs = intakeJobRepository.getIntakeJobs();
        return ResponseEntity.ok(intakeJobs);
    }

    @RequestMapping(value="/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<IntakeJob> getIntakeJob(@PathVariable("jobId") String jobId){
        IntakeJob intakeJob = intakeJobRepository.getIntakeJob(jobId);
        return ResponseEntity.ok(intakeJob);
    }

    @RequestMapping(value="/{jobId}/tasks", method = RequestMethod.GET)
    public ResponseEntity<List<IntakeJobTask>> getIntakeJobTasks(@PathVariable("jobId") String jobId){
        List<IntakeJobTask> intakeJobTasks = intakeJobTaskRepository.getIntakeJobTasks(jobId);
        return ResponseEntity.ok(intakeJobTasks);
    }

    @RequestMapping(value="/{jobId}/corpus", method = RequestMethod.GET)
    public ResponseEntity<List<JobCorpus>> getIntakeCorpus(@PathVariable("jobId") String jobId){
        List<JobCorpus> corpus = corpusRepository.getJobCorpus(jobId);
        return ResponseEntity.ok(corpus);
    }

    @RequestMapping(value="/{jobId}/similarity", method = RequestMethod.GET)
    public ResponseEntity<List<ReducedDimensionalityBook>> getSimilarity(@PathVariable("jobId") String jobId){
        List<ReducedDimensionalityBook> output = reducedDimensionalityBookRepository.getReducedDimensionalityBook(jobId);
        return ResponseEntity.ok(output);
    }

    @RequestMapping(value="/{jobId}/similarityCsv", method = RequestMethod.GET)
    public ResponseEntity<String> getSimilarityCsv(@PathVariable("jobId") String jobId){
        List<ReducedDimensionalityBook> output = reducedDimensionalityBookRepository.getReducedDimensionalityBook(jobId);
        String stringOutput = output.stream()
                .map(book ->{
            List<String> values = new ArrayList<>();
            values.add(book.getTitle().replaceAll(",",""));
            values.add(book.getAuthor().replaceAll(",",""));
            book.getValues().forEach(value -> values.add(Double.toString(value)));
            return String.join(",", values);
        }).collect(Collectors.joining("<br>"));




        return ResponseEntity.ok(stringOutput);







    }
}
