package com.pastelpunk.summaryfic.core.models;

import lombok.Data;

import java.util.Date;


@Data
public class Entity {

    protected String id;
    protected Date created;
    protected Date modified;
    protected boolean deleted;
}
