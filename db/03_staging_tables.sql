CREATE TABLE IF NOT EXISTS stg_housing_fund (
  import_id     VARCHAR(50)  NULL,
  address       VARCHAR(255) NOT NULL,
  floors        INT          NULL,
  apartments    INT          NULL,
  build_year    INT          NULL,
  area_m2       DECIMAL(12,2) NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS stg_employees (
  import_id   VARCHAR(50)  NULL,
  last_name   VARCHAR(100) NOT NULL,
  first_name  VARCHAR(100) NOT NULL,
  patronymic  VARCHAR(100) NULL,
  phone       VARCHAR(20)  NULL,
  email       VARCHAR(254) NULL,
  position    VARCHAR(100) NULL,
  is_active   TINYINT(1)   NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS stg_requests (
  import_id           VARCHAR(50)  NULL,
  address_import_no   VARCHAR(50)  NOT NULL,
  applicant_full_name VARCHAR(200) NOT NULL,
  applicant_phone     VARCHAR(20)  NOT NULL,
  problem_description TEXT         NOT NULL,
  employee_import_id  VARCHAR(50)  NULL,
  status_id           TINYINT UNSIGNED NOT NULL,
  created_at          DATETIME     NULL
) ENGINE=InnoDB;
