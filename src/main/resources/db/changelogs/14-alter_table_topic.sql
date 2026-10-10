-- liquibase formatted sql
-- changeset sonal:14-alter_table_chapter

ALTER TABLE `topic` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `topic_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `chapter` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `chapter_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
