## Тестовое задание

Проект реализует некоторое количество интеграционных тестов на заглушках для несуществующей бизнес-логики.

Задание - написать бизнес-логику таким образом, чтобы тесты перестали падать. 

Стек:
* Spring Boot 2 (cloud-contract, feign, netflix);
* JUnit 5

---
#### Бизнес-задача - обогащение CSV:

Дан csv-файл, каждая строка которого соотвествует клиентским данным. 
Необходимо по каждой сформировать запрос и отправить его на внешний endpoint. 
По результатам надо сформировать новый csv-файл, содержащий обогащенные данные.
---
#### Структура входного CSV-файла:
* FIRST_NAME,LAST_NAME,MIDDLE_NAME,CONTRACT_DATE

---
#### Структура выходного CSV-файла:
* CLIENT_NAME,CONTRACT_DATE,SCORING

CLIENT_NAME - это строка ФИО в формате "ИМЯ ОТЧЕСТВО ФАМИЛИЯ"

---
#### Структура запроса ко внешней системе:

* CLIENT_NAME - строка ФИО в формате "ИМЯ ОТЧЕСТВО ФАМИЛИЯ"
* CONTRACT_DATE - строка с датой в формате ISO_DATE

---
#### Структура ответа внешней системы:
* status - статус ответа;
  * COMPLETED - запрос обработан успешно, скоринговый балл получен (возвращается с http-кодом 200);
  * FAILED - ошибка при обработке запроса (возвращается с http-кодом 500);
  * NOT_FOUND - запрос обработан успешно, данных по клиенту не найдено (возвращается с http-кодом 200);
* description - описание возникшей проблемы при неуспешной обработке запроса;
* scoringValue - значение скорингового балла, возвращаемое при успешной обработке запроса.