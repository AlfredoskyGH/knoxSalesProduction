create table oauth_client_token (
  token_id VARCHAR(256),
  token long varbinary,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256)
);


create table oauth_access_token (
  token_id VARCHAR(256),
  token long varbinary,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256),
  authentication long varbinary,
  refresh_token VARCHAR(256)
);


create table oauth_refresh_token (
  token_id VARCHAR(256),
  token long varbinary,
  authentication long varbinary
);


create table oauth_code (
  code VARCHAR(256),
  authentication long varbinary
);


insert into oauth_users (username, email, password, enabled) values ("stf-admin", "correo@ejemplo.com","$2a$10$iC57OHzeb.RK3UvANFfRbOKWxB409tOtNsMKz/8qxT3yKxK0sROdq",true);

insert into oauth_roles (created, enabled, name,type, aplicacion_cliente_id) values ('2018-04-10', 1, 'ADMIN_SSO', 'APLICACION', NULL);

insert into oauth_users_roles (user_id, role_id) values (1, 2);