create table user_roles(
	id serial,
	role_name varchar(25) not null,
	
	constraint user_roles_pk
	primary key (id)
);
INSERT INTO user_roles
(role_name)
VALUES('ADMIN'),('FINANCE_MANAGER'),('EMPLOYEE');
INSERT INTO user_roles
(role_name)
VALUES('DELETED');

-- +++--------------------------------------------+++ --
create table reimbursement_types(
	id serial,
	role_name varchar(10) not null,
	
	constraint reimbursement_types_pk
	primary key (id)
);
INSERT INTO reimbursement_types
(role_name)
VALUES('LODGING'),('TRAVEL'),('FOOD'),('OTHER');

-- +++--------------------------------------------+++ --
create table reimbursement_statuses(
	id serial,
	role_name varchar(10) not null,
	
	constraint reimbursement_statuses_pk
	primary key (id)
);
INSERT INTO reimbursement_statuses
(role_name)
VALUES('PENDING'),('APPROVED'),('DENIED'),('CLOSED');

-- +++--------------------------------------------+++ --
create table users(
	id serial,
	username varchar(25) unique not null,
	password varchar(256) not null,
	first_name varchar(25) not null,
	last_name varchar(25) not null,
	email varchar(256) unique not null,
	user_role_id int not null,
	is_active boolean default true,
	
	
	constraint user_id
	primary key (id),
	
	constraint user_roles_fk
	foreign key (user_role_id)
	references user_roles
);


-- +++--------------------------------------------+++ --
-- reimbursements definition

-- Drop table

-- DROP TABLE reimbursements;

create TABLE reimbursements (
	id serial NOT NULL,
	amount numeric(6,2) NOT NULL,
	submitted timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	resolved timestamp NULL,
	description varchar(1000) NULL,
	receipt bytea,
	author_id int4 NOT NULL,
	resolver_id int4 NULL,
	reimbursement_status_id int4 NOT NULL,
	reimbursement_type_id int4 NOT NULL,
	CONSTRAINT reimbursements_pk PRIMARY KEY (id)
);


-- reimbursements foreign keys

ALTER TABLE reimbursements
ADD CONSTRAINT author_id_fk 
FOREIGN KEY (author_id) 
REFERENCES users(id);

ALTER TABLE reimbursements
ADD CONSTRAINT reimbursement_status_id_fk 
FOREIGN KEY (reimbursement_status_id) 
REFERENCES reimbursement_statuses(id);

ALTER TABLE reimbursements
ADD CONSTRAINT reimbursement_type_id_fk 
FOREIGN KEY (reimbursement_type_id) 
REFERENCES reimbursement_types(id);

ALTER TABLE reimbursements
ADD CONSTRAINT resolver_id_fk 
FOREIGN KEY (resolver_id) 
REFERENCES users(id);




INSERT INTO users
(username, password, first_name, last_name, email, user_role_id)
VALUES('u6', crypt('password', gen_salt('bf', 10)), 'troy', 'davis', 'u6', 1);

select * from users eu
where password = crypt('password', password);

--https://www.meetspaceapp.com/2016/04/12/passwords-postgresql-pgcrypto.html

truncate users ;

select * from users eu ;

select * from reimbursements er ;
--needed to be able to hash and unhash the passwords
CREATE EXTENSION pgcrypto;

SELECT er.id, er.amount, er.description, er.reimbursement_status_id, 
er.reimbursement_type_id, er.resolved, er.submitted,  er.author_id , er.resolver_id,
author.first_name as author_first_name , author.last_name as author_last_name , 
resolver.first_name as resolver_first_name, resolver.last_name as resolver_last_name
FROM reimbursements er
left join users author
on er.author_id = author.id
left join users resolver
on er.resolver_id = resolver.id;

SELECT * FROM reimbursements er
join users author
on er.author_id = author.id
where author.username = 'u4';

