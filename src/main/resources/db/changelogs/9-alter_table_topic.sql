-- liquibase formatted sql
-- changeset sonal:9-alter_table_topic

ALTER TABLE `topic` CHANGE COLUMN `type` `topic_type` varchar(63) NOT NULL;
ALTER TABLE `topic_history` CHANGE COLUMN `type` `topic_type` varchar(63) NOT NULL;

ALTER TABLE `topic` ADD COLUMN `content_type` varchar(63) NOT NULL;
ALTER TABLE `topic_history` ADD COLUMN `content_type` varchar(63) NOT NULL;

ALTER TABLE `topic` DROP COLUMN `metadata`;
ALTER TABLE `topic_history` DROP COLUMN `metadata`;

ALTER TABLE `chapter` MODIFY COLUMN `sub_level` int NOT NULL;
ALTER TABLE `chapter_history` MODIFY COLUMN `sub_level` int DEFAULT NULL;

ALTER TABLE `chapter` MODIFY COLUMN `sub_sub_level` int NOT NULL;
ALTER TABLE `chapter_history` MODIFY COLUMN `sub_sub_level` int DEFAULT NULL;

ALTER TABLE `kid_topic_tracking` ADD COLUMN `metadata` JSON DEFAULT NULL AFTER `session_id`;
ALTER TABLE `kid_topic_tracking_history` ADD COLUMN `metadata` JSON DEFAULT NULL AFTER `session_id`;

ALTER TABLE `society_instructor_attendance` DROP COLUMN `seconds_delayed_by`;
ALTER TABLE `society_instructor_attendance_history` DROP COLUMN `seconds_delayed_by`;
