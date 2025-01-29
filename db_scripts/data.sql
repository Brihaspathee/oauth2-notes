-- Authorities

-- Authority Authorities
insert into oauth2notesdb.authority(authority_id, permission) values (1001, 'authority.create');
insert into oauth2notesdb.authority(authority_id, permission) values (1002, 'authority.read');
insert into oauth2notesdb.authority(authority_id, permission) values (1003, 'authority.update');
insert into oauth2notesdb.authority(authority_id, permission) values (1004, 'authority.delete');

-- Role Authorities
insert into oauth2notesdb.authority(authority_id, permission) values (1005, 'role.create');
insert into oauth2notesdb.authority(authority_id, permission) values (1006, 'role.read');
insert into oauth2notesdb.authority(authority_id, permission) values (1007, 'role.update');
insert into oauth2notesdb.authority(authority_id, permission) values (1008, 'role.delete');

-- User Authorities
insert into oauth2notesdb.authority(authority_id, permission) values (1009, 'user.create');
insert into oauth2notesdb.authority(authority_id, permission) values (1010, 'user.read');
insert into oauth2notesdb.authority(authority_id, permission) values (1011, 'user.update');
insert into oauth2notesdb.authority(authority_id, permission) values (1012, 'user.delete');

-- Note Authorities
insert into oauth2notesdb.authority(authority_id, permission) values (1013, 'note.create');
insert into oauth2notesdb.authority(authority_id, permission) values (1014, 'note.read');
insert into oauth2notesdb.authority(authority_id, permission) values (1015, 'note.update');
insert into oauth2notesdb.authority(authority_id, permission) values (1016, 'note.delete');

-- Roles
INSERT INTO oauth2notesdb.role(role_id, role_name) VALUES ('2001', 'ADMIN');
INSERT INTO oauth2notesdb.role(role_id, role_name) VALUES ('2002', 'USER');
INSERT INTO oauth2notesdb.role(role_id, role_name) VALUES ('2003', 'CUSTOMER_REP');

-- Role Authority Relationship

-- Admin authorities

INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1001);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1002);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1003);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1004);

INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1005);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1006);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1007);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1008);

INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1009);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1010);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1011);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1012);

INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1013);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1014);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1015);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2001, 1016);

-- USER Authorities
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2002, 1013);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2002, 1014);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2002, 1015);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2002, 1016);

-- Customer Rep Authorities
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2003, 1002);

INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2003, 1006);

INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2003, 1010);

INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2003, 1013);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2003, 1014);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2003, 1015);
INSERT INTO oauth2notesdb.role_authority(role_id, authority_id) VALUES (2003, 1016);

-- Users

INSERT INTO oauth2notesdb.security_user
(user_id, email, password, auth_method)
VALUES(3001, 'john.doe@admins.com', '$2a$10$q15whOtuMFuQIWqRNzzLzezI7.DURBkOL773py64tMdf6DN.x8IgG', 'EMAIL');

INSERT INTO oauth2notesdb.security_user
(user_id, email, password, auth_method)
VALUES(3002, 'mary.doe@customerrep.com', '$2a$10$vTh8UGPShOV1CTungBEC4.vC16cyxyxIXcmGAg4xqDGuyQAbkSiby', 'EMAIL');

-- User Role Relationship
INSERT INTO oauth2notesdb.user_role(user_id, role_id) VALUES (3001, 2001);
INSERT INTO oauth2notesdb.user_role(user_id, role_id) VALUES (3002, 2003);

