CREATE KEYSPACE summaryfic WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 2 };

CREATE TABLE intakeJob
(
    id            text,
    created       timestamp,
    modified      timestamp,
    deleted       boolean,
    tag           text,
    source        text,
    status        text,
    statusMessage text,
    PRIMARY KEY (id, tag, source)
);


CREATE TABLE intakeJobTask
(
    id            text,
    created       timestamp,
    modified      timestamp,
    deleted       boolean,
    intakeJobId   text,
    source        text,
    uri           text,
    status        text,
    statusMessage text,
    PRIMARY KEY (intakeJobId, uri, source)
);

CREATE TYPE tag (
    tagKey text,
    tagValue text);

CREATE TYPE chapter (
    ordering int,
    description text,
    content text);

CREATE TABLE book(
    id          text,
    created     timestamp,
    modified    timestamp,
    deleted     boolean,
    intakeJobId text,
    source        text,
    uri           text,
    title       text,
    author      text,
    updated     timestamp,
    published   timestamp,
    tags        list<frozen<tag>>,
    chapters    list<frozen<chapter>>,
    PRIMARY KEY (intakeJobId, source, uri)
);

CREATE TYPE ngram (
    stringValue text,
    count int,
    n int,
    documentCount int);

CREATE TABLE processedBook(
                     id          text,
                     created     timestamp,
                     modified    timestamp,
                     deleted     boolean,
                     intakeJobId text,
                     source        text,
                     uri           text,
                     title       text,
                     author      text,
                     updated     timestamp,
                     published   timestamp,
                     tags        list<frozen<tag>>,
                     unigrams    list<frozen<ngram>>,
                     PRIMARY KEY (intakeJobId, source, uri)
);

CREATE TABLE jobCorpus(
                          id          text,
                          created     timestamp,
                          modified    timestamp,
                          deleted     boolean,
                          intakeJobId text,
                          language    text,
                          documentCount int,
                          unigrams    list<frozen<ngram>>,
                          PRIMARY KEY (intakeJobId, language)
);


CREATE TABLE reducedDimensionalityBook(
                     id          text,
                     created     timestamp,
                     modified    timestamp,
                     deleted     boolean,
                     intakeJobId text,
                     source        text,
                     uri           text,
                     title       text,
                     author      text,
                     updated     timestamp,
                     published   timestamp,
                     values      list<double>,
                     PRIMARY KEY (intakeJobId, source, uri)
);





