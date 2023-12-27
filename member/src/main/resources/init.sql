create table t_role
(
    id             bigint not null
        primary key,
    create_time    timestamp(6),
    create_user_id bigint,
    deleted        boolean,
    update_time    timestamp(6),
    update_user_id bigint,
    create_user_name        varchar(20),
    update_user_name        varchar(20),
    role_code      varchar(255),
    role_desc      varchar(255),
    role_name      varchar(255)
);

comment on column t_role.id is '主键ID';

comment on column t_role.create_time is '创建时间';

comment on column t_role.create_user_id is '创建用户ID';

comment on column t_role.deleted is '是否逻辑删除';

comment on column t_role.update_time is '修改时间';

comment on column t_role.update_user_id is '修改用户ID';

comment on column t_role.role_code is '角色编码';

comment on column t_role.role_desc is '角色描述';

comment on column t_role.role_name is '角色名称';

comment on column t_role.create_user_name is '创建用户名';

comment on column t_role.update_user_name is '更新用户名';




create table t_user
(
    id                      bigint  not null
        primary key,
    create_time             timestamp(6),
    create_user_id          bigint,
    deleted                 boolean,
    update_time             timestamp(6),
    update_user_id          bigint,
    create_user_name        varchar(20),
    update_user_name        varchar(20),
    expired                 boolean,
    locked                  boolean,
    credentials_non_expired boolean not null,
    enabled                 boolean not null,
    nick_name               varchar(20),
    password                varchar(80),
    username                varchar(20)
);

comment on column t_user.id is '主键ID';

comment on column t_user.create_time is '创建时间';

comment on column t_user.create_user_id is '创建用户ID';

comment on column t_user.deleted is '是否逻辑删除';

comment on column t_user.update_time is '修改时间';

comment on column t_user.update_user_id is '修改用户ID';

comment on column t_user.expired is '是否过期';

comment on column t_user.locked is '是否锁定';

comment on column t_user.credentials_non_expired is '是否过期';

comment on column t_user.enabled is '是否启用';

comment on column t_user.nick_name is '用户昵称';

comment on column t_user.password is '密码';

comment on column t_user.username is '用户名';

comment on column t_user.create_user_name is '创建用户名';

comment on column t_user.update_user_name is '更新用户名';



create table t_user_role
(
    id             bigint not null
        primary key,
    create_time    timestamp(6),
    create_user_id bigint,
    deleted        boolean,
    update_time    timestamp(6),
    update_user_id bigint,
    create_user_name        varchar(20),
    update_user_name        varchar(20),
    role_id        bigint,
    user_id        bigint,
    role_code      varchar(20),
    role_name      varchar(20)
);

comment on column t_user_role.id is '主键ID';

comment on column t_user_role.create_time is '创建时间';

comment on column t_user_role.create_user_id is '创建用户ID';

comment on column t_user_role.deleted is '是否逻辑删除';

comment on column t_user_role.update_time is '修改时间';

comment on column t_user_role.update_user_id is '修改用户ID';

comment on column t_user_role.role_id is '角色ID';

comment on column t_user_role.user_id is '用户ID';

comment on column t_user_role.role_code is '角色代码';

comment on column t_user_role.role_name is '角色名称';

comment on column t_user_role.create_user_name is '创建用户名';

comment on column t_user_role.update_user_name is '更新用户名';



