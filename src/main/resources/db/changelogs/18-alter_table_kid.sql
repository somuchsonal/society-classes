-- liquibase formatted sql
-- changeset sonal:18-alter_table_kid

ALTER TABLE `kid` MODIFY COLUMN `date_of_birth` DATE DEFAULT NULL;
ALTER TABLE `kid_history` MODIFY COLUMN `date_of_birth` DATE DEFAULT NULL;

ALTER TABLE `batch_kid_mapping` MODIFY COLUMN `desired_start_date` DATE DEFAULT NULL;
ALTER TABLE `batch_kid_mapping_history` MODIFY COLUMN `desired_start_date` DATE DEFAULT NULL;
