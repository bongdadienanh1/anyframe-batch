
CREATE TABLE BATCH.JOB_EXEC_HISTORY
(
  JOB_SEQ              NUMBER(19)			NOT NULL,
  JOB_ID               VARCHAR2(500 BYTE),
  IP                   VARCHAR2(20 BYTE),
  PID                  VARCHAR2(30 BYTE),
  STATUS               VARCHAR2(20 BYTE),
  EXIT_MESSAGE         CLOB,
  LOG_FILES            VARCHAR2(4000 BYTE),
  ACTIVE_THREAD_COUNT  NUMBER(4),
  CURRENT_CPU_USAGE    NUMBER(5,2),
  TOTAL_CPU_USAGE      NUMBER(5,2),
  FREE_MEMORY          NUMBER(20),
  TOTAL_MEMORY         NUMBER(20),
  CREATE_TIME          TIMESTAMP(6),
  LAST_UPDATED         TIMESTAMP(6)
);

CREATE UNIQUE INDEX BATCH.JOB_EXEC_HISTORY_PK ON BATCH.JOB_EXEC_HISTORY
(JOB_SEQ);

CREATE INDEX BATCH.JOB_EXEC_HISTORY_I1 ON BATCH.JOB_EXEC_HISTORY
(JOB_ID, STATUS);

CREATE INDEX BATCH.JOB_EXEC_HISTORY_I2 ON BATCH.JOB_EXEC_HISTORY
(CREATE_TIME);

CREATE SEQUENCE JOB_EXEC_SEQ MAXVALUE 9223372036854775807 NOCYCLE;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
CREATE TABLE BATCH.STEP_EXEC_HISTORY
(
  JOB_SEQ           NUMBER(19)			NOT NULL,
  STEP_ID           VARCHAR2(500 BYTE)	NOT NULL,
  JOB_ID            VARCHAR2(500 BYTE),
  PID               VARCHAR2(30 BYTE),
  IP                VARCHAR2(20 BYTE),
  STATUS            VARCHAR2(20 BYTE),
  EXIT_MESSAGE      CLOB,
  LOG_FILES         VARCHAR2(4000 BYTE),
  ACTIVE_THREAD_COUNT  NUMBER(4),
  CURRENT_CPU_USAGE    NUMBER(5,2),
  TOTAL_CPU_USAGE      NUMBER(5,2),
  FREE_MEMORY          NUMBER(20),
  TOTAL_MEMORY         NUMBER(20),
  CREATE_TIME       TIMESTAMP(6),
  LAST_UPDATED      TIMESTAMP(6)
);

CREATE UNIQUE INDEX BATCH.STEP_EXEC_HISTORY_PK ON BATCH.STEP_EXEC_HISTORY
(JOB_SEQ, STEP_ID);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
CREATE TABLE BATCH.RESOURCE_MANAGEMENT
(
  JOB_SEQ        NUMBER(19)				NOT NULL,
  STEP_ID        VARCHAR2(200 BYTE)		NOT NULL,
  RESOURCE_NAME  VARCHAR2(200 BYTE)		NOT NULL,
  IO_TYPE        VARCHAR2(10 BYTE)		NOT NULL,
  STATUS         VARCHAR2(10 BYTE),
  JOB_ID         VARCHAR2(200 BYTE),
  TYPE           VARCHAR2(10 BYTE),
  TRANSACTED_COUNT  NUMBER(38),
  CREATE_TIME    TIMESTAMP(6),
  UPDATE_TIME    TIMESTAMP(6)
);

CREATE UNIQUE INDEX BATCH.RESOURCE_MANAGEMENT_PK ON BATCH.RESOURCE_MANAGEMENT
(RESOURCE_NAME, JOB_SEQ, STEP_ID, IO_TYPE);

CREATE INDEX BATCH.RESOURCE_MANAGEMENT_I1 ON BATCH.RESOURCE_MANAGEMENT
(JOB_SEQ, STEP_ID);

CREATE INDEX BATCH.RESOURCE_MANAGEMENT_I2 ON BATCH.RESOURCE_MANAGEMENT
(RESOURCE_NAME, STATUS);




