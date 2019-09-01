package com.pastelpunk.summaryfic.web.features.intake;

import com.pastelpunk.summaryfic.web.features.intake.processors.cleanup.UpdateJobStatus;
import com.pastelpunk.summaryfic.web.features.intake.processors.download.DownloadBook;
import com.pastelpunk.summaryfic.web.features.intake.processors.download.PersistBook;
import com.pastelpunk.summaryfic.web.features.intake.processors.input.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IntakeRoute extends RouteBuilder {

    private final CreateIntakeJob createIntakeJob;
    private final GetMaxPage getMaxPage;
    private final GetTaggedBooks getTaggedBooks;
    private final PersistJobTasks persistJobTasks;
    private final DownloadBook downloadBook;
    private final PersistBook persistBook;
    private final UpdateJobStatus updateJobStatus;

    public IntakeRoute(@NonNull CreateIntakeJob createIntakeJob,
                       @NonNull GetMaxPage getMaxPage,
                       @NonNull GetTaggedBooks getTaggedBooks,
                       @NonNull PersistJobTasks persistJobTasks,
                       @NonNull DownloadBook downloadBook,
                       @NonNull PersistBook persistBook,
                       @NonNull UpdateJobStatus updateJobStatus){

        this.createIntakeJob = createIntakeJob;
        this.getMaxPage = getMaxPage;
        this.getTaggedBooks = getTaggedBooks;
        this.persistJobTasks = persistJobTasks;
        this.downloadBook = downloadBook;
        this.persistBook = persistBook;
        this.updateJobStatus = updateJobStatus;
    }

    public void configure() throws Exception {

        errorHandler(deadLetterChannel("direct:globalExceptionHandling"));

        from("direct:globalExceptionHandling")
                .log(this.exceptionMessage().toString())
                .process(e-> log.error("", e.getException()));

        from("direct:startIntake")
                .process(createIntakeJob)
                .wireTap("direct:pollData");

        from("direct:pollData")
                .log("start intake")
                .process(getMaxPage)
                .split(simple("${body}"))
                .parallelProcessing()
                .aggregationStrategy(new TaggedBookAggregationStrategy())
                    .log("Getting tagged books")
                    .process(getTaggedBooks)
                .end()
                .log("persist tasks")
                .process(persistJobTasks)
                .split(simple("${body}")).parallelProcessing()
                    .log("downloading book")
                    .process(downloadBook)
                    .log("Persisting book")
                    .process(persistBook)
                    .to("direct:preProcessBook")
                .end()
                .to("direct:aggregate")
                .process(updateJobStatus)
                .log("Finished Process");
    }
}
