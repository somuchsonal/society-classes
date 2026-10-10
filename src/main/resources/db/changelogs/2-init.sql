--liquibase formatted sql
--changeset pranav:2-init

CREATE TABLE IF NOT EXISTS `cluster` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name` varchar(255) NOT NULL,
    `city_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `name` (`name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cluster_history` (
    `id` bigint(20) NOT NULL,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `name` varchar(255) DEFAULT NULL,
    `city_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340c11e1cbfh2k5gj` (`rev`),
    CONSTRAINT `FKbcd29f95340c11e1cbfh2k5gj` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `society` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name` varchar(255) NOT NULL,
    `address` varchar(511) NOT NULL,
    `latitude` double DEFAULT NULL,
    `longitude` double DEFAULT NULL,
    `cluster_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`cluster_id`) REFERENCES `cluster` (`id`),
    KEY `name` (`name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `society_history` (
    `id` bigint(20) NOT NULL,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `name` varchar(255) NOT NULL,
    `address` varchar(511) NOT NULL,
    `latitude` double DEFAULT NULL,
    `longitude` double DEFAULT NULL,
    `cluster_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340c11e1cbh28s5gj` (`rev`),
    CONSTRAINT `FKbcd29f95340c11e1cbh28s5gj` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `batch` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name` varchar(255) NOT NULL,
    `society_id` bigint(20) NOT NULL,
    `instructor_id` bigint(20) NOT NULL,
    `category_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`society_id`) REFERENCES `society` (`id`),
    KEY `name` (`name`),
    KEY `instructor_id` (`instructor_id`),
    KEY `category_id` (`category_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `batch_history` (
    `id` bigint(20) NOT NULL,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `name` varchar(255) NOT NULL,
    `society_id` bigint(20) NOT NULL,
    `instructor_id` bigint(20) NOT NULL,
    `category_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340c11e1cbh28h2k9` (`rev`),
    CONSTRAINT `FKbcd29f95340c11e1cbh28h2k9` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `kid` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `first_name` varchar(255) NOT NULL,
    `last_name` varchar(255) DEFAULT NULL,
    `date_of_birth` date NOT NULL,
    `parent_user_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `first_name` (`first_name`),
    KEY `last_name` (`last_name`),
    KEY `parent_user_id` (`parent_user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `kid_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `first_name` varchar(255) NOT NULL,
    `last_name` varchar(255) DEFAULT NULL,
    `date_of_birth` date NOT NULL,
    `parent_user_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340c162hi23h28h2k9` (`rev`),
    CONSTRAINT `FKbcd29f95340c162hi23h28h2k9` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `instructor` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name` varchar(255) NOT NULL,
    `category_id` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `name` (`name`),
    KEY `user_id` (`user_id`),
    KEY `category_id` (`category_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `instructor_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `name` varchar(255) NOT NULL,
    `category_id` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340c1662j13h28h2k9` (`rev`),
    CONSTRAINT `FKbcd29f95340c1662j13h28h2k9` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `chapter` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name` varchar(255) NOT NULL,
    `category_id` bigint(20) NOT NULL,
    `level` varchar(63) NOT NULL,
    `sub_level` varchar(63) NOT NULL,
    `sub_sub_level` varchar(63) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `name` (`name`),
    KEY `category_id` (`category_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `chapter_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `name` varchar(255) NOT NULL,
    `category_id` bigint(20) NOT NULL,
    `level` varchar(63) NOT NULL,
    `sub_level` varchar(63) NOT NULL,
    `sub_sub_level` varchar(63) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340c1662j13h2k82k9` (`rev`),
    CONSTRAINT `FKbcd29f95340c1662j13h2k82k9` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `topic` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name` varchar(255) NOT NULL,
    `type` varchar(63) NOT NULL,
    `chapter_id` bigint(20) NOT NULL,
    `metadata` json default NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `name` (`name`),
    KEY `type` (`type`),
    FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `topic_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `name` varchar(255) NOT NULL,
    `category_id` bigint(20) NOT NULL,
    `level` varchar(63) NOT NULL,
    `sub_level` varchar(63) NOT NULL,
    `sub_sub_level` varchar(63) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340c166j2k6h2k82k9` (`rev`),
    CONSTRAINT `FKbcd29f95340c166j2k6h2k82k9` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `session` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `end_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `batch_id` bigint(20) NOT NULL,
    `backup_instructor_id` bigint(20) DEFAULT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `start_date` (`start_date`),
    KEY `end_date` (`end_date`),
    FOREIGN KEY (`batch_id`) REFERENCES `batch` (`id`),
    FOREIGN KEY (`backup_instructor_id`) REFERENCES `instructor` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `session_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `end_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `batch_id` bigint(20) NOT NULL,
    `backup_instructor_id` bigint(20) DEFAULT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f953407293j2k9hsa82k9` (`rev`),
    CONSTRAINT `FKbcd29f953407293j2k9hsa82k9` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `membership` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `kid_id` bigint(20) NOT NULL,
    `parent_user_id` bigint(20) NOT NULL,
    `start_date` timestamp NULL DEFAULT NULL,
    `end_date` timestamp NULL DEFAULT NULL,
    `society_id` bigint(20) NOT NULL,
    `category_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `start_date` (`start_date`),
    KEY `end_date` (`end_date`),
    FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`),
    FOREIGN KEY (`society_id`) REFERENCES `society` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `membership_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `kid_id` bigint(20) NOT NULL,
    `parent_user_id` bigint(20) NOT NULL,
    `start_date` timestamp NULL DEFAULT NULL,
    `end_date` timestamp NULL DEFAULT NULL,
    `society_id` bigint(20) NOT NULL,
    `category_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f953407293j2k9hs72829` (`rev`),
    CONSTRAINT `FKbcd29f953407293j2k9hs72829` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `kid_topic_tracking` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `kid_id` bigint(20) NOT NULL,
    `topic_id` bigint(20) NOT NULL,
    `session_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`),
    FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`),
    FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
    UNIQUE KEY `kid_topic_session` (`kid_id`, `topic_id`, `session_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `kid_topic_tracking_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `kid_id` bigint(20) NOT NULL,
    `topic_id` bigint(20) NOT NULL,
    `session_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f953407293j2h28j2829` (`rev`),
    CONSTRAINT `FKbcd29f953407293j2h28j2829` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `kid_attendance` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `kid_id` bigint(20) NOT NULL,
    `session_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`),
    FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `kid_attendance_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `kid_id` bigint(20) NOT NULL,
    `session_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f97hs07293j2h28j2829` (`rev`),
    CONSTRAINT `FKbcd29f97hs07293j2h28j2829` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `instructor_attendance` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `instructor_id` bigint(20) NOT NULL,
    `session_id` bigint(20) NOT NULL,
    `seconds_delayed_by` bigint(20) DEFAULT NULL,
    `latitude` double DEFAULT NULL,
    `longitude` double DEFAULT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`instructor_id`) REFERENCES `instructor` (`id`),
    FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `instructor_attendance_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `instructor_id` bigint(20) NOT NULL,
    `session_id` bigint(20) NOT NULL,
    `seconds_delayed_by` bigint(20) DEFAULT NULL,
    `latitude` double DEFAULT NULL,
    `longitude` double DEFAULT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f729ds07293j2h28j2829` (`rev`),
    CONSTRAINT `FKbcd29f729ds07293j2h28j2829` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `batch_kid_mapping` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `batch_id` bigint(20) NOT NULL,
    `kid_id` bigint(20) NOT NULL,
    `desired_start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `desired_start_date` (`desired_start_date`),
    UNIQUE KEY `batch_kid` (`batch_id`, `kid_id`),
    FOREIGN KEY (`batch_id`) REFERENCES `batch` (`id`),
    FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `batch_kid_mapping_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `batch_id` bigint(20) NOT NULL,
    `kid_id` bigint(20) NOT NULL,
    `desired_start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f729dshfg23j2h28j2829` (`rev`),
    CONSTRAINT `FKbcd29f729dshfg23j2h28j2829` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `batch_schedule` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `batch_id` bigint(20) NOT NULL,
    `day_of_week` varchar(63) NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `venue` varchar(255) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `day_of_week` (`day_of_week`),
    KEY `start_time` (`start_time`),
    KEY `end_time` (`end_time`),
    UNIQUE KEY `batch_schedule` (`batch_id`, `day_of_week`, `start_time`, `end_time`),
    FOREIGN KEY (`batch_id`) REFERENCES `batch` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `batch_schedule_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `batch_id` bigint(20) NOT NULL,
    `day_of_week` varchar(63) NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `venue` varchar(255) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f729dshf71k8dh28j2829` (`rev`),
    CONSTRAINT `FKbcd29f729dshf71k8dh28j2829` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


