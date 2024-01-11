# --- !Ups
ALTER TABLE etikett ADD type varchar(255);
 
# --- !Downs
ALTER TABLE etikett DROP type;
