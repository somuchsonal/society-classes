-- liquibase formatted sql
-- changeset sonal:13-alter_table_session

DELETE FROM `session`;
DELETE FROM `session_history`;

ALTER TABLE `session` MODIFY COLUMN `start_date` date NOT NULL;
ALTER TABLE `session_history` MODIFY COLUMN `start_date` date NOT NULL;

ALTER TABLE `session` ADD COLUMN `start_time` TIME NOT NULL AFTER `start_date`;
ALTER TABLE `session_history` ADD COLUMN `start_time` TIME NOT NULL AFTER `start_date`;

ALTER TABLE `session` ADD COLUMN `end_time` TIME NOT NULL AFTER `start_time`;
ALTER TABLE `session_history` ADD COLUMN `end_time` TIME NOT NULL AFTER `start_time`;

ALTER TABLE `session` DROP COLUMN `end_date`;
ALTER TABLE `session_history` DROP COLUMN `end_date`;
