-- liquibase formatted changes
-- changeset sonal:29-alter_table_kid_attendance

ALTER TABLE kid_attendance ADD COLUMN `attendance` varchar(63) NOT NULL AFTER `session_type`;
ALTER TABLE kid_attendance_history ADD COLUMN `attendance` varchar(63) NOT NULL AFTER `session_type`;

UPDATE kid_attendance set attendance = 'PRESENT' where has_attended is true;
UPDATE kid_attendance set attendance = 'ABSENT' where has_attended is false;
UPDATE kid_attendance_history set attendance = 'ABSENT' where has_attended is false;
UPDATE kid_attendance set attendance = 'PRESENT' where has_attended is true;

ALTER TABLE kid_attendance DROP COLUMN `has_attended`;
ALTER TABLE kid_attendance_history DROP COLUMN `has_attended`;

ALTER TABLE kid_attendance ADD KEY `attendance` (`attendance`);
