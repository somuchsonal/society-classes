-- liquibase formatted sql
-- changeset sonal:31-alter_table_chapter

ALTER TABLE `chapter` ADD COLUMN `chapter_type` varchar(255) NOT NULL AFTER `sub_sub_level`;
ALTER TABLE `chapter_history` ADD COLUMN `chapter_type` varchar(255) NOT NULL AFTER `sub_sub_level`;

ALTER TABLE `topic` ADD COLUMN `completion_type` varchar(255) NOT NULL AFTER `content_type`;
ALTER TABLE `topic_history` ADD COLUMN `completion_type` varchar(255) NOT NULL AFTER `content_type`;
ALTER TABLE `topic` ADD COLUMN `metadata` json DEFAULT NULL AFTER `chapter_id`;
ALTER TABLE `topic_history` ADD COLUMN `metadata` json DEFAULT NULL AFTER `chapter_id`;

UPDATE `chapter` SET `chapter_type` = 'LESSON';
UPDATE `chapter_history` SET `chapter_type` = 'LESSON';
UPDATE `topic` SET `completion_type` = 'NONE';
UPDATE `topic_history` SET `completion_type` = 'NONE';

ALTER TABLE `chapter` ADD KEY `chapter_type` (`chapter_type`);
ALTER TABLE `topic` ADD KEY `completion_type` (`completion_type`);
