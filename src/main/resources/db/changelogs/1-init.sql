--liquibase formatted sql
--changeset sonal:1-init

CREATE TABLE IF NOT EXISTS `revinfo` (
    `rev` bigint(20) NOT NULL AUTO_INCREMENT,
    `revtstmp` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `city` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name` varchar(255) NOT NULL,
    `updated_by` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `city_history` (
    `id` bigint(20) NOT NULL,
    `rev` bigint(20) NOT NULL,
    `revtype` tinyint(4) NOT NULL,
    `name` varchar(255) DEFAULT NULL,
    `updated_by` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`,`rev`),
    KEY `FKbcd29f95340c11e1cbf6892ac` (`rev`),
    CONSTRAINT `FKbcd29f95340c11e1cbf6892ac` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

