package com.pastelpunk.summaryfic.core.models;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Entity {

    protected String id;
    protected Timestamp created;
    protected Timestamp modified;
    protected boolean deleted;
}
