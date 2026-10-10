-- liquibase formatted sql
-- changeset sonal:32-create_table_kid_report_completion

CREATE TABLE IF NOT EXISTS `kid_report_completion` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `kid_id` bigint(20) NOT NULL,
    `chapter_id` bigint(20) NOT NULL,
    `session_id` bigint(20) NOT NULL,
    `session_type` varchar(255) NOT NULL,
    `updated_by` varchar(63) NOT NULL DEFAULT 'SYSTEM',
    PRIMARY KEY `id` (`id`),
    KEY `session_id` (`session_id`),
    KEY `session_type` (`session_type`),
    KEY `created_at` (`created_at`),
    FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`id`),
    FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

ALTER TABLE `video` ADD KEY `created_at` (`created_at`);
