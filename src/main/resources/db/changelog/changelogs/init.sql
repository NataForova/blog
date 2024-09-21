create table author (
    id bigint not null primary key,
    name varchar(255),
    email varchar(255) UNIQUE,
    registration_date timestamp
);
create sequence author_sequence
    increment by 1
    start with 3;

insert into author (id, name, email, registration_date)
values (1, 'author1', 'author1@gmail.com', '2024-09-21 11:30:00');
insert into author (id, name, email, registration_date)
values (2, 'author2', 'author2@gmail.com', '2024-09-21 11:30:00');


create table article (
    id bigint not null primary key,
    title varchar(255),
    content text,
    author_id bigint,
    created_at timestamp,
    updated_at timestamp,
    foreign key (author_id) references author(id)
);

create sequence article_sequence
    increment by 1
    start with 3;

insert into article (id, title, content, author_id, created_at, updated_at)
values
    (1, 'First Article', 'This is the content of the first article.', 1, '2024-09-21 12:00:00', '2024-09-21 12:00:00');

insert into article (id, title, content, author_id, created_at, updated_at)
values
    (2, 'First Article', 'This is the content of the second article.', 2, '2024-09-21 12:00:00', '2024-09-21 12:00:00');