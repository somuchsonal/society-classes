-- liquibase formatted sql
-- changeset sonal:12-alter_table_chapter

ALTER TABLE `chapter` ADD COLUMN `sort_order` int NOT NULL AFTER `sub_sub_level`;
ALTER TABLE `chapter_history` ADD COLUMN `sort_order` int NOT NULL AFTER `sub_sub_level`;

ALTER TABLE `chapter` ADD COLUMN `is_deleted` BIT(1) NOT NULL AFTER `sort_order`;
ALTER TABLE `chapter_history` ADD COLUMN `is_deleted` BIT(1) NOT NULL AFTER `sort_order`;

DELETE FROM `session`;
DELETE FROM `session_history`;

ALTER TABLE `session` ADD COLUMN `venue` varchar(255) NOT NULL AFTER `instructor_id`;
ALTER TABLE `session_history` ADD COLUMN `venue` varchar(255) NOT NULL AFTER `instructor_id`;

ALTER TABLE `topic` ADD COLUMN `is_deleted` BIT(1) NOT NULL AFTER `sort_order`;
ALTER TABLE `topic_history` ADD COLUMN `is_deleted` BIT(1) NOT NULL AFTER `sort_order`;
