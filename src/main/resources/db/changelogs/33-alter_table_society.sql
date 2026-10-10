-- liquibase formatted sql
-- changeset sonal:33-alter_table_society

ALTER TABLE `society` ADD COLUMN `society_image_url` varchar(255) DEFAULT NULL AFTER `cluster_id`;
ALTER TABLE `society` ADD COLUMN `society_video_url` varchar(255) DEFAULT NULL AFTER `society_image_url`;
ALTER TABLE `society` ADD COLUMN `programs` JSON DEFAULT NULL AFTER `society_video_url`;
ALTER TABLE `society` ADD COLUMN `instructor_ids` JSON DEFAULT NULL AFTER `programs`;
ALTER TABLE `society` ADD COLUMN `reviews` JSON DEFAULT NULL AFTER `instructor_ids`;

ALTER TABLE `society_history` ADD COLUMN `society_image_url` varchar(255) DEFAULT NULL AFTER `cluster_id`;
ALTER TABLE `society_history` ADD COLUMN `society_video_url` varchar(255) DEFAULT NULL AFTER `society_image_url`;
ALTER TABLE `society_history` ADD COLUMN `programs` JSON DEFAULT NULL AFTER `society_video_url`;
ALTER TABLE `society_history` ADD COLUMN `instructor_ids` JSON DEFAULT NULL AFTER `programs`;
ALTER TABLE `society_history` ADD COLUMN `reviews` JSON DEFAULT NULL AFTER `instructor_ids`;
