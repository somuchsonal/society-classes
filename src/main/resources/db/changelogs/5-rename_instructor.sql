-- liquibase formatted sql
-- changeset sonal:5-rename_instructor

ALTER TABLE `instructor` RENAME `society_instructor`;
ALTER TABLE `instructor_history` RENAME `society_instructor_history`;

ALTER TABLE `instructor_attendance` RENAME `society_instructor_attendance`;
ALTER TABLE `instructor_attendance_history` RENAME `society_instructor_attendance_history`;

ALTER TABLE `session` CHANGE COLUMN `instructor_id` `society_instructor_id` bigint(20) DEFAULT NULL;
ALTER TABLE `session_history` CHANGE COLUMN `instructor_id` `society_instructor_id` bigint(20) DEFAULT NULL;

ALTER TABLE `batch` CHANGE COLUMN `instructor_id` `society_instructor_id` bigint(20) DEFAULT NULL;
ALTER TABLE `batch_history` CHANGE COLUMN `instructor_id` `society_instructor_id` bigint(20) DEFAULT NULL;
