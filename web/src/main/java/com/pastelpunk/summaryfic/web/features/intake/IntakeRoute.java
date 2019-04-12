package com.pastelpunk.summaryfic.web.features.intake;

import com.pastelpunk.summaryfic.web.features.intake.processors.download.DownloadBook;
import com.pastelpunk.summaryfic.web.features.intake.processors.input.CreateIntakeJob;
import com.pastelpunk.summaryfic.web.features.intake.processors.input.GetTaggedBooks;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class IntakeRoute extends RouteBuilder {

    private final CreateIntakeJob createIntakeJob;
    private final GetTaggedBooks getTaggedBooks;
    private final DownloadBook downloadBook;

    public IntakeRoute(@NonNull CreateIntakeJob createIntakeJob,
                       @NonNull GetTaggedBooks getTaggedBooks,
                       @NonNull DownloadBook downloadBook){

        this.createIntakeJob = createIntakeJob;
        this.getTaggedBooks = getTaggedBooks;
        this.downloadBook = downloadBook;
    }

    public void configure() throws Exception {

        //Async web call
        from("direct:startProcess")
                .process(createIntakeJob)
                .wireTap("direct:pollData");

        from("direct:pollData")
                .process(getTaggedBooks)
                .split(simple("${body.items}")).parallelProcessing()
                .process(downloadBook)
                .end()
        ;
    }
}
