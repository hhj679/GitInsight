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


CREATE TABLE `project_stars` (
  `id` int(11) NOT NULL,
  `repo_full_name` varchar(240) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `starred_at` timestamp(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(600) DEFAULT NULL,
  `git_id` int(11) DEFAULT NULL,
  `avatar_url` varchar(600) DEFAULT NULL,
  `gravatar_id` varchar(600) DEFAULT NULL,
  `url` varchar(600) DEFAULT NULL,
  `html_url` varchar(600) DEFAULT NULL,
  `followers_url` varchar(600) DEFAULT NULL,
  `following_url` varchar(600) DEFAULT NULL,
  `gists_url` varchar(600) DEFAULT NULL,
  `starred_url` varchar(600) DEFAULT NULL,
  `subscriptions_url` varchar(600) DEFAULT NULL,
  `organizations_url` varchar(600) DEFAULT NULL,
  `repos_url` varchar(600) DEFAULT NULL,
  `events_url` varchar(600) DEFAULT NULL,
  `received_events_url` varchar(600) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `site_admin` int(1) DEFAULT NULL,
  `name` varchar(600) DEFAULT NULL,
  `company` varchar(600) DEFAULT NULL,
  `blog` varchar(600) DEFAULT NULL,
  `location` varchar(600) DEFAULT NULL,
  `email` varchar(600) DEFAULT NULL,
  `hireable` int(1) DEFAULT NULL,
  `bio` varchar(600) DEFAULT NULL,
  `public_repos` int(11) DEFAULT NULL,
  `public_gists` int(11) DEFAULT NULL,
  `followers` int(11) DEFAULT NULL,
  `following` int(11) DEFAULT NULL,
  `created_at` varchar(45) DEFAULT NULL,
  `updated_at` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8215 DEFAULT CHARSET=utf8;




CREATE TABLE `git_projects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `git_id` int(11) DEFAULT NULL,
  `name` varchar(120) DEFAULT NULL,
  `full_name` varchar(240) DEFAULT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `organization_id` int(11) DEFAULT NULL,
  `private` int(1) DEFAULT NULL COMMENT '0=false,1=true',
  `html_url` varchar(240) DEFAULT NULL,
  `description` longtext,
  `fork` int(1) DEFAULT NULL COMMENT '0=false,1=true',
  `url` varchar(240) DEFAULT NULL,
  `git_url` varchar(240) DEFAULT NULL,
  `ssh_url` varchar(240) DEFAULT NULL,
  `clone_url` varchar(240) DEFAULT NULL,
  `svn_url` varchar(240) DEFAULT NULL,
  `homepage` varchar(240) DEFAULT NULL,
  `created_at` varchar(240) DEFAULT NULL,
  `updated_at` varchar(120) DEFAULT NULL,
  `pushed_at` varchar(120) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `stargazers_count` int(11) DEFAULT NULL,
  `watchers_count` int(11) DEFAULT NULL,
  `language` varchar(120) DEFAULT NULL,
  `has_issues` int(1) DEFAULT NULL COMMENT '0=false,1=true',
  `has_downloads` int(1) DEFAULT NULL COMMENT '0=false,1=true',
  `has_wiki` int(1) DEFAULT NULL COMMENT '0=false,1=true',
  `has_pages` int(1) DEFAULT NULL COMMENT '0=false,1=true',
  `forks_count` int(11) DEFAULT NULL,
  `mirror_url` varchar(240) DEFAULT NULL,
  `forks` int(11) DEFAULT NULL,
  `open_issues` int(11) DEFAULT NULL,
  `watchers` int(11) DEFAULT NULL,
  `default_branch` varchar(120) DEFAULT NULL,
  `network_count` int(11) DEFAULT NULL,
  `subscribers_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `git_id_UNIQUE` (`git_id`)
) ENGINE=InnoDB AUTO_INCREMENT=68182 DEFAULT CHARSET=utf8;
