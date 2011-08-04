CREATE TABLE `ps_accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(16) NOT NULL,
  `amount` int(11) NOT NULL DEFAULT '0',
  `comment` varchar(128) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `code_index` (`code`(8))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='The accounts table of the payment system'

CREATE TABLE `ps_log` (
  `external_id` varchar(32) NOT NULL,
  `to_code` varchar(16) NOT NULL,
  `from_code` varchar(16) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `processed_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `comment` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `ps_transactions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `to_account` int(11) NOT NULL,
  `from_account` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '0',
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `initiator_account` int(11) NOT NULL,
  `comment` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `to_account_fk` (`to_account`),
  KEY `from_account_fk` (`from_account`),
  KEY `initiator_account_fk` (`initiator_account`),
  CONSTRAINT `from_account_fk` FOREIGN KEY (`from_account`) REFERENCES `ps_accounts` (`id`),
  CONSTRAINT `initiator_account_fk` FOREIGN KEY (`initiator_account`) REFERENCES `ps_accounts` (`id`),
  CONSTRAINT `to_account_fk` FOREIGN KEY (`to_account`) REFERENCES `ps_accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8 COMMENT='Transactions between accounts'

CREATE TABLE `ps_tx_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `amount` int(11) DEFAULT NULL,
  `to_code` varchar(16) DEFAULT NULL,
  `from_code` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

