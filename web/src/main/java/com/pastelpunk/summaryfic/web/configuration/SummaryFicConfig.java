package com.pastelpunk.summaryfic.web.configuration;

import com.pastelpunk.summaryfic.web.configuration.model.DatabaseConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties("com.pastelpunk.summaryfic")
public class SummaryFicConfig {
    private DatabaseConfig database;
}
