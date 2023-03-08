drop table if exists book_comments cascade;

drop table if exists bookshelf_item cascade;

drop table if exists bookshelves cascade;

drop table if exists group_comments cascade;

drop table if exists group_member cascade;

drop table if exists book_groups cascade;

drop table if exists books cascade;

drop table if exists oauth2_authorized_client cascade;

drop table if exists user_authorities cascade;

drop table if exists authorities cascade;

drop table if exists users cascade;

drop table if exists jobs cascade;



create table if not exists authorities
(
    name        varchar(255) not null
        primary key,
    created_at  datetime(6)  not null,
    modified_at datetime(6)  null
);


create table if not exists  jobs
(
    id          bigint auto_increment
        primary key,
    created_at  datetime(6)       not null,
    modified_at datetime(6)       null,
    job_group   varchar(255)      null,
    job_name    varchar(255)      null,
    sort_order  smallint unsigned not null
);

create table if not exists  books
(
    id           bigint auto_increment
        primary key,
    api_provider varchar(20)   not null,
    author       varchar(255)  not null,
    contents     varchar(2000) not null,
    image_key    varchar(500)  null,
    image_url    varchar(2083) not null,
    is_deleted   bit           not null,
    isbn         varchar(20)   not null,
    publisher    varchar(30)   not null,
    title        varchar(500)  not null,
    url          varchar(2083) null,
    constraint UK_kibbepcitr0a3cpk3rfr7nihn
        unique (isbn)
);

create table if not exists  book_groups
(
    id               bigint auto_increment
        primary key,
    created_at       datetime(6)   not null,
    modified_at      datetime(6)   null,
    end_date         date          not null,
    has_join_passwd  bit           not null,
    introduce        varchar(1000) not null,
    is_public        bit           not null,
    join_passwd      varchar(64)   null,
    join_question    varchar(30)   null,
    max_member_count int           not null,
    owner_id         bigint        not null,
    start_date       date          not null,
    title            varchar(30)   not null,
    book_id          bigint        not null,
    constraint FK3dxr5ayx0ugm6gkh026cnuuwj
        foreign key (book_id) references books (id)
);

create index is_public_index
    on book_groups (is_public);

create index owner_id_index
    on book_groups (owner_id);

create table if not exists  oauth2_authorized_client
(
    client_registration_id  varchar(100)                        not null,
    principal_name          varchar(200)                        not null,
    access_token_type       varchar(100)                        not null,
    access_token_value      blob                                not null,
    access_token_issued_at  timestamp                           not null,
    access_token_expires_at timestamp                           not null,
    access_token_scopes     varchar(1000)                       null,
    refresh_token_value     blob                                null,
    refresh_token_issued_at timestamp                           null,
    created_at              timestamp default CURRENT_TIMESTAMP not null,
    primary key (client_registration_id, principal_name)
);

create table if not exists  users
(
    id             bigint auto_increment
        primary key,
    created_at     datetime(6)  not null,
    modified_at    datetime(6)  null,
    auth_id        varchar(255) not null,
    auth_provider  varchar(10)  not null,
    birthday       datetime(6)  null,
    email          varchar(255) null,
    gender         varchar(10)  null,
    name           varchar(30)  null,
    nickname       varchar(255) null,
    oauth_nickname varchar(100) null,
    profile_image  varchar(500) null,
    job_id         bigint       null,
    constraint UK_6dotkott2kjsp8vw4d0m25fb7
        unique (email),
    constraint FK4f60m7trc06r46cn56dgdnd23
        foreign key (job_id) references jobs (id)
);

create table if not exists  book_comments
(
    id      bigint auto_increment
        primary key,
    comment varchar(500) not null,
    book_id bigint       not null,
    user_id bigint       not null,
    constraint user_id_book_id_unique_key
        unique (user_id, book_id),
    constraint FK6xpmpddanwk8vq96sax69c4hu
        foreign key (book_id) references books (id),
    constraint FK8b2kkv55i033vk6nahefaqabt
        foreign key (user_id) references users (id)
);

create table if not exists  bookshelves
(
    id          bigint auto_increment
        primary key,
    created_at  datetime(6) not null,
    modified_at datetime(6) null,
    is_public   bit         not null,
    job_id      bigint      null,
    name        varchar(30) not null,
    user_id     bigint      not null,
    constraint UK_tcrodhygyxwj34g51k38cpm3v
        unique (user_id),
    constraint FKtcbycs6nu7hb8tambe8i7fk46
        foreign key (user_id) references users (id)
);

create table if not exists  bookshelf_item
(
    id           bigint auto_increment
        primary key,
    created_at   datetime(6) not null,
    modified_at  datetime(6) null,
    item_type    varchar(20) not null,
    book_id      bigint      not null,
    bookshelf_id bigint      not null,
    constraint FKciao5n3idp080b3h3xilaydsp
        foreign key (bookshelf_id) references bookshelves (id),
    constraint FKmxi0cndspoavkel5ei7i3l4pm
        foreign key (book_id) references books (id)
);

create index job_id_index
    on bookshelves (job_id);

create table if not exists  group_comments
(
    id                bigint auto_increment
        primary key,
    created_at        datetime(6)   not null,
    modified_at       datetime(6)   null,
    contents          varchar(2000) not null,
    book_group_id     bigint        null,
    parent_comment_id bigint        null,
    user_id           bigint        not null,
    constraint FK2cv3g5ochp667sjgdflxxv5bt
        foreign key (parent_comment_id) references group_comments (id),
    constraint FK7vaaa3vdpeneoa1jr8yo6733f
        foreign key (book_group_id) references book_groups (id),
    constraint FKvju9g6sf06vw0hj1tivelssr
        foreign key (user_id) references users (id)
);

create table if not exists  group_member
(
    id            bigint auto_increment
        primary key,
    created_at    datetime(6) not null,
    modified_at   datetime(6) null,
    book_group_id bigint      not null,
    user_id       bigint      not null,
    constraint FKfanwu738tcegh6u9uf7jrnn27
        foreign key (user_id) references users (id),
    constraint FKrhlb9rqt2hwukhben45kpuwfe
        foreign key (book_group_id) references book_groups (id)
);

create table if not exists  user_authorities
(
    id          bigint auto_increment
        primary key,
    created_at  datetime(6)  not null,
    modified_at datetime(6)  null,
    authority   varchar(255) not null,
    user_id     bigint       null,
    constraint userId_authority_unique_key
        unique (user_id, authority),
    constraint FKhiiib540jf74gksgb87oofni
        foreign key (user_id) references users (id),
    constraint FKmwsrmuc2q63yd3u3217guv6wg
        foreign key (authority) references authorities (name)
);

