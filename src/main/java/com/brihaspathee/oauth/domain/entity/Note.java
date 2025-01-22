package com.brihaspathee.oauth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, January 2025
 * Time: 8:30 PM
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
public class Note {

    /**
     * Represents the unique identifier for the Note entity.
     * This field serves as the primary key for the database table
     * associated with the Note entity and is auto-generated.
     */
    @Id
    @GeneratedValue
    private Long noteId;

    /**
     * Represents the title of the note.
     * This field has a maximum length of 50 characters and is mandatory (cannot be null).
     * It is both insertable and updatable, allowing for modifications and additions.
     */
    @Column(length = 50, nullable = false, updatable = true, insertable = true)
    private String noteTitle;

    /**
     * Represents the content of the note.
     * This field is defined as a large object (LOB) to handle potentially large amounts of text data.
     * It is mandatory (cannot be null), and is stored as a CLOB (Character Large Object)
     * in the database with a maximum length of 1000 characters.
     * The field is both insertable and updatable, allowing modifications and additions.
     */
    @Lob
    @Column(length = 1000, columnDefinition = "clob", nullable = false, updatable = true, insertable = true)
    private String noteContent;

    /**
     * Represents the user associated with the note.
     * This is a mandatory many-to-one relationship, where each note is associated with exactly one user.
     * The "user_id" column in the database serves as the foreign key for this relationship.
     * The association ensures that the note entity is linked to the user entity, establishing ownership.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
