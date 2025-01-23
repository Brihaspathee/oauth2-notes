package com.brihaspathee.oauth.service;

import com.brihaspathee.oauth.domain.entity.User;
import com.brihaspathee.oauth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 1/22/25
 * Time: 6:25 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.service
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String githubId = oAuth2User.getAttribute("id");
        String email = oAuth2User.getAttribute("email");
        String githubLogin = oAuth2User.getAttribute("login");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");

        // check if the user is an existing user
        User existingUser = userRepository.findUserByGithubId(githubId).orElse(null);
        if (existingUser != null) {
            return oAuth2User;
        }
        // Check if email exists for another auth method
        User emailUser = userRepository.findUserByEmail(email).orElse(null);
        if (emailUser != null && !emailUser.getAuthenticationMethod().equals("GITHUB")) {
            throw new RuntimeException("Email already used with email/password based authentication");
        }
        // Create new user
        User newUser = User.builder()
                .email(email)
                .githubId(githubId)
                .githubLogin(githubLogin)
                .githubAvatarUrl(avatarUrl)
                .authenticationMethod("GITHUB")
                .build();
        userRepository.save(newUser);
        return oAuth2User;
    }
}
