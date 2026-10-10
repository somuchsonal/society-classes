-- liquibase formatted sql
-- changeset sonal:25-alter_table_membership

ALTER TABLE `membership` DROP FOREIGN KEY `membership_ibfk_1`;
ALTER TABLE `membership` DROP KEY `kid_category`;

ALTER TABLE `membership` ADD FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`);

ALTER TABLE `membership` ADD COLUMN `order_id` bigint(20) NOT NULL AFTER `validity_in_months`;
ALTER TABLE `membership_history` ADD COLUMN `order_id` bigint(20) NOT NULL AFTER `validity_in_months`;

ALTER TABLE `membership` ADD COLUMN `is_partly_paid` bit(1) NOT NULL AFTER `order_id`;
ALTER TABLE `membership_history` ADD COLUMN `is_partly_paid` bit(1) NOT NULL AFTER `order_id`;

UPDATE `membership` SET `order_id` = `id`;
UPDATE `membership_history` SET `order_id` = `id`;
UPDATE `membership` SET `is_partly_paid` = false;
UPDATE `membership_history` SET `is_partly_paid` = false;

ALTER TABLE `membership` ADD UNIQUE KEY `order` (`order_id`);
