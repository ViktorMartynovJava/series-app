# 🎬 SeriesOnBlackDay — Сайт рекомендаций сериалов

Spring Boot приложение с каталогом сериалов, видеоплеером и комментариями.

## Стек технологий

| Слой     | Технология                                                              |
|----------|-------------------------------------------------------------------------|
| Backend  | Spring Boot 3.2, Spring MVC, Grafana + Prometheus, Docker, CI CD GitLab | 
| Security | Spring Security 6 (form-login, BCrypt)                                  |
| Database | PostgreSQL + Spring Data JPA                                            |
| Frontend | Thymeleaf + Vanilla JS                                                  |
| Build    | Maven                                                                   |

## Структура проекта

```
src/main/java/com/seriesapp/
├── config/
│   ├── SecurityConfig.java       # Конфигурация Spring Security
│   └── DataInitializer.java      # Тестовые данные
├── controller/
│   ├── AuthController.java       # Регистрация / вход
│   ├── SeriesController.java     # Каталог, детали, комментарии
│   └── AdminController.java      # Управление сериалами
├── entity/
│   ├── User.java                 # Пользователь (implements UserDetails)
│   ├── Series.java               # Сериал
│   └── Comment.java              # Комментарий с оценкой
├── repository/                   # JPA репозитории
├── service/
│   ├── UserService.java          # Регистрация, избранное
│   ├── SeriesService.java        # CRUD сериалов, фильтрация
│   ├── CommentService.java       # Комментарии
│   └── UserDetailsServiceImpl.java
└── dto/                          # RegisterDto, CommentDto, SeriesDto

src/main/resources/
├── templates/
│   ├── series/home.html          # Главная страница
│   ├── series/catalog.html       # Каталог с поиском
│   ├── series/detail.html        # Страница сериала + плеер
│   ├── auth/login.html
│   ├── auth/register.html
│   └── admin/dashboard.html      # Админ панель
└── static/css/main.css           # Тёмный кинематографический стиль
```


## Функционал

### Для гостей
- Просмотр главной страницы с топ сериалами
- Поиск и фильтрация по жанру в каталоге
- Просмотр карточек и описаний сериалов
- Просмотр трейлеров и видео (без плеера только гостям)
- Чтение комментариев

### Для зарегистрированных
- Всё что выше
- Добавление/удаление из избранного
- Оставление комментариев с оценкой (1–10)
- Удаление своих комментариев

### Для администратора
- Панель управления `/admin`
- Добавление, редактирование, удаление сериалов
- Удаление любых комментариев
- Поддержка YouTube и прямых ссылок для плеера

## API эндпоинты

| Метод | URL | Описание |
|-------|-----|----------|
| GET | / | Главная |
| GET | /series | Каталог (поиск, фильтр, пагинация) |
| GET | /series/{id} | Детали сериала |
| POST | /series/{id}/comment | Добавить комментарий |
| POST | /series/{id}/favorite | Добавить/убрать из избранного |
| POST | /comment/{id}/delete | Удалить комментарий |
| GET | /auth/login | Страница входа |
| GET | /auth/register | Регистрация |
| GET | /admin | Панель администратора |


