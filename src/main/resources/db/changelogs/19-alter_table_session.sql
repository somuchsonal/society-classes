-- liquibase formatted sql
-- changeset sonal:19-alter_table_session

ALTER TABLE `session` ADD COLUMN `minutes_delayed` INT DEFAULT NULL AFTER `venue`;
ALTER TABLE `session_history` ADD COLUMN `minutes_delayed` INT DEFAULT NULL AFTER `venue`;
