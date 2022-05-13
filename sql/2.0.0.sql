# grant select,insert,update,delete on bot_group_topic to raccoon@'%'
CREATE TABLE `bot_admin_action` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) unsigned NOT NULL,
  `member_id` bigint(20) unsigned NOT NULL,
  `script_id` bigint(20) unsigned NOT NULL,
  `status` tinyint(4) NOT NULL,
  `type` tinyint(4) unsigned NOT NULL,
  `quota_cnt` int(11) DEFAULT NULL,
  `quota_step` int(11) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expire_time` timestamp NULL DEFAULT NULL,
  `is_del` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `bot_message` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `sender_id` bigint(20) unsigned NOT NULL,
  `group_id` bigint(20) NOT NULL,
  `msg_id` bigint(20) NOT NULL,
  `content` varchar(10000) NOT NULL,
  `label_crtype` tinyint(3) unsigned DEFAULT NULL,
  `label_type` int(10) unsigned DEFAULT NULL,
  `label_first` varchar(45) DEFAULT NULL,
  `label_second` varchar(45) DEFAULT NULL,
  `lf_pby` float DEFAULT NULL,
  `ls_pby` float DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_del` bit(1) NOT NULL,
  `is_trainable` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `idx_sender_time` (`create_time`,`sender_id`,`label_first`),
  KEY `idx_topic` (`group_id`,`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `bot_script` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) unsigned NOT NULL,
  `script_url` varchar(256) CHARACTER SET latin1 NOT NULL,
  `script_entrance` varchar(32) CHARACTER SET latin1 NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_del` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `bot_used_invcode` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) unsigned NOT NULL,
  `solution_id` int(10) unsigned NOT NULL,
  `invcode` varchar(64) CHARACTER SET latin1 NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_del` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `invcode_idx` (`invcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `bot_group_topic` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) unsigned NOT NULL,
  `sender_id` bigint(20) unsigned NOT NULL,
  `msg_start_id` bigint(20) unsigned NOT NULL,
  `msg_end_id` bigint(20) unsigned DEFAULT NULL,
  `msg_ignore_ids_json` varchar(2000) CHARACTER SET latin1 NOT NULL,
  `status` tinyint(4) NOT NULL,
  `type` int(10) unsigned NOT NULL,
  `title` varchar(128) CHARACTER SET latin1 NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `solution` (
  `solution_id` int(11) NOT NULL AUTO_INCREMENT,
  `problem_id` int(11) NOT NULL DEFAULT '0',
  `user_id` char(48) NOT NULL,
  `nick` char(20) NOT NULL DEFAULT '',
  `time` int(11) NOT NULL DEFAULT '0',
  `memory` int(11) NOT NULL DEFAULT '0',
  `in_date` datetime NOT NULL DEFAULT '2016-05-13 19:24:00',
  `result` smallint(6) NOT NULL DEFAULT '0',
  `language` int(10) unsigned NOT NULL DEFAULT '0',
  `ip` char(46) NOT NULL,
  `contest_id` int(11) DEFAULT '0',
  `valid` tinyint(4) NOT NULL DEFAULT '1',
  `num` tinyint(4) NOT NULL DEFAULT '-1',
  `code_length` int(11) NOT NULL DEFAULT '0',
  `judgetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `pass_rate` decimal(3,2) unsigned NOT NULL DEFAULT '0.00',
  `lint_error` int(10) unsigned NOT NULL DEFAULT '0',
  `judger` char(16) NOT NULL DEFAULT 'LOCAL',
  `solution_uuid` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`solution_id`),
  UNIQUE KEY `solution_uuid_UNIQUE` (`solution_uuid`),
  KEY `uid` (`user_id`),
  KEY `pid` (`problem_id`),
  KEY `res` (`result`),
  KEY `cid` (`contest_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 ROW_FORMAT=FIXED;




