# --- !Ups
ALTER TABLE etikett ADD multi_lang_serialized text;
 
# --- !Downs
ALTER TABLE etikett DROP multi_lang_serialized;
