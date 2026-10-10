-- liquibase formatted sql
-- changeset sonal:22-alter_table_session

ALTER TABLE `session` CHANGE COLUMN `is_deleted` `is_cancelled` bit(1) NOT NULL DEFAULT 0;
ALTER TABLE `session_history` CHANGE COLUMN `is_deleted` `is_cancelled` bit(1) NOT NULL DEFAULT 0;

ALTER TABLE `session` ADD COLUMN `cancellation_reason` varchar(63) DEFAULT NULL AFTER `is_cancelled`;
ALTER TABLE `session_history` ADD COLUMN `cancellation_reason` varchar(63) DEFAULT NULL AFTER `is_cancelled`;

ALTER TABLE `additional_session` CHANGE COLUMN `is_deleted` `is_cancelled` bit(1) NOT NULL DEFAULT 0;
ALTER TABLE `additional_session_history` CHANGE COLUMN `is_deleted` `is_cancelled` bit(1) NOT NULL DEFAULT 0;

ALTER TABLE `kid_attendance` ADD COLUMN `session_type` varchar(63) NOT NULL AFTER `session_id`;
ALTER TABLE `kid_attendance_history` ADD COLUMN `session_type` varchar(63) NOT NULL AFTER `session_id`;

ALTER TABLE `instructor_attendance` ADD COLUMN `session_type` varchar(63) NOT NULL AFTER `session_id`;
ALTER TABLE `instructor_attendance_history` ADD COLUMN `session_type` varchar(63) NOT NULL AFTER `session_id`;

UPDATE `kid_attendance` set `session_type` = 'BATCH_SESSION';
UPDATE `instructor_attendance` set `session_type` = 'BATCH_SESSION';
UPDATE `kid_attendance_history` set `session_type` = 'BATCH_SESSION';
UPDATE `instructor_attendance_history` set `session_type` = 'BATCH_SESSION';

ALTER TABLE `kid_attendance` DROP FOREIGN KEY `kid_attendance_ibfk_1`;
ALTER TABLE `kid_attendance` DROP FOREIGN KEY `kid_attendance_ibfk_2`;

ALTER TABLE `kid_attendance` DROP KEY `kid_id_session_id`;

ALTER TABLE `kid_attendance` ADD FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`);
ALTER TABLE `kid_attendance` ADD UNIQUE KEY `kid_session_type` (`kid_id`, `session_id`, `session_type`);

ALTER TABLE `instructor_attendance` DROP FOREIGN KEY `instructor_attendance_ibfk_2`;
ALTER TABLE `instructor_attendance` DROP KEY `society_instructor_id_session_id`;

ALTER TABLE `instructor_attendance` ADD UNIQUE KEY `instructor_session_type` (`instructor_id`, `session_id`, `session_type`);

ALTER TABLE `additional_session_kid_mapping` ADD UNIQUE KEY `additional_session_kid` (`additional_session_id`, `kid_id`);
