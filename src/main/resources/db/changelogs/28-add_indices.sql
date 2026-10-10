-- liquibase formatted changes
-- changeset sonal:28-add_indices

ALTER TABLE additional_session ADD KEY `category_id` (`category_id`);

ALTER TABLE batch ADD KEY `is_deleted` (`is_deleted`);

ALTER TABLE batch_schedule ADD KEY `venue` (`venue`);

ALTER TABLE chapter ADD KEY `level` (`level`);
ALTER TABLE chapter ADD KEY `sub_level` (`sub_level`);
ALTER TABLE chapter ADD KEY `sub_sub_level` (`sub_sub_level`);
ALTER TABLE chapter ADD KEY `sort_order` (`sort_order`);
ALTER TABLE chapter ADD KEY `is_deleted` (`is_deleted`);

ALTER TABLE instructor_attendance ADD KEY `instructor_id` (`instructor_id`);
ALTER TABLE instructor_attendance ADD KEY `session_type` (`session_type`);

ALTER TABLE kid_attendance ADD KEY `session_type` (`session_type`);
ALTER TABLE kid_attendance ADD KEY `has_attended` (`has_attended`);

ALTER TABLE kid_topic_tracking ADD KEY `session_type` (`session_type`);

ALTER TABLE membership ADD KEY `category_id` (`category_id`);
ALTER TABLE membership ADD KEY `is_partly_paid` (`is_partly_paid`);

ALTER TABLE `session` ADD KEY `start_time` (`start_time`);
ALTER TABLE `session` ADD KEY `end_time` (`end_time`);
ALTER TABLE `session` ADD KEY `is_cancelled` (`is_cancelled`);

ALTER TABLE `topic` ADD KEY `content_type` (`content_type`);
ALTER TABLE `topic` ADD KEY `sort_order` (`sort_order`);
ALTER TABLE `topic` ADD KEY `is_deleted` (`is_deleted`);

ALTER TABLE `video_kid_mapping` ADD FOREIGN KEY (`video_id`) REFERENCES `video` (`id`);
ALTER TABLE `video_kid_mapping` ADD FOREIGN KEY (`kid_id`) REFERENCES `kid` (`id`);
