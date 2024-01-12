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

