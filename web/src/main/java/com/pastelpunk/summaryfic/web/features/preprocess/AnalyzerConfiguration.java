package com.pastelpunk.summaryfic.web.features.preprocess;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.en.PorterStemFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AnalyzerConfiguration {

    @Bean(name="basicPreprocessor")
    public Analyzer analyzer() throws IOException {
        Analyzer analyzer = org.apache.lucene.analysis.custom.CustomAnalyzer.builder()
                .withTokenizer(StandardTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(StopFilterFactory.class)
                .addTokenFilter(PorterStemFilterFactory.class)
                .build();

        return analyzer;
    }
}
