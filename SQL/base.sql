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
  `descrip` VARCHAR (200),
  PRIMARY KEY (`id`) );

ALTER TABLE `gestionGym`.`categoria` RENAME TO  `gestionGym`.`categorias` ;

CREATE  TABLE `gestionGym`.`envios` (
  `fecha` DATE NOT NULL ,
  `enviado` VARCHAR(5) NULL ,
  PRIMARY KEY (`fecha`) );
ALTER TABLE `gestionGym`.`envios` ADD COLUMN `id` INT NOT NULL AUTO_INCREMENT  AFTER `enviado` 
, DROP PRIMARY KEY 
, ADD PRIMARY KEY (`id`, `fecha`) , RENAME TO  `gestionGym`.`envios` ;

ALTER TABLE `gestionGym`.`envios` CHANGE COLUMN `enviado` `enviado` INT NULL DEFAULT NULL  ;

CREATE  TABLE `gestionGym`.`emails` (
  `email` VARCHAR(45) NOT NULL ,
  `password` VARCHAR(45) NULL ,
  PRIMARY KEY (`email`) );


ALTER TABLE `gestionGym`.`emails` ADD COLUMN `id` VARCHAR(45) NOT NULL  AFTER `password` 
, DROP PRIMARY KEY 
, ADD PRIMARY KEY (`email`, `id`) ;


ALTER TABLE `gestionGym`.`emails` CHANGE COLUMN `id` `id` INT NOT NULL AUTO_INCREMENT  
, DROP PRIMARY KEY 
, ADD PRIMARY KEY (`id`, `email`) ;
