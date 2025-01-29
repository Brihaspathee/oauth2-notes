drop table if exists `oauth2notesdb`.`profile`;
drop table if exists `oauth2notesdb`.`note`;
drop table if exists `oauth2notesdb`.`user_role`;
drop table if exists `oauth2notesdb`.`role_authority`;
drop table if exists `oauth2notesdb`.`auth_github`;
drop table if exists `oauth2notesdb`.`auth_google`;
drop table if exists `oauth2notesdb`.`security_user`;
drop table if exists `oauth2notesdb`.`role`;
drop table if exists `oauth2notesdb`.`authority`;
CREATE TABLE IF NOT EXISTS `oauth2notesdb`.`authority` (
                                                           `authority_id` BIGINT NOT NULL COMMENT 'The authority id associated with the authority',
                                                           `permission` VARCHAR(100) NOT NULL COMMENT 'The permission that is associated with the authority',
    PRIMARY KEY (`authority_id`))
    ENGINE = InnoDB
    COMMENT = 'The table that contains the list of authorities';
CREATE TABLE IF NOT EXISTS `oauth2notesdb`.`role` (
                                                      `role_id` BIGINT NOT NULL COMMENT 'The role id for the role',
                                                      `role_name` VARCHAR(100) NOT NULL COMMENT 'The role name associated with the role',
    PRIMARY KEY (`role_id`))
    ENGINE = InnoDB
    COMMENT = 'The table that contains the roles for the service';
CREATE TABLE `oauth2notesdb`.`security_user` (
                                                 user_id BIGINT NOT NULL COMMENT 'User id for the user',
                                                 email varchar(100) NOT NULL COMMENT 'Email id of the user, it is also the username',
                                                 password varchar(100) NULL COMMENT 'Password of the user account',
                                                 auth_method varchar(100) NULL COMMENT 'Authentication method for the user account',
                                                 CONSTRAINT security_user_pk PRIMARY KEY (user_id),
                                                 CONSTRAINT security_user_unique UNIQUE KEY (email)
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='List of users';
-- oauth2notesdb.auth_github definition

CREATE TABLE `auth_github` (
                               `auth_github_id` bigint NOT NULL COMMENT 'The primary key of the table',
                               `github_id` varchar(100) NOT NULL COMMENT 'The github id of the user',
                               `github_login` varchar(100) NOT NULL COMMENT 'The github login id',
                               `github_avatar_url` varchar(100) NOT NULL COMMENT 'The github user avatar URL',
                               `user_id` bigint NOT NULL COMMENT 'The user id of the user to who the github is associated',
                               PRIMARY KEY (`auth_github_id`),
                               KEY `auth_github_security_user_FK` (`user_id`),
                               CONSTRAINT `auth_github_security_user_FK` FOREIGN KEY (`user_id`) REFERENCES `security_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- oauth2notesdb.auth_google definition

CREATE TABLE `auth_google` (
                               `auth_google_id` bigint NOT NULL COMMENT 'The primary key of the table',
                               `user_id` bigint NOT NULL COMMENT 'The user to whom the google account belongs',
                               `google_id` varchar(100) NOT NULL COMMENT 'The unique google id',
                               `google_name` varchar(100) NOT NULL COMMENT 'The name of the user in google',
                               `google_picture_url` varchar(100) DEFAULT NULL COMMENT 'The URL of the user''s picture in google',
                               PRIMARY KEY (`auth_google_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS `oauth2notesdb`.`user_role` (
                                                           `user_id` BIGINT NOT NULL COMMENT 'User id that is mapped to the role',
                                                           `role_id` BIGINT NOT NULL COMMENT 'Role that is mapped to the user',
                                                           INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
    INDEX `role_id_idx` (`role_id` ASC) VISIBLE,
    CONSTRAINT `user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `oauth2notesdb`.`security_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `role_id`
    FOREIGN KEY (`role_id`)
    REFERENCES `oauth2notesdb`.`role` (`role_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB
    COMMENT = 'The mapping table, to map the users with respective roles.';
CREATE TABLE IF NOT EXISTS `oauth2notesdb`.`role_authority` (
                                                                `role_id` BIGINT NOT NULL COMMENT 'Role that is mapped to the authority',
                                                                `authority_id` BIGINT NOT NULL COMMENT 'Authority that is mapped to the role',
                                                                INDEX `role_auth_id_idx` (`role_id` ASC) VISIBLE,
    INDEX `authority_id_idx` (`authority_id` ASC) VISIBLE,
    CONSTRAINT `role_auth_id`
    FOREIGN KEY (`role_id`)
    REFERENCES `oauth2notesdb`.`role` (`role_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `authority_id`
    FOREIGN KEY (`authority_id`)
    REFERENCES `oauth2notesdb`.`authority` (`authority_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB
    COMMENT = 'The mapping table, to map roles to authorities.';
CREATE TABLE `oauth2notesdb`.`profile` (
                                           profile_id BIGINT NOT NULL COMMENT 'Primary key of the profile',
                                           first_name varchar(100) NOT NULL COMMENT 'First Name of the user',
                                           middle_name varchar(100) NULL COMMENT 'Middle name of the user',
                                           last_name varchar(100) NOT NULL COMMENT 'Last Name of the user',
                                           date_of_birth DATE NOT NULL COMMENT 'Date of birth of the user',
                                           user_id BIGINT NOT NULL COMMENT 'User id of the user to who the profile belongs',
                                           CONSTRAINT profile_pk PRIMARY KEY (profile_id),
                                           CONSTRAINT profile_security_user_FK FOREIGN KEY (user_id) REFERENCES oauth2notesdb.security_user(user_id)
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='Profile of the user';
CREATE TABLE `oauth2notesdb`.`note` (
                                        note_id BIGINT NOT NULL COMMENT 'The primary key of the note',
                                        note_title varchar(100) NOT NULL COMMENT 'The title of the note',
                                        note_content varchar(100) NOT NULL COMMENT 'The content of the note',
                                        user_id BIGINT NOT NULL COMMENT 'The user to whom the notes belong',
                                        CONSTRAINT note_pk PRIMARY KEY (note_id),
                                        CONSTRAINT note_security_user_FK FOREIGN KEY (user_id) REFERENCES oauth2notesdb.security_user(user_id)
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='The notes created by users';
