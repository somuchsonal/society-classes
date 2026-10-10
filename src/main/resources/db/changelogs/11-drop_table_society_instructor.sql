-- liquibase formatted sql
-- changeset sonal:11-drop_table_society_instructor

ALTER TABLE `society_instructor_attendance` DROP FOREIGN KEY `society_instructor_attendance_ibfk_1`;
ALTER TABLE `batch` DROP FOREIGN KEY `batch_ibfk_2`;
ALTER TABLE `session` DROP FOREIGN KEY `session_ibfk_2`;

ALTER TABLE `society_instructor_attendance` RENAME `instructor_attendance`;
ALTER TABLE `society_instructor_attendance_history` RENAME `instructor_attendance_history`;

ALTER TABLE `session` CHANGE COLUMN `society_instructor_id` `instructor_id` bigint(20) DEFAULT NULL;
ALTER TABLE `session_history` CHANGE COLUMN `society_instructor_id` `instructor_id` bigint(20) DEFAULT NULL;

ALTER TABLE `batch` CHANGE COLUMN `society_instructor_id` `instructor_id` bigint(20) DEFAULT NULL;
ALTER TABLE `batch_history` CHANGE COLUMN `society_instructor_id` `instructor_id` bigint(20) DEFAULT NULL;

DROP TABLE `society_instructor`;
DROP TABLE `society_instructor_history`;
