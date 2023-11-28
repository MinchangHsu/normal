create schema normal;
DROP TABLE IF EXISTS normal.cf_menu;
create table normal.cf_menu
(
    id          int auto_increment comment '編號'
        primary key,
    name        varchar(32)      null comment '名稱',
    url         varchar(64)      null comment '地址',
    text        varchar(128)     null comment '介紹',
    `index`     bigint           null comment '順序',
    parentid    varchar(32)      null comment '父級id',
    createdDate bigint default 0 null comment '創建時間',
    updatedDate bigint default 0 null comment '修改時間',
    createdUser varchar(32)      null,
    updatedUser varchar(32)      null,
    status      int    default 1 null comment '狀態(啟用禁用)',
    type        int    default 0 null comment '(類型，0：菜單  1.button)',
    menuType    int    default 0 null comment '菜單類型( 0：mis內網訪問，1：外網開放菜單)'
)
    comment '菜單信息' charset = utf8mb3;

