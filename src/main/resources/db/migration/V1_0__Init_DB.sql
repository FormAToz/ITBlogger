create table captcha_codes (
    id integer not null auto_increment,
    code TINYTEXT not null,
    secret_code TINYTEXT,
    time datetime(6) not null,
    primary key (id)
    ) engine=InnoDB;

create table global_settings (
    id integer not null auto_increment,
    code varchar(255) not null,
    name varchar(255) not null,
    value varchar(255) not null,
    primary key (id)
    ) engine=InnoDB;

create table post_comments (
    id integer not null auto_increment,
    parent_id integer,
    post_id integer not null,
    text TEXT not null,
    time datetime(6) not null,
    user_id integer not null,
    primary key (id)
    ) engine=InnoDB;

create table post_votes (
    id integer not null auto_increment,
    post_id integer not null,
    time datetime(6) not null,
    user_id integer not null,
    value tinyint not null,
    primary key (id)
    ) engine=InnoDB;

create table posts (
    id integer not null auto_increment,
    is_active tinyint not null,
    moderation_status VARCHAR(32) default 'NEW' not null,
    moderator_id integer,
    text TEXT not null,
    time datetime(6) not null,
    title varchar(255) not null,
    user_id integer not null,
    view_count integer not null,
    primary key (id)
    ) engine=InnoDB;

create table tag2post (
    id integer not null auto_increment,
    post_id integer not null,
    tag_id integer not null,
    primary key (id)
    ) engine=InnoDB;

create table tags (
    id integer not null auto_increment,
    name varchar(255) not null,
    primary key (id)
    ) engine=InnoDB;

create table users (
    id integer not null auto_increment,
    code varchar(255),
    email varchar(255) not null,
    is_moderator tinyint not null,
    name varchar(255) not null,
    password varchar(255) not null,
    photo TEXT, reg_time datetime(6) not null,
    primary key (id)
    ) engine=InnoDB;
