-- liquibase formatted sql
-- changeset sonal:23-alter_table_additional_session

ALTER TABLE `additional_session` CHANGE COLUMN `date` `start_date` date NOT NULL;
ALTER TABLE `additional_session_history` CHANGE COLUMN `date` `start_date` date NOT NULL;

ALTER TABLE `additional_session` ADD COLUMN `category_id` bigint(20) NOT NULL AFTER `end_time`;
ALTER TABLE `additional_session_history` ADD COLUMN `category_id` bigint(20) NOT NULL AFTER `end_time`;
