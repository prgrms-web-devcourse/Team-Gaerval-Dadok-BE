SET FOREIGN_KEY_CHECKS = 0;

truncate table bookshelf_item;
alter table bookshelf_item auto_increment = 1;

truncate table bookshelves;
alter table bookshelves auto_increment = 1;

truncate table group_member;
alter table group_member auto_increment = 1;

truncate table book_groups;
alter table book_groups auto_increment = 1;

truncate table books;
alter table books auto_increment = 1;

truncate table user_authorities;
alter table user_authorities auto_increment = 1;

truncate table authorities;

truncate table users;
alter table users auto_increment = 1;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO authorities(name, created_at, modified_at)
VALUES ('USER', now(), null),
       ('ADMIN', now(), null);

insert
into books
(id, api_provider, author, contents, image_key, image_url, is_deleted, isbn, publisher, title, url)
values (1, 'KAKAO', '기시미 이치로, 고가 후미타케', '인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다고 말한 철학자가 있다.', NULL,
        'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038',
        false, '9788996991342', '인플루엔셜', '미움받을 용기',
        'https://search.daum.net/search?w=bookpage&bookId=1467038&q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0');


insert into users(id, created_at, modified_at, auth_id, auth_provider, birthday, email, gender, name, nickname, profile_image, job_id, oauth_nickname)
values (1000, now(), now(), 'abcdfgh123', 'NAVER', NULL,
        'testEmail0@naver.com', 'NONE', NULL, '책장왕다독이0', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1'),
       (1001, now(), now(), 'abcdfgh1234', 'NAVER', NULL,
        'testEmail1@naver.com', 'NONE', NULL, '책장왕다독이1', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1'),
       (1002, now(), now(), 'abcdfgh1235', 'NAVER', NULL,
        'testEmail2@naver.com', 'NONE', NULL, '책장왕다독이2', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1'),
       (1003, now(), now(), 'abcdfgh1236', 'NAVER', NULL,
        'testEmail3@naver.com', 'NONE', NULL, '책장왕다독이3', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1'),
       (1004, now(), now(), 'abcdfgh1237', 'NAVER', NULL,
        'testEmail4@naver.com', 'NONE', NULL, '책장왕다독이4', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1'),
       (1005, now(), now(), 'abcdfgh1238', 'NAVER', NULL,
        'testEmail5@naver.com', 'NONE', NULL, '책장왕다독이5', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1'),
       (1006, now(), now(), 'abcdfgh1239', 'NAVER', NULL,
        'testEmail6@naver.com', 'NONE', NULL, '책장왕다독이6', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1'),
       (1007, now(), now(), 'abcdfgh1230', 'NAVER', NULL,
        'testEmail7@naver.com', 'NONE', NULL, '책장왕다독이7', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1'),
       (1008, now(), now(), 'abcdfgh1230', 'NAVER', NULL,
        'testEmail8@naver.com', 'NONE', NULL, '책장왕다독이8', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1'),
       (1009, now(), now(), 'abcdfgh1231', 'NAVER', NULL,
        'testEmail9@naver.com', 'NONE', NULL, '책장왕다독이9', 'https://image.dadokcdn.dadok.io/abcdfgpe.jpg', 1, 'oauth1');


insert into user_authorities(id, created_at, modified_at, authority, user_id)
values (1000, now(), now(), 'USER', 1000),
       (1001, now(), now(), 'USER', 1001),
       (1002, now(), now(), 'USER', 1002),
       (1003, now(), now(), 'USER', 1003),
       (1004, now(), now(), 'USER', 1004),
       (1005, now(), now(), 'USER', 1005),
       (1006, now(), now(), 'USER', 1006),
       (1007, now(), now(), 'USER', 1007),
       (1008, now(), now(), 'USER', 1008),
       (1009, now(), now(), 'USER', 1009);

insert
into bookshelves
    (id, created_at, modified_at, is_public, job_id, name, user_id)
values (1000, now(), now(), true, NULL, '책장왕다독이님의 책장', 1000),
       (1001, now(), now(), true, NULL, '책장왕다독이님의 책장', 1001),
       (1002, now(), now(), true, NULL, '책장왕다독이님의 책장', 1002),
       (1003, now(), now(), true, NULL, '책장왕다독이님의 책장', 1003),
       (1004, now(), now(), true, NULL, '책장왕다독이님의 책장', 1004),
       (1005, now(), now(), true, NULL, '책장왕다독이님의 책장', 1005),
       (1006, now(), now(), true, NULL, '책장왕다독이님의 책장', 1006),
       (1007, now(), now(), true, NULL, '책장왕다독이님의 책장', 1007),
       (1008, now(), now(), true, NULL, '책장왕다독이님의 책장', 1008),
       (1009, now(), now(), true, NULL, '책장왕다독이님의 책장', 1009);


