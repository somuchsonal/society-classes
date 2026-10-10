-- liquibase formatted changes
-- changeset sonal:27-alter_table_attendance

ALTER TABLE `kid_attendance` ADD COLUMN `has_attended` bit(1) NOT NULL AFTER `session_type`;
ALTER TABLE `kid_attendance_history` ADD COLUMN `has_attended` bit(1) NOT NULL AFTER `session_type`;

UPDATE `kid_attendance` set `has_attended` = true;
UPDATE `kid_attendance_history` set `has_attended` = true;
