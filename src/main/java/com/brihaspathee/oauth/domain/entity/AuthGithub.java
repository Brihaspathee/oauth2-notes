package com.brihaspathee.oauth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, January 2025
 * Time: 1:46 PM
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
@Table(name = "auth_github")
public class AuthGithub{

    @Id
    @GeneratedValue
    @Column(name = "auth_github_id")
    private Long authGithubId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "github_id")
    private String githubId;

    @Column(name = "github_login")
    private String githubLogin;

    @Column(name = "github_avatar_url")
    private String githubAvatarUrl;
}
