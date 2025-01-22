package com.brihaspathee.oauth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, January 2025
 * Time: 4:11 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.domain.entity
 * To change this template use File | Settings | File and Code Template
 */
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /**
     * Represents the unique identifier for the Role entity.
     * This field serves as the primary key for the database table
     * associated with the Role entity and is auto-generated.
     */
    @Id
    @GeneratedValue
    private Long roleId;

    /**
     * Represents the name of the role.
     * This field identifies the specific role assigned to users or associated with authorities.
     */
    private String roleName;

    /**
     * Represents the set of authorities associated with a specific role.
     * This establishes a many-to-many relationship between the Role and Authority entities,
     * where a role can have multiple authorities, and an authority can belong to multiple roles.
     * The relationship is eagerly fetched and persisted into a join table named "ROLE_AUTHORITY".
     *
     * The join table "ROLE_AUTHORITY" contains the following columns:
     * - "ROLE_ID": References the primary key of the Role entity.
     * - "AUTHORITY_ID": References the primary key of the Authority entity.
     */
    @Singular
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_AUTHORITY",
            joinColumns = @JoinColumn(name = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "AUTHORITY_ID"))
    private Set<Authority> authorities;

    /**
     * Represents the set of users associated with a specific role.
     * This establishes a bidirectional many-to-many relationship between the Role and User entities,
     * where a role can be associated with multiple users, and a user can have multiple roles.
     *
     * The relationship is mapped by the "roles" field in the User entity.
     */
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
