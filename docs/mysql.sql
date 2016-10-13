create database insightDB;

grant select,update,create,drop,insert,alter,delete on insightDB.* to insight@localhost

SET SQL_SAFE_UPDATES = 0;
delete from insightdb.gitlog_source;

delimiter $$

CREATE TABLE `insightdb`.`projects` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(1200) NOT NULL,
  `grade` VARCHAR(240) DEFAULT NULL,
  `isOfficial` INT NULL DEFAULT 0,
  `logo` VARCHAR(1200) DEFAULT NULL,
  `description` LONGTEXT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id`)
)  ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `gitlog_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectid` int(11) NOT NULL,
  `commit` varchar(120) DEFAULT NULL,
  `merge` varchar(120) DEFAULT NULL,
  `author` varchar(120) DEFAULT NULL,
  `date` varchar(120) DEFAULT NULL,
  `changefiles` varchar(45) DEFAULT NULL,
  `addline` varchar(45) DEFAULT NULL,
  `delline` varchar(45) DEFAULT NULL,
  `marks` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_project_idx` (`projectid`),
  CONSTRAINT `fk_project` FOREIGN KEY (`projectid`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;


delimiter $$