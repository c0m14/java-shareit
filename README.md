# java-shareit

## summary
Мой третий проект в рамках обучения Java на платформе Яндекс Практикум.  
Основная идея проекта - создать бэкенд для так называемого "шеринг-сервиса".  
В сервиса одни пользователи могут размещать информацию о вещах, которые они готовы отдать в аренду, а другие -   
арендовать вещи или размещать запросы на аренду, если нужно вещи еще нет.  

Проект реализован как многомодульный - отдельно вынесенен шлюз (gateway), осуществляющий первичную валидацию запросов и   
проксирующий их в сторону основного сервиса (server)

### my other edu projects
- [(1) Kanban (Трекер задач а-ля Jira)](https://github.com/c0m14/java-kanban/blob/main/README.md)
- [(2) Filmorate (Сервис для оценки и рекомендации фильмов)](https://github.com/c0m14/java-filmorate/blob/main/README.md)
- [(4) Explore-with-me (Агрегатор мероприятий а-ля Афиша, дипломный проект)](https://github.com/c0m14/graduate_java-explore-with-me/blob/main/README.md)

### stack

- Java 11
- Spring Boot
- JPA (Hibernate 5.6)
- PosgreSQL (and H2 for CI and test)
- RestTemplate / WebClient (separate client realizations)
- Docker
- Lombok

## main educational purposes
- Освоить работу с JPA и ORM (Hibernate)
- Использование альтернативной структуры проекта (feature layout)
- Расширение навыков покрытия проекта тестами (покрытие проекта ~90%)
  - Unit-тесты с использованием mock-ов
  - Тестовые слайсы (WebMvcTest, DataJpaTest, JsonTest)
  - Интеграционые тесты
- Работа с docker, настройка и запуск многомодульного проекта с помощью docker-compose
- Написание web-клиента для взаимодействия шлюза с сервисом, сделана реализация отдельно с использованием двух   
технологий:
  - RestTemplate
  - WebClient

