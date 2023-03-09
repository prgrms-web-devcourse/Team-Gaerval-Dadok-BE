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

truncate table bookshelf_item;
alter table bookshelf_item auto_increment = 1;

truncate table jobs;
alter table jobs auto_increment = 1;

SET FOREIGN_KEY_CHECKS = 1;
