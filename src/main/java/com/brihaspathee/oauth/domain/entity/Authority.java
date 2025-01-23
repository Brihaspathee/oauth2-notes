package com.brihaspathee.oauth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, January 2025
 * Time: 4:10 PM
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
@Table(name = "authority")
public class Authority {

    /**
     * Represents the unique identifier for the Authority entity.
     * This ID is marked as the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue
    private Long authorityId;

    /**
     * Represents the specific permission or capability associated with an authority.
     * This field defines the actionable permission that the authority grants.
     */
    private String permission;

    /**
     * Represents the association between the current authority and the roles that
     * are granted this authority. This establishes a bidirectional many-to-many
     * relationship with the Role entity, where the "roles" are defined in the Role class
     * and mapped by the "authorities" collection in that class.
     */
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;
}
