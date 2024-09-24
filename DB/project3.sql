CREATE SCHEMA `project3`;

CREATE TABLE `project3`.`user` ( 
	`id` int NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(45) NOT NULL UNIQUE, 
    `password` VARCHAR(45) NOT NULL, 
    `email` VARCHAR(45) NOT NULL,
PRIMARY KEY (`id`) 
);

CREATE TABLE `project3`.`folder` ( 
	`id` int NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(45) NOT NULL, 
    `creationdate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `userid` int NOT NULL, 
PRIMARY KEY (`id`), 
KEY `folder_user` (`userid`),
CONSTRAINT `folder_user` FOREIGN KEY (`userid`) REFERENCES `project3`.`user`(`id`) ON DELETE CASCADE,
CONSTRAINT `unique_foldername` UNIQUE (`name`,`userid`)
);

CREATE TABLE `project3`.`subfolder` ( 
	`id` int NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(45) NOT NULL, 
    `creationdate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `folderid` INT NOT NULL,
PRIMARY KEY (`id`),
KEY `folder_father` (`folderid`),
CONSTRAINT `folder_father` FOREIGN KEY (`folderid`) REFERENCES `project3`.`folder`(`id`) ON DELETE CASCADE,
CONSTRAINT `unique_subfoldername` UNIQUE (`name`,`folderid`) 
);

CREATE TABLE `project3`.`document` ( 
	`id` int NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(45) NOT NULL,
    `type` VARCHAR(45) NOT NULL,
    `summary` VARCHAR(200) NOT NULL,
	`userid` int NOT NULL, 
    `subfolderid` int NOT NULL,
    `creationdate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`), 
KEY `document_user` (`userid`),
KEY `document_subfolder` (`subfolderid`),
CONSTRAINT `document_user` FOREIGN KEY (`userid`) REFERENCES `project3`.`user`(`id`) ON DELETE CASCADE,
CONSTRAINT `document_subfolder` FOREIGN KEY (`subfolderid`) REFERENCES `project3`.`subfolder`(`id`) ON DELETE CASCADE,
CONSTRAINT `unique_documentname` UNIQUE (`name`,`subfolderid`)
);