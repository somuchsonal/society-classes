-- liquibase formatted sql
-- changeset sonal:26-create_table_video

CREATE TABLE IF NOT EXISTS `video` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `video_url` varchar(255) NOT NULL,
    `thumbnail_url` varchar(255) NOT NULL,
    `session_id` bigint(20) DEFAULT NULL,
    `updated_by` varchar(63) NOT NULL DEFAULT 'SYSTEM',
    PRIMARY KEY (`id`),
    KEY `updated_by` (`updated_by`),
    KEY `session_id` (`session_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `video_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `video_url` varchar(255) NOT NULL,
    `thumbnail_url` varchar(255) NOT NULL,
    `session_id` bigint(20) DEFAULT NULL,
    `updated_by` varchar(63) NOT NULL DEFAULT 'SYSTEM',
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340a2445a8b73c6gj` (`rev`),
    CONSTRAINT `FKbcd29f95340a2445a8b73c6gj` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `video_kid_mapping` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `video_id` bigint(20) NOT NULL,
    `kid_id` bigint(20) NOT NULL,
    `updated_by` varchar(63) NOT NULL DEFAULT 'SYSTEM',
    PRIMARY KEY (`id`),
    KEY `video_id` (`video_id`),
    KEY `kid_id` (`kid_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
