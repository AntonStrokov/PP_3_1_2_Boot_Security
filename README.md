# Spring Boot Security Demo Application

## Описание проекта

Spring Boot приложение с системой аутентификации и авторизации на основе ролей. Приложение демонстрирует полный цикл работы Spring Security с использованием современных практик разработки.

## Архитектура приложения

### Технологический стек
- **Spring Boot 3.x** - основной фреймворк
- **Spring Security** - безопасность и авторизация
- **Spring Data JPA** - работа с базой данных
- **MySQL Database** - embedded база данных для разработки
- **Thymeleaf** - шаблонизатор для веб-интерфейса
- **Maven** - управление зависимостями

### Структура проекта

```
src/main/java/ru/kata/spring/boot_security/demo/
├── SpringBootSecurityDemoApplication.java     # Главный класс приложения
├── configs/                                   # Конфигурационные классы
│   ├── WebSecurityConfig.java                 # Конфигурация безопасности
│   ├── MvcConfig.java                         # MVC конфигурация
│   ├── SuccessUserHandler.java               # Обработчик успешной аутентификации
│   └── DataInitializer.java                  # Инициализация тестовых данных
├── controller/                                # Контроллеры
│   ├── AdminController.java                   # Административный функционал
│   └── UserController.java                   # Пользовательский функционал
├── service/                                   # Сервисный слой
│   ├── UserService.java                      # Интерфейс сервиса пользователей
│   ├── RoleService.java                      # Интерфейс сервиса ролей
│   └── impl/                                 # Реализации сервисов
│       ├── UserServiceImpl.java
│       └── RoleServiceImpl.java
├── dao/                                      # Data Access Object слой
│   ├── UserDao.java                          # Интерфейс DAO пользователей
│   ├── RoleDao.java                          # Интерфейс DAO ролей
│   └── impl/                                 # Реализации DAO
│       ├── UserDaoImpl.java
│       └── RoleDaoImpl.java
├── model/                                    # Модели данных
│   ├── User.java                            # Сущность пользователя
│   └── Role.java                            # Сущность роли
├── aspect/                                   # Аспекты
│   └── LoggingAspect.java                   # Логирование методов
└── exception/                                # Обработка исключений
    └── GlobalExceptionHandler.java          # Глобальный обработчик
```

## Функциональные возможности

### ✅ Аутентификация
- Регистрация новых пользователей
- Вход в систему по email и паролю
- Безопасное хранение паролей (хеширование)

### ✅ Авторизация
- Ролевая модель доступа (ROLE_ADMIN, ROLE_USER)
- Защита эндпоинтов на основе ролей
- Динамическое управление правами доступа

### ✅ Управление пользователями
- Просмотр списка всех пользователей (для администраторов)
- Редактирование данных пользователей
- Управление ролями пользователей
- Удаление пользователей

### ✅ Административные функции
- Полный доступ ко всем пользователям
- Назначение и изменение ролей
- Просмотр системной информации

## Как запустить приложение

### Предварительные требования
- Java 17 или выше
- Maven 3.6 или выше

### Шаги запуска

1. **Клонирование репозитория**
   ```bash
   git clone https://github.com/AntonStrokov/2.3.1-Spring-MVC-Hibernate/tree/spring-boot-version
   cd PP_3_1_2_Boot_Security
   ```

2. **Сборка проекта**
   ```bash
   mvn clean install
   ```

3. **Запуск приложения**
   ```bash
   mvn spring-boot:run
   ```

4. **Доступ к приложению**
   - Приложение будет доступно по адресу: `http://localhost:8080`
   - База данных H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:testdb`
     - User Name: `sa`
     - Password: (пусто)

## Тестовые данные

При запуске приложения автоматически создаются тестовые пользователи:

### Пользователи по умолчанию
- **Администратор**
  - Email: `admin@example.com`
  - Пароль: `admin`
  - Роли: `ROLE_ADMIN`, `ROLE_USER`

- **Обычный пользователь**
  - Email: `user@example.com`
  - Пароль: `user`
  - Роли: `ROLE_USER`

## API Endpoints

### Публичные endpoints
- `GET /` - главная страница
- `GET /login` - страница входа
- `POST /login` - обработка входа
- `GET /registration` - страница регистрации
- `POST /registration` - обработка регистрации

### Защищенные endpoints
- `GET /user` - личный кабинет пользователя
- `GET /admin` - панель администратора
- `GET /admin/users` - список всех пользователей
- `POST /admin/users/{id}` - редактирование пользователя
- `DELETE /admin/users/{id}` - удаление пользователя

## Настройки безопасности

### Ролевая модель
- **ROLE_ADMIN** - полный доступ ко всем функциям
- **ROLE_USER** - доступ только к личному кабинету

### Конфигурация безопасности
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/", "/login", "/registration").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successUserHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            );
        return http.build();
    }
}
```

## База данных

### Схема данных
```sql
-- Таблица пользователей
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255)
);

-- Таблица ролей
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Связь многие-ко-многим пользователи-роли
CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

## Разработка

### Добавление новой функциональности

1. **Новая сущность**
   - Создать модель в пакете `model/`
   - Создать DAO интерфейс и реализацию
   - Создать сервисный интерфейс и реализацию
   - Добавить контроллер

2. **Новый endpoint**
   - Добавить метод в существующий контроллер
   - или создать новый контроллер
   - Настроить безопасность в `WebSecurityConfig`

3. **Новая роль**
   - Добавить запись в таблицу `roles`
   - Настроить доступ в конфигурации безопасности

## Troubleshooting

### Частые проблемы и решения

1. **Ошибка доступа к базе данных**
   - Проверить настройки в `application.properties`
   - Убедиться, что H2 Console включена

2. **Ошибка аутентификации**
   - Проверить правильность email и пароля
   - Убедиться, что пользователь существует в базе

3. **Ошибка авторизации**
   - Проверить назначенные роли пользователю
   - Убедиться, что endpoint правильно настроен в SecurityConfig

## Лицензия

Этот проект создан для образовательных целей в рамках изучения Spring Security.

---

*Последнее обновление: 08.03.2026