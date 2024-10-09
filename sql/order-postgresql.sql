create table "order" (
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

comment on table "order" is '订单表'; 
comment on column "order".id is '主键';
comment on column "order".user_id is '用户id';
comment on column "order".product_id is '产品id';
comment on column "order".quantity is '数量';
comment on column "order".money is '金额';
comment on column "order".finished is '是否订单已完成';
comment on column "order".version is '修改版本';
comment on column "order".deleted is '是否已逻辑删除';
comment on column "order".creator is '创建人';
comment on column "order".modifier is '修改人';
comment on column "order".creation_time is '创建时间';
comment on column "order".modification_time is '修改时间';
