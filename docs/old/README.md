# Use-case'ы
![user](./user-use-cases.png)
![admin](./admin-use-cases.png)

# Интерфейс

## Скрин
![interface](./intpic.png)

## Диаграмма
![interface-diag](./interface-diagram.png)

# Диаграмма модели предметной области
![po-diag](./po-diag.png)

# Процессы 
## 1. Процесс авторизации
```mermaid
sequenceDiagram
    participant User as Пользователь
    participant Frontend as Frontend
    participant Backend as Backend
    participant Database as База данных

    User->>Frontend: Ввод данных для входа
    Frontend->>Backend: Запрос на аутентификацию (передача данных)
    Backend->>Database: Проверка введенных данных
    Database-->>Backend: Результат проверки
    Backend->>Backend: Формирование JWT токена
    Backend->>Frontend: JWT токен
    Frontend->>User: Отправка токена
```

## 2. Процесс взаимодействия для управления пользователями
```mermaid
sequenceDiagram
    participant Admin as Администратор
    participant Frontend as Frontend
    participant Backend as Backend
    participant Database as База данных

    Admin->>Frontend: Создание/редактирование <br> пользователя/групп
    Frontend->>Backend: Запрос на создание/редактирование <br> пользователя/групп
    Backend->>Database: Обновление данных пользователя
    Database-->>Backend: Результат
    Backend->>Frontend: Подтверждение изменений
    Frontend->>Admin: Отображение обновлений
```

## 3. Процесс взаимодействия для управления пользователями на кластере
```mermaid
sequenceDiagram
    participant Admin as Администратор
    participant Frontend as Frontend
    participant Backend as Backend
    participant Database as База данных
    participant Slurmdbd as SlurmDBD
    participant Cluster as Кластер

    Admin->>Frontend: Управление доступа<br> пользователю/группе
    Frontend->>Backend: Запрос на управление <br> доступа к кластеру пользователям
    alt Добавление
        Backend->>Backend: Генерация информации о профилях
        Backend->>Cluster: Создание профилей на ОС через <br> SSH-соединение
        Backend->>Slurmdbd: Создание профилей на <br> базе данных SlurmDBD
        Cluster->>Cluster: Создание профилей на кластере
        Cluster->>Backend: Подтверждение создания
        Slurmdbd->>Backend: Подтверждение создания
        Backend->>Database: Сохранение информации о новых профилях
        Database->>Backend: Подтверждение сохранения
    else Изменение/Удаление
        Backend->>Database: Получение изменяемых профилей
        Backend->>Cluster: Изменение/удаление профилей на ОС <br> через открытое SSH-соединение
        Backend->>Slurmdbd: Изменение/удаление профилей на <br> базе данных SlurmDBD
        Cluster->>Backend: Подтверждение операций
        Slurmdbd->>Backend: Подтверждение операций
        Backend->>Database: Изменение/удаление профилей
    end
    Backend->>Frontend: Передача изменений
    Frontend->>Admin: Отображение изменений
```


## 4. Управление задачами
```mermaid
sequenceDiagram
    participant User as Пользователь
    participant Frontend as Frontend
    participant Backend as Backend
    participant Database as База данных
    participant Cluster as Кластер
    participant Slurmdbd as SlurmDBD

    User->>Frontend: Выбор опции запуска задачи
    Frontend->>User: Вывод формы с параметрами задачи
    User->>Frontend: Ввод параметров задачи
    Frontend->>Backend: Передача параметров задачи
    Backend->>Database: Получение профиля пользователя <br> на узле кластера
    Database->>Backend: Передача профиля пользователя <br> на узле кластера
    Backend->>Slurmdbd: Получение JWT-токена <br> для работы с REST API через SSH
    Slurmdbd->>Backend: Отправка JWT-токена через SSH
    Backend->>Cluster: Отправка параметров задачи <br> через REST API
    Cluster->>Backend: Подтверждение добавления задачи
    Cluster->>Slurmdbd: Добавление данных о задачи
    Backend->>Slurmdbd: Получение задачи через REST API
    Slurmdbd->>Backend: Отправка данных о задаче
    Backend->>Frontend: Отправка данных о задаче
    Frontend->>User: Отображение задачи
```

## 5. Загрузка директории пользователя
```mermaid
sequenceDiagram
    participant User as Пользователь
    participant Frontend as Frontend
    participant Backend as Backend
    participant Cluster as Кластер

    User->>Frontend: Инициирует скачивание директории
    Frontend->>Backend: Запрос на скачивание директории
    Backend->>Cluster: Установка SFTP-соединения
    Backend->>Cluster: Рекурсивное считывание файлов и директорий
    Cluster-->>Backend: Поток данных (файлы)
    Backend->>Backend: Архивирование в ZIP на лету
    Backend-->>Frontend: Отправка ZIP-файла
    Frontend-->>User: Скачивание ZIP-файла
```

## 6. Взаимодействие для мониторинга
```mermaid
sequenceDiagram
    participant Admin as Администратор
    participant Frontend as Frontend
    participant Backend as Backend
    participant Slurmdbd as Slurmdbd
    participant Cluster as Кластер

    Admin->>Frontend: Выбор опции отображения статистики
    Frontend->>Backend: Запрос на соединение через WebSocket
    Backend->>Frontend: Открытие WebSocket-соединения
    Backend->>Slurmdbd: Получение JWT-токена через SSH
    Slurmdbd->>Backend: Отправка JWT-токена через SSH
    Backend->>Cluster: Запрос данных о загрузке узлов (CPU, память и т.д.)<br> и статистике по задачам через REST API
    Cluster-->>Backend: Данные о загрузке
    Backend->>Frontend: Отправка данных через WebSocket
    Frontend->>Admin: Отображение данных для мониторинга
```
## 7. Взаимодействие для терминала
```mermaid
sequenceDiagram
    participant User as Пользователь
    participant Frontend as Frontend
    participant Backend as Backend
    participant Cluster as Кластер

    User->>Frontend: Выбор опции терминала
    Frontend->>Backend: Запрос на соединение <br> с терминалом через WebSocket
    Backend->>Backend: Создание локального <br>pseudo-terminal (pty) сессии
    Backend->>Cluster: Установка SSH-соединения
    Cluster->>Backend: Подтверждение успешного SSH-соединения
    Backend->>Frontend: Подтверждение WebSocket-сессии
    Frontend->>User: Отображение терминала
    loop Сессия терминала
        User->>Frontend: Ввод команды
        Frontend->>Backend: Отправка команды через WebSocket
        Backend->>Backend: Проброс команды в PTY-сессию
        Backend->>Cluster: Передача команды через SSH
        Cluster-->>Backend: Результат выполнения
        Backend-->>Backend: Получение вывода
        Backend->>Frontend: Передача вывода через WebSocket
        Frontend->>User: Отображение результата
    end
```