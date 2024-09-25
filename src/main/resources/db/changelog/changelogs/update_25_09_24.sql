create table article_event (
    id bigint not null primary key,
    article_id bigint not null,
    user_id bigint not null,
    change_type varchar(255),
    changes text,
    created_at timestamp,
    is_sent boolean,
    foreign key (user_id) references users(id),
    foreign key (article_id) references article(id)

);
create sequence event_seq
    increment by 1
    start with 1;