USE up01_uk;

ALTER TABLE address
  ADD COLUMN import_id VARCHAR(50) NULL,
  ADD UNIQUE KEY uq_address_import_id (import_id);
