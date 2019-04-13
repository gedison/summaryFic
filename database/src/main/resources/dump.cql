CREATE KEYSPACE summaryfic WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 2 };

CREATE TABLE intakeJob(
id text,
created timestamp,
modified timestamp,
deleted boolean,
tag text ,
source text,
status text,
statusMessage text,
PRIMARY KEY (id, tag, source));


CREATE TYPE validationRecord (
    fieldName text,
    errorLevel int,
    errorCode int);

CREATE TABLE medicalClaim (
id text PRIMARY KEY,
packageId text,
name text,
additionalProperties map<text, text>,
validationRecords list< frozen<validationRecord>>);

