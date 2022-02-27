CREATE TABLE METRICS(
 METRIC_UUID varchar(36) NOT NULL,
 NAME varchar(255) NOT NULL ,
 METRIC_TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 DURATION_MS INT NOT NULL,
 DESCRIPTION text DEFAULT NULL,
 PRIMARY KEY (METRIC_UUID)
);

--CREATE TYPE LOG_LEVEL AS ENUM ('EMPTY', 'DEBUG', 'WARN', 'INFO', 'ERROR');

--CREATE TYPE LOG_TYPE AS ENUM ('EMPTY', 'REQUEST', 'EXCEPTION', 'TRACE', 'DEPENDENCY');

CREATE TABLE TRANSACTIONS(
 TRANSACTION_UUID varchar(36) NOT NULL,
 METRIC_UUID varchar(36) NOT NULL ,
 NAME varchar(255) default NULL,
 TRANSACTION_TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 TYPE varchar(255) default NULL,
 TRANSACTION_LEVEL varchar(255) default NULL,
 TRANSACTION_VALUE text DEFAULT NULL,
 TRANSACTION_CODE INT DEFAULT 0,
 PRIMARY KEY (TRANSACTION_UUID),
 CONSTRAINT fk_metrics
  FOREIGN KEY(METRIC_UUID) 
  REFERENCES METRICS(METRIC_UUID)
);