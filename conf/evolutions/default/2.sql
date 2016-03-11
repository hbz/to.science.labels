# --- !Ups
ALTER TABLE etikett ADD comment TEXT;
 
# --- !Downs
ALTER TABLE etikett DROP comment;
