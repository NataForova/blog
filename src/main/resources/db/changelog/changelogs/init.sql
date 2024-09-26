create table users (
    id bigint not null primary key,
    name varchar(255),
    email varchar(255) UNIQUE,
    password varchar(255),
    registration_date timestamp
);
create sequence user_sequence
    increment by 1
    start with 1;

create table article (
    id bigint not null primary key,
    title varchar(255),
    content text,
    user_id bigint,
    created_at timestamp,
    updated_at timestamp,
    foreign key (user_id) references users(id)
);

create sequence article_sequence
    increment by 1
    start with 1;

create table roles (
    id bigint not null primary key,
    name varchar(255)
);

insert into roles (id, name) values (1, 'ROLE_AUTHOR');
insert into roles (id, name) values (2, 'ROLE_GUEST');

create table users_roles (
    user_id bigint,
    role_id bigint,
    foreign key (user_id) references users(id),
    foreign key (role_id) references roles(id),
    primary key (user_id, role_id)
);