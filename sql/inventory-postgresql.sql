create table "inventory" (
    "id" bigserial,
    "product_id" bigint,
    "quantity" integer default null,
    "used" integer default null,
    "residue" integer default null,
	"version" integer not null default 1,
    "deleted" boolean not null default false,
    "creator" bigint,
    "modifier" bigint,
    "creation_time" timestamptz,
    "modification_time" timestamptz,
    primary key(id)
);

comment on table "inventory" is '库存表'; 
comment on column "inventory".id is '主键';
comment on column "inventory".product_id is '产品id';
comment on column "inventory".quantity is '库存';
comment on column "inventory".used is '已用库存';
comment on column "inventory".residue is '剩余库存';
comment on column "inventory".version is '修改版本';
comment on column "inventory".deleted is '是否已逻辑删除';
comment on column "inventory".creator is '创建人';
comment on column "inventory".modifier is '修改人';
comment on column "inventory".creation_time is '创建时间';
comment on column "inventory".modification_time is '修改时间';

insert into inventory("id", "product_id", "quantity", "used", "residue")
    values(1, 1, 10, 0, 10);
