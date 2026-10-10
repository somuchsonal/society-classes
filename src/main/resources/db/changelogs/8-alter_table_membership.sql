-- liquibase formatted sql
-- changeset sonal:8-alter_table_membership

ALTER TABLE `membership` ADD COLUMN `validity_in_months` int NOT NULL AFTER `category_id`;
ALTER TABLE `membership_history` ADD COLUMN `validity_in_months` int NOT NULL AFTER `category_id`;
