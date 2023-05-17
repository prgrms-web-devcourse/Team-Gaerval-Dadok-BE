insert into authorities
    (created_at, modified_at, name)
values (now(), now(), 'USER');

insert into users
(id, created_at, modified_at, auth_id, auth_provider,
 birthday, email, gender, job_id, name, nickname, oauth_nickname, profile_image)
values (1, now(), now(), 'abcdfgh123', 'KAKAO', NULL, 'dadok@kakao.com', 'NONE', NULL, NULL, '다독이카카오', '책장왕다독이',
        'https://image.dadokcdn.dadok.io/abcdfgpe.jpg');

insert into users
(id, created_at, modified_at, auth_id, auth_provider,
 birthday, email, gender, job_id, name, nickname, oauth_nickname, profile_image)
values (2, now(), now(), 'abcdfgh123', 'NAVER', NULL, 'dadok1@naver.com', 'NONE', NULL, NULL, '다독이네이버', '책장왕다독이',
        'https://image.dadokcdn.dadok.io/abcdfgpe.jpg');


insert into user_authorities
    (created_at, modified_at, authority, user_id)
values (now(), now(), 'USER', 1);

insert into user_authorities
    (created_at, modified_at, authority, user_id)
values (now(), now(), 'USER', 2);

insert
into bookshelves
    (id, created_at, modified_at, is_public, job_id, name, user_id)
values (1000, now(), now(), true, NULL, '책장왕다독이님의 책장', 1),
       (1001, now(), now(), true, NULL, '책장왕다독이님의 책장', 2);

