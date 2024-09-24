
/*QUERY PRINCIPALI DI MODIFICA*/
/*inserisci user*/
/*INSERT INTO `project3`.`user` (`username`,`password`) VALUES ('Arbogast', 'hans'); /**/

/*inserisci folder*/
/*INSERT INTO `project3`.`folder`(`name`,`userid`) VALUES ('Pelor', '2');  /**/

/*inserisci subfolder*/
/*INSERT INTO project3.subfolder (name, folderid) VALUES ('chess', '2'); /**/

/*inserisci document*/
/*INSERT INTO `project3`.`document` (`name`, `type`, `summary`,`userid`,`subfolderid`)
 VALUES ('Sun Song', '.txt', 'a song celebrating Pelor ','2','3'); /**/
 
 /*aggiorna nome document*/
 /*UPDATE project3.document SET name = 'Sun Song' WHERE id='4'; /**/
 
 /*sposta document in una nuova subfolder*/
 /*UPDATE project3.document SET subfolderid = '2' WHERE id = '4'; /**/
 
 /*sposta subfolder in una nuova folder*/
 /*UPDATE project3.subfolder SET folderid = '2' WHERE id = '4'; /**/
 
 /*cancella una subfolder*/
 /*delete from project3.document where true; /**/