-- liquibase formatted sql
-- changeset sonal:24-alter_table_kid_topic_tracking

ALTER TABLE `kid_topic_tracking` ADD COLUMN `session_type` varchar(63) NOT NULL AFTER `session_id`;
ALTER TABLE `kid_topic_tracking_history` ADD COLUMN `session_type` varchar(63) NOT NULL AFTER `session_id`;

UPDATE `kid_topic_tracking` set `session_type` = 'BATCH_SESSION';
UPDATE `kid_topic_tracking_history` set `session_type` = 'BATCH_SESSION';

ALTER TABLE `membership` ADD UNIQUE KEY `kid_category` (`kid_id`,`category_id`);
ALTER TABLE `kid_topic_tracking` DROP FOREIGN KEY `kid_topic_tracking_ibfk_3`;
