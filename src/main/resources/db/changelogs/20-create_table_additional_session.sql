-- liquibase formatted sql
-- changeset sonal:20-create_table_additional_session

CREATE TABLE IF NOT EXISTS `additional_session` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `date` date NOT NULL,
    `start_time` time NOT NULL,
    `end_time` time NOT NULL,
    `instructor_id` bigint(20) NOT NULL,
    `link` varchar(255) NOT NULL,
    `is_deleted` bit(1) DEFAULT 0 NOT NULL,
    `updated_by` varchar(255) DEFAULT 'System' NOT NULL,
    PRIMARY KEY (`id`),
    KEY `date` (`date`),
    KEY `start_time` (`start_time`),
    KEY `end_time` (`end_time`),
    KEY `instructor_id` (`instructor_id`),
    KEY `is_deleted` (`is_deleted`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `additional_session_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `date` date NOT NULL,
    `start_time` time NOT NULL,
    `end_time` time NOT NULL,
    `instructor_id` bigint(20) NOT NULL,
    `link` varchar(255) NOT NULL,
    `is_deleted` bit(1) DEFAULT 0 NOT NULL,
    `updated_by` varchar(255) DEFAULT 'System' NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f9827206bce1edafa18s3` (`rev`),
    CONSTRAINT `FKbcd29f9827206bce1edafa18s3` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

ALTER TABLE `batch_kid_mapping` DROP COLUMN `desired_start_date`;
ALTER TABLE `batch_kid_mapping_history` DROP COLUMN `desired_start_date`;

ALTER TABLE `chapter` MODIFY COLUMN `is_deleted` BIT(1) DEFAULT 0 NOT NULL;
ALTER TABLE `chapter_history` MODIFY COLUMN `is_deleted` BIT(1) DEFAULT 0 NOT NULL;

ALTER TABLE `topic` MODIFY COLUMN `is_deleted` BIT(1) DEFAULT 0 NOT NULL;
ALTER TABLE `topic_history` MODIFY COLUMN `is_deleted` BIT(1) DEFAULT 0 NOT NULL;
