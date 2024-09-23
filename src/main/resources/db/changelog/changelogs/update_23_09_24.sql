alter table article add column "is_deleted" boolean default false;
create index idx_article_title_author on article (title, user_id);
