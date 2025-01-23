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