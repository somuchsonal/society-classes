-- liquibase formatted sql
-- changeset sonal:30-create_table_lead

CREATE TABLE IF NOT EXISTS `lead` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `kid_name` varchar(255) NOT NULL,
    `contact` varchar(255) NOT NULL,
    `source` varchar(255) NOT NULL,
    `society_id` bigint(20) NOT NULL,
    `category_id` bigint(20) DEFAULT NULL,
    `parent_name` varchar(255) DEFAULT NULL,
    `age` int DEFAULT NULL,
    `apartment` varchar(255) DEFAULT NULL,
    `alternate_contact` varchar(255) DEFAULT NULL,
    `updated_by` varchar(63) NOT NULL DEFAULT 'SYSTEM',
    PRIMARY KEY `id` (`id`),
    KEY `kid_name` (`kid_name`),
    KEY `contact` (`contact`),
    KEY `source` (`source`),
    FOREIGN KEY (`society_id`) REFERENCES `society` (`id`),
    KEY `category_id` (`category_id`),
    KEY `age` (`age`),
    KEY `parent_name` (`parent_name`),
    KEY `alternate_contact` (`alternate_contact`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `lead_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `kid_name` varchar(255) NOT NULL,
    `contact` varchar(255) NOT NULL,
    `source` varchar(255) NOT NULL,
    `society_id` bigint(20) NOT NULL,
    `category_id` bigint(20) DEFAULT NULL,
    `parent_name` varchar(255) DEFAULT NULL,
    `age` int DEFAULT NULL,
    `apartment` varchar(255) DEFAULT NULL,
    `alternate_contact` varchar(255) DEFAULT NULL,
    `updated_by` varchar(63) NOT NULL DEFAULT 'SYSTEM',
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd28f96340c12e1cbh28s5gr` (`rev`),
    CONSTRAINT `FKbcd28f96340c12e1cbh28s5gr` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
