package com.brihaspathee.oauth.permissions;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 23, January 2025
 * Time: 12:37 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.permissions
 * To change this template use File | Settings | File and Code Template
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('authority.create')")
public @interface AuthorityCreatePermission {
}
