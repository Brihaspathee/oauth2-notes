package com.brihaspathee.oauth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, January 2025
 * Time: 4:29 PM
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
public class Profile {

    /**
     * Represents the unique identifier for the Profile entity.
     * This field serves as the primary key for the database table
     * associated with the Profile entity. It is auto-generated
     * and cannot be updated. The value is mandatory for persistence.
     */
    @Id
    @GeneratedValue
    @Column(name = "profile_id", length = 50, columnDefinition = "number", updatable = false, nullable = false)
    private Long profileId;

    /**
     * Represents the first name of the profile entity.
     * This field is a mandatory attribute, with a maximum length of 50 characters.
     * It can be updated after creation and must be provided during insertion.
     */
    @Column(name = "first_name", length = 50, nullable = false, updatable = true, insertable = true)
    private String firstName;

    /**
     * Represents the middle name of the profile entity.
     * This field is an optional attribute, with a maximum length of 50 characters.
     * It can be updated after creation and can also be omitted during insertion.
     */
    @Column(length = 50, nullable = true, updatable = true, insertable = true)
    private String middleName;

    /**
     * Represents the last name of the profile entity.
     * This field is a mandatory attribute, with a maximum length of 50 characters.
     * It can be updated after creation and must be provided during insertion.
     */
    @Column(length = 50, nullable = false, updatable = true, insertable = true)
    private String lastName;

    /**
     * Represents the date of birth of the profile entity.
     * This field is stored as a date in the database table and is a mandatory attribute.
     * It can be updated post-creation and must be provided during insertion.
     */
    @Column(name = "date_of_birth", columnDefinition = "date", nullable = false, updatable = true, insertable = true)
    private LocalDate dateOfBirth;

    /**
     * Represents the user associated with this profile.
     * This field establishes a one-to-one relationship between the Profile and User entities.
     * The mapping is facilitated by a foreign key column named "user_id" in the database,
     * which cannot be null. This association ensures that each profile is tied to a single user.
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
