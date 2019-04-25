package com.pastelpunk.summaryfic.web.features.intake;

import com.pastelpunk.summaryfic.web.features.intake.processors.download.DownloadBook;
import com.pastelpunk.summaryfic.web.features.intake.processors.download.PersistBook;
import com.pastelpunk.summaryfic.web.features.intake.processors.input.CreateIntakeJob;
import com.pastelpunk.summaryfic.web.features.intake.processors.input.GetTaggedBooks;
import com.pastelpunk.summaryfic.web.features.intake.processors.input.PersistJobTasks;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class IntakeRoute extends RouteBuilder {

    private final CreateIntakeJob createIntakeJob;
    private final GetTaggedBooks getTaggedBooks;
    private final PersistJobTasks persistJobTasks;
    private final DownloadBook downloadBook;
    private final PersistBook persistBook;

    public IntakeRoute(@NonNull CreateIntakeJob createIntakeJob,
                       @NonNull GetTaggedBooks getTaggedBooks,
                       @NonNull PersistJobTasks persistJobTasks,
                       @NonNull DownloadBook downloadBook,
                       @NonNull PersistBook persistBook){

        this.createIntakeJob = createIntakeJob;
        this.getTaggedBooks = getTaggedBooks;
        this.persistJobTasks = persistJobTasks;
        this.downloadBook = downloadBook;
        this.persistBook = persistBook;
    }

    public void configure() throws Exception {

        //Async web call
        from("direct:startIntake")
                .process(createIntakeJob)
                .wireTap("direct:pollData");

        from("direct:pollData")
                .log("start intake")
                .process(getTaggedBooks)
                .log("persist tasks")
                .process(persistJobTasks)
                .split(simple("${body}")).parallelProcessing()
                    .log("downloading book")
                    .process(downloadBook)
                    .log("Persisting book")
                    .process(persistBook)
                    .log("Finished split")
                .end()
                .log("Finished Process");


    }
}
