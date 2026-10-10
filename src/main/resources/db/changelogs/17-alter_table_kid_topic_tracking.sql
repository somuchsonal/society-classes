-- liquibase formatted sql
-- changeset sonal:17-alter_table_kid_topic_tracking

ALTER TABLE `kid_topic_tracking` DROP FOREIGN KEY `kid_topic_tracking_ibfk_1`;
ALTER TABLE `kid_topic_tracking` DROP FOREIGN KEY `kid_topic_tracking_ibfk_2`;
ALTER TABLE `kid_topic_tracking` DROP FOREIGN KEY `kid_topic_tracking_ibfk_3`;

ALTER TABLE `kid_topic_tracking` DROP KEY `kid_topic_session`;

ALTER TABLE `kid_topic_tracking` ADD FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`);
ALTER TABLE `kid_topic_tracking` ADD FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`);
ALTER TABLE `kid_topic_tracking` ADD FOREIGN KEY (`session_id`) REFERENCES `session` (`id`);

ALTER TABLE `kid_topic_tracking` ADD UNIQUE KEY `kid_id_topic_id` (`kid_id`, `topic_id`);

ALTER TABLE `cluster` ADD FOREIGN KEY (`city_id`) REFERENCES `city` (`id`);
