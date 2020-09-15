# Схема базы данных
## Таблица ```users``` - пользователи
| name         | properties                  | description                                                                                             |
|--------------|-----------------------------|---------------------------------------------------------------------------------------------------------|
| id           | INT NOT NULL AUTO_INCREMENT | id пользователя                                                                                         |
| is_moderator | TINYINT NOT NULL            | является ли пользователь модератором (может ли править глобальные настройки сайта и модерировать посты) |
| reg_time     | DATETIME NOT NULL           | дата и время регистрации пользователя                                                                   |
| name         | VARCHAR(255) NOT NULL       | имя пользователя                                                                                        |
| email        | VARCHAR(255) NOT NULL       | e-mail пользователя                                                                                     |
| password     | VARCHAR(255) NOT NULL       | хэш пароля пользователя                                                                                 |
| code         | VARCHAR(255)                | код для восстановления пароля, может быть NULL                                                          |
| photo        | TEXT                        | фотография (ссылка на файл), может быть NULL                                                            |

## Таблица ```posts``` - посты
| name              | properties                                   | description                                              |
|-------------------|----------------------------------------------|----------------------------------------------------------|
| id                | INT NOT NULL AUTO_INCREMENT                  | id поста                                                 |
| is_active         | TINYINT NOT NULL                             | скрыта или активна публикация: 0 или 1                   |
| moderation_status | ENUM("NEW", "ACCEPTED", "DECLINED") NOT NULL | статус модерации, по умолчанию значение "NEW".           |
| moderator_id      | INT                                          | ID пользователя-модератора, принявшего решение, или NULL |
| user_id           | INT NOT NULL                                 | автор поста                                              |
| timestamp              | DATETIME NOT NULL                            | дата и время публикации поста                            |
| title             | VARCHAR(255) NOT NULL                        | заголовок поста                                          |
| text              | TEXT NOT NULL                                | текст поста                                              |
| view_count        | INT NOT NULL                                 | количество просмотров поста                              |

## Таблица ```post_votes``` - лайки и дизлайки постов
| name    | properties                  | description                             |
|---------|-----------------------------|-----------------------------------------|
| id      | INT NOT NULL AUTO_INCREMENT | id лайка/дизлайка                       |
| user_id | INT NOT NULL                | тот, кто поставил лайк / дизлайк        |
| post_id | INT NOT NULL                | пост, которому поставлен лайк / дизлайк |
| timestamp    | DATETIME NOT NULL           | дата и время лайка / дизлайка           |
| value   | TINYINT NOT NULL            | лайк или дизлайк: 1 или -1              |

## Таблица ```tags``` - тэги
| name | properties                  | description |
|------|-----------------------------|-------------|
| id   | INT NOT NULL AUTO_INCREMENT | id тэга     |
| name | VARCHAR(255) NOT NULL       | текст тэга  |

## Таблица ```tag2post``` - связи тэгов с постами
|name      |properties                    |description  |
|----------|------------------------------|-------------|
| id       | INT NOT NULL AUTO_INCREMENT  | id связи    |
| post_id  | INT NOT NULL                 | id поста    |
| tag_id   | INT NOT NULL                 | id тэга     |

## Таблица ```post_comments``` - комментарии к постам
| name      | properties                  | description                                                                                                   |
|-----------|-----------------------------|---------------------------------------------------------------------------------------------------------------|
| id        | INT NOT NULL AUTO_INCREMENT | id комментария                                                                                                |
| parent_id | INT                         | комментарий, на который оставлен этот комментарий (может быть NULL, если комментарий оставлен просто к посту) |
| post_id   | INT NOT NULL                | пост, к которому написан комментарий                                                                          |
| user_id   | INT NOT NULL                | автор комментария                                                                                             |
| timestamp      | DATETIME NOT NULL           | дата и время комментария                                                                                      |
| text      | TEXT NOT NULL               | текст комментария                                                                                             |

## Таблица ```captcha_codes``` - коды капч
|name          |properties                    |description                            |
|--------------|------------------------------|---------------------------------------|
| id           | INT NOT NULL AUTO_INCREMENT  | id каптча                             |
| timestamp         | DATETIME NOT NULL            | дата и время генерации кода капчи     |
| code         | TINYTEXT NOT NULL            | код, отображаемый на картинкке капчи  |
| secret_code  | TINYTEXT NOT NULL            | код, передаваемый в параметре         |

## Таблица ```global_settings``` - глобальные настройки движка
|name    |properties                    |description               |
|--------|------------------------------|--------------------------|
| id     | INT NOT NULL AUTO_INCREMENT  | id настройки             |
| code   | VARCHAR(255) NOT NULL        | системное имя настройки  |
| name   | VARCHAR(255) NOT NULL        | название настройки       |
| value  | VARCHAR(255) NOT NULL        | значение настройки       |

# Значения глобальных настроек
| code                 | name                             | value    |
|:---------------------|:---------------------------------|:---------|
| MULTIUSER_MODE       | Многопользовательский режим      | YES / NO |
| POST_PREMODERATION   | Премодерация постов              | YES / NO |
| STATISTICS_IS_PUBLIC | Показывать всем статистику блога | YES / NO |