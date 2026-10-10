-- liquibase formatted sql
-- changeset sonal:15-alter_table_membership

ALTER TABLE `membership` MODIFY COLUMN `start_date` date DEFAULT NULL;
ALTER TABLE `membership_history` MODIFY COLUMN `start_date` date DEFAULT NULL;

ALTER TABLE `membership` MODIFY COLUMN `end_date` date DEFAULT NULL;
ALTER TABLE `membership_history` MODIFY COLUMN `end_date` date DEFAULT NULL;
