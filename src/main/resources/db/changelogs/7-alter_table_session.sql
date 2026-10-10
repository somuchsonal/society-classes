-- liquibase formatted sql
-- changeset sonal:7-alter_table_session

ALTER TABLE `batch` DROP KEY `instructor_id`;
ALTER TABLE `batch` ADD FOREIGN KEY (`society_instructor_id`) REFERENCES `society_instructor` (`id`);

ALTER TABLE `session` ADD COLUMN `is_deleted` bit(1) NOT NULL DEFAULT 0 AFTER `society_instructor_id`;
ALTER TABLE `session_history` ADD COLUMN `is_deleted` bit(1) NOT NULL DEFAULT 0 AFTER `society_instructor_id`;

ALTER TABLE `batch` MODIFY COLUMN `is_deleted` bit(1) NOT NULL DEFAULT 0 AFTER `category_id`;
ALTER TABLE `batch_history` MODIFY COLUMN `is_deleted` bit(1) NOT NULL DEFAULT 0 AFTER `category_id`;
