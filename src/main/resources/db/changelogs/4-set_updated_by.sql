--liquibase formatted sql
--changeset sonal:4-set-updated-by

ALTER TABLE `city` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `cluster` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `cluster_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `society` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `society_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `kid` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `kid_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `batch` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `batch_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `batch_schedule` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `batch_schedule_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `batch_kid_mapping` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `batch_kid_mapping_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `session` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `session_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `instructor` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `instructor_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `instructor_attendance` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `instructor_attendance_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `kid_topic_tracking` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `kid_topic_tracking_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `kid_attendance` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `kid_attendance_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;

ALTER TABLE `membership` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
ALTER TABLE `membership_history` MODIFY COLUMN updated_by varchar(255) DEFAULT 'System' NOT NULL;
