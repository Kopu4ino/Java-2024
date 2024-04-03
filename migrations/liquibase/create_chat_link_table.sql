create table if not exists chat_link
(
    chat_id bigint references chat (id),
    link_id bigint references link (id),

    primary key (chat_id, link_id)
);
