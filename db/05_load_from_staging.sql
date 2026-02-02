USE up01_uk;

-- 1) Addresses: create unique addresses from staging
-- Assumption: address.address_line exists OR you have street/house fields.
-- If in your schema address is split, replace address_line with your actual columns.
INSERT INTO address (address_line)
SELECT DISTINCT TRIM(address)
FROM stg_housing_fund
WHERE address IS NOT NULL AND TRIM(address) <> ''
ON DUPLICATE KEY UPDATE address_line = VALUES(address_line);

-- 2) Employees
INSERT INTO employee (last_name, first_name, patronymic, phone, email, position, is_active)
SELECT
  TRIM(last_name),
  TRIM(first_name),
  NULLIF(TRIM(patronymic), ''),
  NULLIF(TRIM(phone), ''),
  NULLIF(TRIM(email), ''),
  NULLIF(TRIM(position), ''),
  COALESCE(is_active, 1)
FROM stg_employees
ON DUPLICATE KEY UPDATE
  phone = VALUES(phone),
  email = VALUES(email),
  position = VALUES(position),
  is_active = VALUES(is_active);

-- 3) Requests (link by address text and optional employee import id)
-- If you don't have employee matching by import_id in employee table, we link by name/phone or leave NULL.
INSERT INTO service_request (
  address_id,
  applicant_full_name,
  applicant_phone,
  problem_description,
  employee_id,
  status_id,
  created_at
)
SELECT
  a.id AS address_id,
  r.applicant_full_name,
  r.applicant_phone,
  r.problem_description,
  e.id AS employee_id,
  r.status_id,
  COALESCE(r.created_at, NOW())
FROM stg_requests r
JOIN address a
  ON a.address_line = TRIM(r.address_import_no)  -- IMPORTANT: see note below
LEFT JOIN employee e
  ON e.id IS NOT NULL
;
