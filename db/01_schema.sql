-- UP.01 (8 семестр) / Модуль 1
-- MySQL 8.x
-- Схема БД подсистемы заявок УК: адреса, сотрудники, заявки, статусы, история.
-- 3НФ + ссылочная целостность.

SET NAMES utf8mb4;
SET time_zone = '+00:00';

CREATE DATABASE IF NOT EXISTS up01_uk
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE up01_uk;

-- Если нужно пересоздать схему при отладке (раскомментируй):
-- DROP TABLE IF EXISTS request_history;
-- DROP TABLE IF EXISTS service_request;
-- DROP TABLE IF EXISTS request_status;
-- DROP TABLE IF EXISTS employee;
-- DROP TABLE IF EXISTS address;

-- 1) Адреса (жилой фонд)
-- import_id — необязательный ключ для удобного импорта из файлов заказчика
CREATE TABLE IF NOT EXISTS address (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  import_id  VARCHAR(50)     NULL,
  city       VARCHAR(100)    NOT NULL,
  street     VARCHAR(200)    NOT NULL,
  house      VARCHAR(20)     NOT NULL,
  building   VARCHAR(20)     NULL,
  entrance   VARCHAR(10)     NULL,
  apartment  VARCHAR(10)     NULL,
  note       VARCHAR(255)    NULL,
  created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_address (city, street, house, building, entrance, apartment),
  UNIQUE KEY uq_address_import_id (import_id)
) ENGINE=InnoDB;

-- 2) Сотрудники (исполнители)
CREATE TABLE IF NOT EXISTS employee (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  import_id  VARCHAR(50)     NULL,
  last_name  VARCHAR(100)    NOT NULL,
  first_name VARCHAR(100)    NOT NULL,
  patronymic VARCHAR(100)    NULL,
  phone      VARCHAR(20)     NULL,
  email      VARCHAR(254)    NULL,
  position   VARCHAR(100)    NULL,
  is_active  TINYINT(1)      NOT NULL DEFAULT 1,
  created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_employee_import_id (import_id),
  KEY ix_employee_name (last_name, first_name, patronymic)
) ENGINE=InnoDB;

-- 3) Статусы заявок (справочник)
CREATE TABLE IF NOT EXISTS request_status (
  id         TINYINT UNSIGNED NOT NULL,
  name       VARCHAR(60)      NOT NULL,
  sort_order TINYINT UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uq_status_name (name)
) ENGINE=InnoDB;

-- 4) Заявки
-- Поля формы по заданию: адрес, ФИО заявителя, контактный телефон, описание проблемы,
-- ответственный исполнитель, статус.
CREATE TABLE IF NOT EXISTS service_request (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  import_id           VARCHAR(50)     NULL,
  address_id          BIGINT UNSIGNED NOT NULL,
  applicant_full_name VARCHAR(200)    NOT NULL,
  applicant_phone     VARCHAR(20)     NOT NULL,
  problem_description TEXT            NOT NULL,
  employee_id         BIGINT UNSIGNED NULL,
  status_id           TINYINT UNSIGNED NOT NULL,
  created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  closed_at           DATETIME        NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_request_import_id (import_id),
  KEY ix_request_address (address_id),
  KEY ix_request_employee (employee_id),
  KEY ix_request_status (status_id),
  KEY ix_request_created_at (created_at),
  CONSTRAINT fk_request_address
    FOREIGN KEY (address_id) REFERENCES address(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_request_employee
    FOREIGN KEY (employee_id) REFERENCES employee(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT fk_request_status
    FOREIGN KEY (status_id) REFERENCES request_status(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 5) История выполнения заявок (по сотрудникам и адресам)
CREATE TABLE IF NOT EXISTS request_history (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  request_id BIGINT UNSIGNED NOT NULL,
  employee_id BIGINT UNSIGNED NULL,
  status_id  TINYINT UNSIGNED NOT NULL,
  changed_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  comment    VARCHAR(255)    NULL,
  PRIMARY KEY (id),
  KEY ix_history_request (request_id),
  KEY ix_history_employee (employee_id),
  KEY ix_history_status (status_id),
  KEY ix_history_changed_at (changed_at),
  CONSTRAINT fk_history_request
    FOREIGN KEY (request_id) REFERENCES service_request(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_history_employee
    FOREIGN KEY (employee_id) REFERENCES employee(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT fk_history_status
    FOREIGN KEY (status_id) REFERENCES request_status(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 6) Триггеры для автоматического ведения истории
DELIMITER $$

CREATE TRIGGER trg_request_ai_history
AFTER INSERT ON service_request
FOR EACH ROW
BEGIN
  INSERT INTO request_history (request_id, employee_id, status_id, changed_at, comment)
  VALUES (NEW.id, NEW.employee_id, NEW.status_id, NEW.created_at, 'Создание заявки');
END$$

CREATE TRIGGER trg_request_au_history
AFTER UPDATE ON service_request
FOR EACH ROW
BEGIN
  -- Записываем историю только когда поменялся статус или исполнитель.
  IF (NEW.status_id <> OLD.status_id) OR (NOT (NEW.employee_id <=> OLD.employee_id)) THEN
    INSERT INTO request_history (request_id, employee_id, status_id, changed_at, comment)
    VALUES (NEW.id, NEW.employee_id, NEW.status_id, NOW(), 'Изменение статуса/исполнителя');
  END IF;

  -- Если заявка закрыта и closed_at выставлен впервые
  IF (NEW.closed_at IS NOT NULL) AND (OLD.closed_at IS NULL) THEN
    INSERT INTO request_history (request_id, employee_id, status_id, changed_at, comment)
    VALUES (NEW.id, NEW.employee_id, NEW.status_id, NEW.closed_at, 'Закрытие заявки');
  END IF;
END$$

DELIMITER ;
