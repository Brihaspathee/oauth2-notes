package com.brihaspathee.oauth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, January 2025
 * Time: 1:47 PM
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
@Table(name = "auth_google")
public class AuthGoogle{

    @Id
    @GeneratedValue
    @Column(name = "auth_google_id")
    private Long authGoogleId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "google_name")
    private String googleName;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "google_picture_url")
    private String googlePictureUrl;
}
