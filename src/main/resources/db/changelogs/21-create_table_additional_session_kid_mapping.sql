-- liquibase formatted sql
-- changeset sonal:21-create_table_additional_session_kid_mapping

CREATE TABLE IF NOT EXISTS `additional_session_kid_mapping` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `additional_session_id` bigint(20) NOT NULL,
    `kid_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) DEFAULT 'System' NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`additional_session_id`) REFERENCES `additional_session` (`id`),
    FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `additional_session_kid_mapping_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `additional_session_id` bigint(20) NOT NULL,
    `kid_id` bigint(20) NOT NULL,
    `updated_by` varchar(255) DEFAULT 'System' NOT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd2493296fa6bb3d1588497c5` (`rev`),
    CONSTRAINT `FKbcd2493296fa6bb3d1588497c5` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
