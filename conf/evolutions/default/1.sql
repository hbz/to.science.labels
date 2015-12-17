# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table etikett (
  uri                       varchar(255) not null,
  label                     varchar(255),
  icon                      varchar(255),
  name                      varchar(255),
  reference_type            varchar(255),
  container                 varchar(255),
  constraint pk_etikett primary key (uri))
;

create sequence etikett_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists etikett;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists etikett_seq;

