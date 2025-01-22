drop table if exists `oauth2notesdb`.`security_user`;
drop table if exists `oauth2notesdb`.`role`;
drop table if exists `oauth2notesdb`.`authority`;
drop table if exists `oauth2notesdb`.`user_role`;
drop table if exists `oauth2notesdb`.`role_authority`;
CREATE TABLE IF NOT EXISTS `oauth2notesdb`.`authority` (
                                                          `authority_id` VARCHAR(36) NOT NULL COMMENT 'The authority id associated with the authority',
    `permission` VARCHAR(100) NOT NULL COMMENT 'The permission that is associated with the authority',
    `created_date` DATETIME NOT NULL COMMENT 'The date when the authority was created',
    `updated_date` DATETIME NOT NULL COMMENT 'The date when the authority was last updated',
    PRIMARY KEY (`authority_id`))
    ENGINE = InnoDB
    COMMENT = 'The table that contains the list of authorities';
CREATE TABLE IF NOT EXISTS `oauth2notesdb`.`role` (
                                                      `role_id` VARCHAR(36) NOT NULL COMMENT 'The role id for the role',
    `role_name` VARCHAR(100) NOT NULL COMMENT 'The role name associated with the role',
    `created_date` DATETIME NOT NULL COMMENT 'The date when the role was created',
    `updated_date` DATETIME NOT NULL COMMENT 'The date last when the role was updated',
    PRIMARY KEY (`role_id`))
    ENGINE = InnoDB
    COMMENT = 'The table that contains the roles for the service';
CREATE TABLE `oauth2notesdb`.`security_user` (
                                             user_id BIGINT NOT NULL COMMENT 'User id for the user',
                                             email varchar(100) NOT NULL COMMENT 'Email id of the user, it is also the username',
                                             password varchar(100) NOT NULL COMMENT 'Password of the user account',
                                             auth_method varchar(100) NULL COMMENT 'Authentication method for the user account',
                                             github_id varchar(100) NOT NULL COMMENT 'The giyhub id of the user',
                                             github_login varchar(100) NOT NULL COMMENT 'The github login id of the user',
                                             github_avatar_url varchar(100) NULL COMMENT 'The github avatar url of the user',
                                             CONSTRAINT security_user_pk PRIMARY KEY (user_id),
                                             CONSTRAINT security_user_unique UNIQUE KEY (email),
                                             CONSTRAINT security_user_github_id UNIQUE KEY (github_id),
                                             CONSTRAINT security_user_github_login UNIQUE KEY (github_login)
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci
COMMENT='List of users';
CREATE TABLE IF NOT EXISTS `oauth2notesdb`.`user_role` (
                                                           `user_id` BIGINT NOT NULL COMMENT 'User id that is mapped to the role',
                                                           `role_id` VARCHAR(36) NOT NULL COMMENT 'Role that is mapped to the user',
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
                                                                `role_id` VARCHAR(36) NOT NULL COMMENT 'Role that is mapped to the authority',
    `authority_id` VARCHAR(36) NOT NULL COMMENT 'Authority that is mapped to the role',
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
