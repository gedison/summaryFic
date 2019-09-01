package com.pastelpunk.summaryfic.web.configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.spark.SparkConf;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfiguration {

    @Bean
    public Cluster cluster(SummaryFicConfig summaryFicConfig) {
        return Cluster.builder()
                .addContactPoint(summaryFicConfig.getDatabase().getHostname())
                .withPort(summaryFicConfig.getDatabase().getPort())
                .build();
    }

    @Bean
    public Session session(Cluster cluster, SummaryFicConfig summaryFicConfig) {
        String keyspace = summaryFicConfig.getDatabase().getKeyspace();
        return cluster.connect(keyspace);
    }

    @Bean
    public MappingManager mappingManager(Session session) {
        return new MappingManager(session);
    }

    @Bean
    public SparkConf sparkConf(){
        return new SparkConf().setAppName("SummaryFic").setMaster("local");
    }


}
