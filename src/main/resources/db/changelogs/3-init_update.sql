--liquibase formatted sql
--changeset pranav:3-init_update

ALTER TABLE `membership` DROP COLUMN `parent_user_id`;
ALTER TABLE `membership_history` DROP COLUMN `parent_user_id`;

DROP TABLE `city_history`;

ALTER TABLE `session` CHANGE COLUMN `backup_instructor_id` `instructor_id` bigint(20) DEFAULT NULL;
ALTER TABLE `session_history` CHANGE COLUMN `backup_instructor_id` `instructor_id` bigint(20) DEFAULT NULL;

ALTER TABLE `batch` ADD COLUMN `is_deleted` bit(1) NOT NULL DEFAULT 0;
ALTER TABLE `batch_history` ADD COLUMN `is_deleted` bit(1) NOT NULL DEFAULT 0;

ALTER TABLE `topic` ADD COLUMN `sort_order` int(11) NOT NULL AFTER `chapter_id`;

DROP TABLE `topic_history`;

CREATE TABLE IF NOT EXISTS `topic_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `name` varchar(255) NOT NULL,
    `type` varchar(63) NOT NULL,
    `chapter_id` bigint(20) NOT NULL,
    `sort_order` int(11) NOT NULL,
    `metadata` json default NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340c166j2k6h2j2kd6` (`rev`),
    CONSTRAINT `FKbcd29f95340c166j2k6h2j2kd6` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

