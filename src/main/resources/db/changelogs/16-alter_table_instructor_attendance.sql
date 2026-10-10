-- liquibase formatted sql
-- changeset sonal:16-alter_table_instructor_attendance

ALTER TABLE `instructor_attendance` CHANGE COLUMN `society_instructor_id` `instructor_id` bigint(20) DEFAULT NULL;
ALTER TABLE `instructor_attendance_history` CHANGE COLUMN `society_instructor_id` `instructor_id` bigint(20) DEFAULT NULL;

ALTER TABLE `topic` MODIFY COLUMN `content_type` varchar(63) NOT NULL AFTER `topic_type`;
ALTER TABLE `topic_history` MODIFY COLUMN `content_type` varchar(63) NOT NULL AFTER `topic_type`;
