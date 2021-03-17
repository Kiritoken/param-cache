begin;
#
# create schema cache;

use cache;


# 应用配置表
drop table if exists app_config;
create table app_config(
 id int(11) not null auto_increment,
 app_code varchar(32) not null comment '系统编号',
 app_name varchar(64) not null comment '系统名称',
 app_des varchar(200) comment'系统描述',
 create_user int(11) not null comment'创建人ID',
 modifier int(11) not null  comment'修改人ID',
 gmt_create datetime not null default CURRENT_TIMESTAMP COMMENT '创建时间',
 gmt_modified timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
 eff_flg tinyint(1) not null default '1' COMMENT '0:未启用，1：启用',
 primary key(id),
 unique key idx_code(app_code)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用配置表';


# 数据源配置表
drop table if exists datasource_config;
create table datasource_config(
 id int(11) not null auto_increment,
 datasource_code varchar(32) not null comment '数据源编号',
 datasource_des varchar(200) comment '数据源描述',
 url varchar(400) not null comment'数据源连接串',
 user_name varchar(50) not null  comment '连接用户',
 credential varchar(50) not null  comment '密码',
 gmt_create datetime not null default CURRENT_TIMESTAMP COMMENT '创建时间',
 gmt_modified timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
 primary key(id),
 unique key idx_source_code(datasource_code)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源配置表';


# 参数缓存配置表
drop table if exists cache_config;
create table cache_config(
 id int(11) not null auto_increment,
 app_code varchar(32) not null comment '系统编号',
 datasource_code varchar(32) not null comment '数据源编号',
 table_name varchar(64) not null comment '表名',
 clazz_name varchar(64) not null comment '对应的PO',
 gmt_create datetime not null default CURRENT_TIMESTAMP COMMENT '创建时间',
 gmt_modified timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
 version bigint not null comment '版本号',
 primary key(id),
 unique key idx_app_source_table(app_code,datasource_code,table_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='缓存参数配置表';



commit ;