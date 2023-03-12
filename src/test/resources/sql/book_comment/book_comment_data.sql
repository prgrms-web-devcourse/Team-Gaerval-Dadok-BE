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

insert into books
(id, api_provider, author, contents, image_key, image_url, is_deleted, isbn, publisher, title, url)
values (1, 'KAKAO', '기시미 이치로, 고가 후미타케', '인간은 변할 수 있고, 누구나 행복해 질 수 있다. 단 그러기 위해서는 ‘용기’가 필요하다고 말한 철학자가 있다.', NULL,
        'https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1467038',
        false, '9788996991342', '인플루엔셜', '미움받을 용기',
        'https://search.daum.net/search?w=bookpage&bookId=1467038&q=%EB%AF%B8%EC%9B%80%EB%B0%9B%EC%9D%84+%EC%9A%A9%EA%B8%B0');

insert into book_comments(comment, book_id, user_id, created_at, modified_at)
    value ('하이룽~!', 1,1,now(),null);