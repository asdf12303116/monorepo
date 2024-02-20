INSERT INTO public.t_user_role (id, create_time, create_user_id, deleted, update_time, update_user_id, role_id, user_id, role_code, role_name) VALUES (1, null, null, null, null, null, 10000, 10000, 'admin', '管理员');
INSERT INTO public.t_user (id, create_time, create_user_id, deleted, update_time, update_user_id, expired, locked, credentials_non_expired, enabled, nick_name, password, username) VALUES (10000, null, null, false, null, null, false, false, true, true, null, '$2a$10$hDzOP6BceG7HJFl5EPYuGeLBJPUsw/s7WdZD4yf//7QmuVQ9XJqpm', 'admin');
INSERT INTO public.t_role (id, create_time, create_user_id, deleted, update_time, update_user_id, role_code, role_desc, role_name) VALUES (10000, null, null, false, null, null, 'admin', '默认管理员账号', '管理员');
INSERT INTO public.t_role (id, create_time, create_user_id, deleted, update_time, update_user_id, role_code, role_desc,
                           role_name)
VALUES (20000, null, null, false, null, null, 'user', '默认普通账号', '普通用户');

INSERT INTO public.t_oauth_role_map(id, create_time, create_user_id, deleted, update_time, update_user_id,
                                    create_user_name, update_user_name, group_uuid, group_name, role_id, group_desc)
VALUES (10000, null, null, false, null, null, null, null, 'ff51b3b1-fb94-4886-a4f5-e9c59159ec68', 'admin', 10000, null);

INSERT INTO public.t_oauth_role_map(id, create_time, create_user_id, deleted, update_time, update_user_id,
                                    create_user_name, update_user_name, group_uuid, group_name, role_id, group_desc)
VALUES (20000, null, null, false, null, null, null, null, '5786aa9f-8f77-4776-b982-f71268f48879', 'user', 20000, null);


INSERT INTO public.t_book_info (id, create_time, create_user_id, deleted, update_time, update_user_id, create_user_name, update_user_name, book_name, author, publication_date, isbn, description) VALUES (6, null, 10000, false, null, null, 'system', null, 'Book 6', '作者六', '2022-02-03', 'ISBN-333333333', 'Description for Book 6');
INSERT INTO public.t_book_info (id, create_time, create_user_id, deleted, update_time, update_user_id, create_user_name, update_user_name, book_name, author, publication_date, isbn, description) VALUES (2, null, 10000, false, null, null, 'system', null, 'Book 2', '作者二', '2022-01-01', 'ISBN-987654321', 'Description for Book 2');
INSERT INTO public.t_book_info (id, create_time, create_user_id, deleted, update_time, update_user_id, create_user_name, update_user_name, book_name, author, publication_date, isbn, description) VALUES (5, null, 10000, false, null, null, 'system', null, '图书五', 'Author 5', '2022-02-02', 'ISBN-222222222', '图书五的描述');
INSERT INTO public.t_book_info (id, create_time, create_user_id, deleted, update_time, update_user_id, create_user_name, update_user_name, book_name, author, publication_date, isbn, description) VALUES (4, null, 10000, false, null, null, 'system', null, 'Book 4', '作者四', '2022-02-01', 'ISBN-111111111', 'Description for Book 4');
INSERT INTO public.t_book_info (id, create_time, create_user_id, deleted, update_time, update_user_id, create_user_name, update_user_name, book_name, author, publication_date, isbn, description) VALUES (1, null, 10000, false, null, null, 'system', null, '图书一', 'Author 1', '2021-12-01', 'ISBN-123456789', '图书一的描述');
INSERT INTO public.t_book_info (id, create_time, create_user_id, deleted, update_time, update_user_id, create_user_name, update_user_name, book_name, author, publication_date, isbn, description) VALUES (3, null, 10000, false, null, null, 'system', null, '图书三', 'Author 3', '2022-02-01', 'ISBN-456789123', '图书三的描述');
