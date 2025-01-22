package com.brihaspathee.oauth.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a User entity in the system that implements the UserDetails interface
 * for security purposes.
 * The class is annotated for use with Java's Persistence API (JPA) and functions
 * as a data model for user-related information, including authentication and role management.
 */
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "security_user")
public class User implements UserDetails {

    /**
     * Represents the unique identifier for a user entity.
     * This field is annotated with {@code @Id} to mark it as the primary key.
     * It is automatically generated using the {@code @GeneratedValue} annotation.
     * The {@code @Column} annotation specifies additional attributes such as
     * the database column name, type, length, and constraints.
     */
    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false, updatable = false, insertable = true, columnDefinition = "bigint", length = 20)
    private Long userId;

    /**
     * Represents the email address of a user.
     * This field is mandatory, non-updatable, and must be unique.
     * The email is a string with a maximum length of 100 characters and
     * is stored in the database as a varchar column.
     */
    @Column(name = "email", nullable = false, updatable = false, insertable = true, unique = true, columnDefinition = "varchar", length = 100)
    private String email;

    /**
     * Represents the password field for a user entity.
     * This field is a required attribute and will be stored
     * in the database as a VARCHAR type with a maximum length of 100 characters.
     * The password is both insertable and updatable.
     */
    @Column(name = "password", nullable = false, updatable = true, insertable = true, columnDefinition = "varchar", length = 100)
    private String password;

    /**
     * Represents the authentication method used by a user.
     *
     * This field is mandatory and cannot be updated once the value is set.
     * The value is stored as a VARCHAR in the database with a maximum length of 100 characters.
     */
    @Column(name = "auth_method", nullable = false, updatable = false, insertable = true, columnDefinition = "varchar", length = 100)
    private String authenticationMethod;

    /**
     * Represents the GitHub ID of a user.
     * This field is stored in the database with the column name "github_id".
     * The value is a non-null, immutable string with a maximum length of 100 characters.
     * This is intended to uniquely identify the user's GitHub account associated with the application.
     */
    @Column(name = "github_id", nullable = false, updatable = false, insertable = true, columnDefinition = "varchar", length = 100)
    private String githubId;

    /**
     * Represents the GitHub login identifier for a user.
     * This field is mapped to the "github_login" column in the database.
     * The GitHub login is required, cannot be modified once set, and must be inserted at the time of creation.
     * The maximum length for this field is 100 characters.
     */
    @Column(name = "github_login", nullable = false, updatable = false, insertable = true, columnDefinition = "varchar", length = 100)
    private String githubLogin;

    /**
     * Represents the URL of the user's GitHub avatar.
     * This field is used to store the link to the avatar image associated
     * with the user's GitHub account, enabling integration with external systems
     * or rendering the user's profile picture in the application.
     */
    @Column(name = "github_avatar_url", nullable = false, updatable = true, insertable = true, columnDefinition = "varchar", length = 100)
    private String githubAvatarUrl;

    /**
     * Represents the roles assigned to a user in a many-to-many relationship.
     * This variable is annotated with @Singular to allow for a fluent API
     * when constructing instances.
     * The @ManyToMany annotation signifies the many-to-many association
     * between the User and Role entities.
     * The associated table, named 'USER_ROLE', defines the relationship,
     * where 'USER_ID' is the join column referencing the User entity
     * and 'ROLE_ID' is the inverse join column referencing the Role entity.
     * Fetch type is set to EAGER, meaning the roles will be immediately fetched
     * along with the user entity.
     */
    @Singular
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<Role> roles;

    /**
     * Represents the profile associated with a specific user.
     * This entity maintains a one-to-one relationship with the User entity.
     * The relationship is bidirectional and this side is mapped by the "user" property in the Profile entity.
     */
    @OneToOne(mappedBy = "user")
    private Profile profile;

    /**
     * Represents the collection of notes associated with a user.
     * This is a one-to-many relationship, where a single user can have multiple notes.
     * The notes are mapped by the "user" field in the Note entity.
     */
    @OneToMany(mappedBy = "user")
    private Set<Note> notes;

    /**
     * Retrieves the collection of granted authorities associated with the roles.
     *
     * @return a collection of granted authorities derived from the permissions of the roles.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .map(authority ->
                        new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the password of the user.
     *
     * @return the password of the user as a String.
     */
    @Override
    public String getPassword() {
        return "";
    }

    /**
     * Retrieves the username of the user.
     *
     * @return the username of the user as a String.
     */
    @Override
    public String getUsername() {
        return "";
    }

    /**
     * Checks if the user account is not expired.
     *
     * @return true if the account is not expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Checks if the user account is not locked.
     *
     * @return true if the account is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * Checks if the user's credentials are non-expired.
     *
     * @return true if the user's credentials are non-expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * Checks if the user account is enabled.
     *
     * @return true if the account is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    /**
     * Returns a string representation of the User object, including its ID, email,
     * password, authentication method, GitHub ID, GitHub login, and GitHub avatar URL.
     *
     * @return a string representation of the User object.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + userId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", authenticationMethod='" + authenticationMethod + '\'' +
                ", githubId='" + githubId + '\'' +
                ", githubLogin='" + githubLogin + '\'' +
                ", githubAvatarUrl='" + githubAvatarUrl + '\'' +
                '}';
    }
}
