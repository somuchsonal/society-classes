-- liquibase formatted sql
-- changeset sonal:10-alter_table_attendance

ALTER TABLE `society_instructor_attendance` CHANGE COLUMN `instructor_id` `society_instructor_id` bigint(20) NOT NULL;
ALTER TABLE `society_instructor_attendance_history` CHANGE COLUMN `instructor_id` `society_instructor_id` bigint(20) NOT NULL;

ALTER TABLE `kid_attendance` ADD UNIQUE KEY `kid_id_session_id` (`kid_id`,`session_id`);
ALTER TABLE `society_instructor_attendance` ADD UNIQUE KEY `society_instructor_id_session_id` (`society_instructor_id`,`session_id`);

ALTER TABLE `session` MODIFY COLUMN `society_instructor_id` bigint(20) NOT NULL;
