create table "account" (
    "id" bigserial,
    "user_id" bigint,
    "total_amount" money default null,
    "spent" money default null,
    "balance" money default null,
	"version" integer not null default 1,
    "deleted" boolean not null default false,
    "creator" bigint,
    "modifier" bigint,
    "creation_time" timestamptz,
    "modification_time" timestamptz,
    primary key(id)
);

comment on table "account" is '账户表'; 
comment on column "account".id is '主键';
comment on column "account".user_id is '用户id';
comment on column "account".total_amount is '总金额';
comment on column "account".spent is '已用金额';
comment on column "account".balance is '余额';
comment on column "account".version is '修改版本';
comment on column "account".deleted is '是否已逻辑删除';
comment on column "account".creator is '创建人';
comment on column "account".modifier is '修改人';
comment on column "account".creation_time is '创建时间';
comment on column "account".modification_time is '修改时间';

insert into account("id", "user_id", "total_amount", "spent", "balance")
    values(1, 1, 100, 0, 100);
