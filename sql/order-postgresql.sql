create table "t_order" (
    "id" bigserial,
    "user_id" bigint,
    "product_id" bigint,
    "quantity" integer,
    "money" money,
    "finished" boolean not null default false,
	"version" integer not null default 1,
    "deleted" boolean not null default false,
    "creator" bigint,
    "modifier" bigint,
    "creation_time" timestamptz,
    "modification_time" timestamptz,
    primary key(id)
);

comment on table "t_order" is '订单表'; 
comment on column "t_order".id is '主键';
comment on column "t_order".user_id is '用户id';
comment on column "t_order".product_id is '产品id';
comment on column "t_order".quantity is '数量';
comment on column "t_order".money is '金额';
comment on column "t_order".finished is '是否订单已完成';
comment on column "t_order".version is '修改版本';
comment on column "t_order".deleted is '是否已逻辑删除';
comment on column "t_order".creator is '创建人';
comment on column "t_order".modifier is '修改人';
comment on column "t_order".creation_time is '创建时间';
comment on column "t_order".modification_time is '修改时间';
