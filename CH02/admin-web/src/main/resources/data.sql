INSERT INTO customers (name, contact_name, email, phone) values ('Acme', 'Wylie Coyote', 'wcoyote@acme.com', '1-515-555-2348');
INSERT INTO customers (name, contact_name, email, phone)
values ('Spacely Space Sprockets', 'George Jettson', 'gjettson@spacely.com', '1-515-555-2350');
INSERT INTO customers (name, contact_name, email, phone)
values ('Callahan Auto', 'Thomas Callhan', 'tcallahan@callhhanauto.com', '1-515-555-2333');
INSERT INTO customers (name, contact_name, email, phone)
values ('Dundler Mifflin Inc', 'Michael Scott', 'mscott@dundlermifflin.com', '1-515-555-2320');
INSERT INTO customers (name, contact_name, email, phone)
values ('Stark Industries', 'Tony Stark', 'tstark@stark.com', '1-515-555-7777');
INSERT INTO customers (name, contact_name, email, phone)
values ('Initech', 'Peter Gibbons', 'pgibbons@initec.com', '1-515-555-0666');
INSERT INTO customers (name, contact_name, email, phone)
values ('Wayne Enterprises', 'Bruce Wayne', 'bwayne@wayne.com', '1-515-555-1111');

INSERT INTO orders (customer_id, order_info)
values ((SELECT customer_id FROM customers where name = 'Acme'), '1500 Widgets');
INSERT INTO orders (customer_id, order_info)
values ((SELECT customer_id FROM customers where name = 'Acme'), '3000 Widgets');
INSERT INTO orders (customer_id, order_info)
values ((SELECT customer_id FROM customers where name = 'Callahan Auto'), '200 Widgets');

INSERT INTO users (username, password, enabled)
--values ('user', '{bcrypt}$2a$10$XlkdPQQhYcolx8bgp6nL3uNvDs8ZwDXA4KFaDencZsIhjMQO3j5lq', true);
values ('user', '$2a$10$XlkdPQQhYcolx8bgp6nL3uNvDs8ZwDXA4KFaDencZsIhjMQO3j5lq', true);
INSERT INTO users (username, password, enabled)
--values ('admin', '{bcrypt}$2a$10$XlkdPQQhYcolx8bgp6nL3uNvDs8ZwDXA4KFaDencZsIhjMQO3j5lq', true);
values ('admin', '$2a$10$XlkdPQQhYcolx8bgp6nL3uNvDs8ZwDXA4KFaDencZsIhjMQO3j5lq', true);

INSERT INTO users (username, password, enabled)
values ('david', '$2a$10$jyX4RlL0H9EWlJVpGagFau2/jHPrHksHS2jMmujPJ1vztfSHU7rzy', true);

--create a duplicate user to monitor how Spring Security behaves when multiple authentication providers are used.
INSERT INTO users (username, password, enabled)
--The password is different from the one in LDAP.
--values ('adminLdap', '$2a$10$ATR/jW94UdOd8ovf7jnSHOAupJduo8mxWiIdzBp8Lp9BRlSPLGGAy', true);
--The password is the same with the one in LDAP.
values ('adminLdap', '$2a$10$XlkdPQQhYcolx8bgp6nL3uNvDs8ZwDXA4KFaDencZsIhjMQO3j5lq', true);

INSERT INTO authorities (username, authority)
values ('user', 'ROLE_USER');
INSERT INTO authorities (username, authority)
values ('admin', 'ROLE_USER');
INSERT INTO authorities (username, authority)
values ('admin', 'ROLE_ADMIN');

INSERT INTO authorities (username, authority)
values ('david', 'ROLE_USER');
--Added to verify whether the data is used by "authoritiesMapper()"
INSERT INTO authorities (username, authority)
values ('david', 'ADMIN');

--grant different(from LDAP) role to adminLdap. In LDAP, adminLdap has ROLE_ADMIN role too.
INSERT INTO authorities (username, authority)
values ('adminLdap', 'ROLE_USER');

