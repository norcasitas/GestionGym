drop database if exists gestionGym;
create database gestionGym;

use gestionGym;

CREATE  TABLE `gestionGym`.`categoria` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `nombre` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) );

CREATE  TABLE `gestionGym`.`datos` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `descripcion` VARCHAR(200) NULL ,
  `categoria_id` INT NULL ,
  `ingreso_egreso` VARCHAR(7) NULL ,
  PRIMARY KEY (`id`) );

CREATE  TABLE `gestionGym`.`gastos` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `dato_id` INT NULL ,
  `monto` FLOAT NULL ,
  `fecha` DATE NULL ,
  'desc' VARCHAR (200),
  PRIMARY KEY (`id`) );

ALTER TABLE `gestionGym`.`categoria` RENAME TO  `gestionGym`.`categorias` ;
