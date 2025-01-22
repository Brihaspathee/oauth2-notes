DROP DATABASE IF EXISTS oauth2notesdb;
DROP USER IF EXISTS `oauth2notesadmin`@`%`;
DROP USER IF EXISTS `oauth2notesapp`@`%`;
CREATE DATABASE IF NOT EXISTS oauth2notesdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `oauth2notesadmin`@`%` IDENTIFIED WITH mysql_native_password BY 'password'; -- caching_sha2_password
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `oauth2notesdb`.* TO `oauth2notesadmin`@`%`;
CREATE USER IF NOT EXISTS `oauth2notesapp`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `oauth2notesdb`.* TO `oauth2notesapp`@`%`;
FLUSH PRIVILEGES;