-- UP.01 / seed справочников
SET NAMES utf8mb4;
USE up01_uk;

-- Статусы из описания предметной области: «Открыта заявка», «Заявка в работе», «Заявка закрыта»
INSERT INTO request_status (id, name, sort_order)
VALUES
  (1, 'Открыта заявка', 1),
  (2, 'Заявка в работе', 2),
  (3, 'Заявка закрыта', 3)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  sort_order = VALUES(sort_order);
