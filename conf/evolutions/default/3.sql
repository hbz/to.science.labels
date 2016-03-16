# --- !Ups
ALTER TABLE etikett ADD weight varchar(255);
 
# --- !Downs
ALTER TABLE etikett DROP weight;