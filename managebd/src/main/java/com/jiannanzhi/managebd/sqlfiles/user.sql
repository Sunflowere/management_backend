create table qingge_bg.user
(
    id         bigint auto_increment comment '用户id'
        primary key,
    username   varchar(256)                       not null comment '用户名',
    password   varchar(512)                       not null comment '用户密码',
    nickname   varchar(256)                       null comment '用户昵称',
    email      varchar(512)                       null comment '用户邮箱',
    phone      varchar(128)                       null comment '电话',
    address    varchar(512)                       null comment '用户地址',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '用户表';