create table public.t_oauth_role_map
(
    id               bigint primary key not null,
    create_time      timestamp(6) without time zone,
    create_user_id   bigint,
    deleted          boolean,
    update_time      timestamp(6) without time zone,
    update_user_id   bigint,
    create_user_name character varying(60),
    update_user_name character varying(60),
    group_uuid       character varying,      -- 组uuid
    group_name       character varying(255), -- 组名
    role_id          bigint,                 -- 对应角色id
    group_desc       character varying(255)  -- 组名描述
);
comment on column public.t_oauth_role_map.group_uuid is '组uuid';
comment on column public.t_oauth_role_map.group_name is '组名';
comment on column public.t_oauth_role_map.role_id is '对应角色id';
comment on column public.t_oauth_role_map.group_desc is '组名描述';

create table public.t_role
(
    id               bigint primary key not null,    -- 主键ID
    create_time      timestamp(6) without time zone, -- 创建时间
    create_user_id   bigint,                         -- 创建用户ID
    deleted          boolean,                        -- 是否逻辑删除
    update_time      timestamp(6) without time zone, -- 修改时间
    update_user_id   bigint,                         -- 修改用户ID
    create_user_name character varying(60),          -- 创建用户名
    update_user_name character varying(60),          -- 更新用户名
    role_code        character varying(255),         -- 角色编码
    role_desc        character varying(255),         -- 角色描述
    role_name        character varying(255)          -- 角色名称
);
comment on column public.t_role.id is '主键ID';
comment on column public.t_role.create_time is '创建时间';
comment on column public.t_role.create_user_id is '创建用户ID';
comment on column public.t_role.deleted is '是否逻辑删除';
comment on column public.t_role.update_time is '修改时间';
comment on column public.t_role.update_user_id is '修改用户ID';
comment on column public.t_role.create_user_name is '创建用户名';
comment on column public.t_role.update_user_name is '更新用户名';
comment on column public.t_role.role_code is '角色编码';
comment on column public.t_role.role_desc is '角色描述';
comment on column public.t_role.role_name is '角色名称';

create table public.t_user
(
    id                      bigint primary key not null,    -- 主键ID
    create_time             timestamp(6) without time zone, -- 创建时间
    create_user_id          bigint,                         -- 创建用户ID
    deleted                 boolean,                        -- 是否逻辑删除
    update_time             timestamp(6) without time zone, -- 修改时间
    update_user_id          bigint,                         -- 修改用户ID
    create_user_name        character varying(60),          -- 创建用户名
    update_user_name        character varying(60),          -- 更新用户名
    expired                 boolean,                        -- 是否过期
    locked                  boolean,                        -- 是否锁定
    credentials_non_expired boolean            not null,    -- 是否过期
    enabled                 boolean            not null,    -- 是否启用
    nick_name               character varying(60),          -- 用户昵称
    password                character varying(80),          -- 密码
    username                character varying(60),          -- 用户名
    oauth_uuid              character varying               -- oauth2 uuid
);
comment on column public.t_user.id is '主键ID';
comment on column public.t_user.create_time is '创建时间';
comment on column public.t_user.create_user_id is '创建用户ID';
comment on column public.t_user.deleted is '是否逻辑删除';
comment on column public.t_user.update_time is '修改时间';
comment on column public.t_user.update_user_id is '修改用户ID';
comment on column public.t_user.create_user_name is '创建用户名';
comment on column public.t_user.update_user_name is '更新用户名';
comment on column public.t_user.expired is '是否过期';
comment on column public.t_user.locked is '是否锁定';
comment on column public.t_user.credentials_non_expired is '是否过期';
comment on column public.t_user.enabled is '是否启用';
comment on column public.t_user.nick_name is '用户昵称';
comment on column public.t_user.password is '密码';
comment on column public.t_user.username is '用户名';
comment on column public.t_user.oauth_uuid is 'oauth2 uuid';

create table public.t_user_role
(
    id               bigint primary key not null,    -- 主键ID
    create_time      timestamp(6) without time zone, -- 创建时间
    create_user_id   bigint,                         -- 创建用户ID
    deleted          boolean,                        -- 是否逻辑删除
    update_time      timestamp(6) without time zone, -- 修改时间
    update_user_id   bigint,                         -- 修改用户ID
    create_user_name character varying(60),          -- 创建用户名
    update_user_name character varying(60),          -- 更新用户名
    role_id          bigint,                         -- 角色ID
    user_id          bigint,                         -- 用户ID
    role_code        character varying(60),          -- 角色代码
    role_name        character varying(60)           -- 角色名称
);
comment on column public.t_user_role.id is '主键ID';
comment on column public.t_user_role.create_time is '创建时间';
comment on column public.t_user_role.create_user_id is '创建用户ID';
comment on column public.t_user_role.deleted is '是否逻辑删除';
comment on column public.t_user_role.update_time is '修改时间';
comment on column public.t_user_role.update_user_id is '修改用户ID';
comment on column public.t_user_role.create_user_name is '创建用户名';
comment on column public.t_user_role.update_user_name is '更新用户名';
comment on column public.t_user_role.role_id is '角色ID';
comment on column public.t_user_role.user_id is '用户ID';
comment on column public.t_user_role.role_code is '角色代码';
comment on column public.t_user_role.role_name is '角色名称';

CREATE TABLE public.t_book_info
(
    id               bigint PRIMARY KEY NOT NULL,             -- 主键ID
    create_time      timestamp(6) WITHOUT TIME ZONE,          -- 创建时间
    create_user_id   bigint,                                  -- 创建用户ID
    deleted          boolean,                                 -- 是否逻辑删除
    update_time      timestamp(6) WITHOUT TIME ZONE,          -- 修改时间
    update_user_id   bigint,                                  -- 修改用户ID
    create_user_name character varying(60),                   -- 创建用户名
    update_user_name character varying(60),                   -- 更新用户名
    book_name        character varying(255),                  -- 图书名称
    author           character varying(255),                  -- 作者
    publication_date date,                                    -- 出版日期
    ISBN             character varying(255),                  -- ISBN编号
    description      text                                     -- 图书描述
);

COMMENT ON COLUMN public.t_book_info.id IS '主键ID';
COMMENT ON COLUMN public.t_book_info.create_time IS '创建时间';
COMMENT ON COLUMN public.t_book_info.create_user_id IS '创建用户ID';
COMMENT ON COLUMN public.t_book_info.deleted IS '是否逻辑删除';
COMMENT ON COLUMN public.t_book_info.update_time IS '修改时间';
COMMENT ON COLUMN public.t_book_info.update_user_id IS '修改用户ID';
COMMENT ON COLUMN public.t_book_info.create_user_name IS '创建用户名';
COMMENT ON COLUMN public.t_book_info.update_user_name IS '更新用户名';
COMMENT ON COLUMN public.t_book_info.book_name IS '图书名称';
COMMENT ON COLUMN public.t_book_info.author IS '作者';
COMMENT ON COLUMN public.t_book_info.publication_date IS '出版日期';
COMMENT ON COLUMN public.t_book_info.ISBN IS 'ISBN编号';
COMMENT ON COLUMN public.t_book_info.description IS '图书描述';

CREATE TABLE public.t_book_borrow
(
    id               bigint PRIMARY KEY NOT NULL,             -- 主键ID
    create_time      timestamp(6) WITHOUT TIME ZONE,          -- 创建时间
    create_user_id   bigint,                                  -- 创建用户ID
    deleted          boolean,                                 -- 是否逻辑删除
    update_time      timestamp(6) WITHOUT TIME ZONE,          -- 修改时间
    update_user_id   bigint,                                  -- 修改用户ID
    create_user_name character varying(60),                   -- 创建用户名
    update_user_name character varying(60),                   -- 更新用户名
    user_id          bigint,                                  -- 用户ID
    book_id          bigint,                                  -- 图书ID
    borrow_date      date,                                    -- 借阅日期
    return_date      date,                                    -- 预计归还日期
    status           character varying(255)                   -- 状态
);

COMMENT ON COLUMN public.t_book_borrow.id IS '主键ID';
COMMENT ON COLUMN public.t_book_borrow.create_time IS '创建时间';
COMMENT ON COLUMN public.t_book_borrow.create_user_id IS '创建用户ID';
COMMENT ON COLUMN public.t_book_borrow.deleted IS '是否逻辑删除';
COMMENT ON COLUMN public.t_book_borrow.update_time IS '修改时间';
COMMENT ON COLUMN public.t_book_borrow.update_user_id IS '修改用户ID';
COMMENT ON COLUMN public.t_book_borrow.create_user_name IS '创建用户名';
COMMENT ON COLUMN public.t_book_borrow.update_user_name IS '更新用户名';
COMMENT ON COLUMN public.t_book_borrow.user_id IS '用户ID';
COMMENT ON COLUMN public.t_book_borrow.book_id IS '图书ID';
COMMENT ON COLUMN public.t_book_borrow.borrow_date IS '借阅日期';
COMMENT ON COLUMN public.t_book_borrow.return_date IS '预计归还日期';
COMMENT ON COLUMN public.t_book_borrow.status IS '状态';

CREATE TABLE public.t_book_return
(
    id               bigint PRIMARY KEY NOT NULL,             -- 主键ID
    create_time      timestamp(6) WITHOUT TIME ZONE,          -- 创建时间
    create_user_id   bigint,                                  -- 创建用户ID
    deleted          boolean,                                 -- 是否逻辑删除
    update_time      timestamp(6) WITHOUT TIME ZONE,          -- 修改时间
    update_user_id   bigint,                                  -- 修改用户ID
    create_user_name character varying(60),                   -- 创建用户名
    update_user_name character varying(60),                   -- 更新用户名
    user_id          bigint,                                  -- 用户ID
    book_id          bigint,                                  -- 图书ID
    borrow_id        bigint,                                  -- 借阅ID
    return_date      date                                     -- 归还日期
);

COMMENT ON COLUMN public.t_book_return.id IS '主键ID';
COMMENT ON COLUMN public.t_book_return.create_time IS '创建时间';
COMMENT ON COLUMN public.t_book_return.create_user_id IS '创建用户ID';
COMMENT ON COLUMN public.t_book_return.deleted IS '是否逻辑删除';
COMMENT ON COLUMN public.t_book_return.update_time IS '修改时间';
COMMENT ON COLUMN public.t_book_return.update_user_id IS '修改用户ID';
COMMENT ON COLUMN public.t_book_return.create_user_name IS '创建用户名';
COMMENT ON COLUMN public.t_book_return.update_user_name IS '更新用户名';
COMMENT ON COLUMN public.t_book_return.user_id IS '用户ID';
COMMENT ON COLUMN public.t_book_return.book_id IS '图书ID';
COMMENT ON COLUMN public.t_book_return.borrow_id IS '借阅ID';
COMMENT ON COLUMN public.t_book_return.return_date IS '归还日期';