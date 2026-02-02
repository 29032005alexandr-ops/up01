# UP01 — JavaFX + MySQL (подсистема заявок)

## Что это
Десктоп-приложение на JavaFX для проверки требований преподавателя:
- просмотр адресов и сотрудников
- CRUD заявок (добавить/редактировать/удалить) с привязкой к адресу/исполнителю/статусу
- история выполнения заявок + фильтрация по сотруднику и адресу
- валидация и предупреждения (удаление необратимо)

## Требования
- Java 17+
- Maven
- MySQL 8 (локально или Docker)
- База: схема `up01_uk` уже создана вашими SQL-скриптами

## Настройка подключения к БД
Файл: `src/main/resources/db.properties`

По умолчанию:
url=jdbc:mysql://127.0.0.1:3306/up01_uk?... 
user=root
password=root

Поставьте свой пароль/пользователя.

## Запуск
В терминале из папки `app`:
mvn clean javafx:run

## Если MySQL в Docker
Можно поднять так (пример):
docker run --name up01-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=up01_uk -p 3306:3306 -d mysql:8.0

Затем импортировать ваш SQL-дамп:
docker exec -i up01-mysql mysql -uroot -proot up01_uk < ../db/your_dump.sql

## Важно про схему
Код ожидает таблицы:
- address(id, address_line, city, street, house)
- employee(id, full_name, phone, email, position) (есть fallback на last_name/first_name/patronymic)
- request_status(id, name)
- service_request(id, import_id, address_id, employee_id, status_id, applicant_full_name, applicant_phone, problem_description, created_at, updated_at)
- request_history(id, request_id, employee_id, status_id, changed_at, comment)

Если в вашей схеме названия столбцов отличаются — скажите, я дам точечный патч под ваши имена.
