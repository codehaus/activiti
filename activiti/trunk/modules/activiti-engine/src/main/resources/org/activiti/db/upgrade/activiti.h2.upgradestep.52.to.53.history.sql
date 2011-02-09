create table ACT_HI_DETAIL_TMP (
� � ID_ varchar(64) not null,
� � TYPE_ varchar(255) not null,
� � PROC_INST_ID_ varchar(64),
� � EXECUTION_ID_ varchar(64),
� � TASK_ID_ varchar(64),
� � ACT_INST_ID_ varchar(64),
� � NAME_ varchar(255),
� � VAR_TYPE_ varchar(255),
� � REV_ integer,
� � TIME_ timestamp not null,
� � BYTEARRAY_ID_ varchar(64),
� � DOUBLE_ double,
� � LONG_ bigint,
� � TEXT_ varchar(255),
� � TEXT2_ varchar(255),
� � primary key (ID_)
);

insert into ACT_HI_DETAIL_TMP
select * from ACT_HI_DETAIL;

drop table ACT_HI_DETAIL;

create table ACT_HI_DETAIL (
� � ID_ varchar(64) not null,
� � TYPE_ varchar(255) not null,
� � TIME_ timestamp not null,
� � NAME_ varchar(255),
� � PROC_INST_ID_ varchar(64),
� � EXECUTION_ID_ varchar(64),
� � TASK_ID_ varchar(64),
� � ACT_INST_ID_ varchar(64),
� � VAR_TYPE_ varchar(255),
� � REV_ integer,
� � BYTEARRAY_ID_ varchar(64),
� � DOUBLE_ double,
� � LONG_ bigint,
� � TEXT_ varchar(255),
� � TEXT2_ varchar(255),
� � primary key (ID_)
);

insert into ACT_HI_DETAIL
select * from ACT_HI_DETAIL_TMP;

drop table ACT_HI_DETAIL_TMP;
