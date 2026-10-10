-- liquibase formatted sql
-- changeset sonal:8-alter_table_batch

ALTER TABLE `batch` DROP COLUMN `name`;
ALTER TABLE `batch_history` DROP COLUMN `name`;

ALTER TABLE `batch_kid_mapping` MODIFY COLUMN `desired_start_date` date NOT NULL;
