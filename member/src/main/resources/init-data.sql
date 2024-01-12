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