package com.pastelpunk.summaryfic.web.configuration.model;

import lombok.Data;

@Data
public class DatabaseConfig {

    private String hostname;
    private int port;
    private String keyspace;
}
