package com.brihaspathee.oauth.service;

import com.brihaspathee.oauth.domain.entity.AuthGithub;
import com.brihaspathee.oauth.domain.entity.Role;
import com.brihaspathee.oauth.domain.entity.User;
import com.brihaspathee.oauth.domain.repository.AuthGithubRepository;
import com.brihaspathee.oauth.domain.repository.RoleRepository;
import com.brihaspathee.oauth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

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

    private final RoleRepository roleRepository;

    private final AuthGithubRepository authGithubRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Loading user for registration");
        log.info("User request:{}", userRequest);
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("Loaded user: {}", oAuth2User.getAttributes());
        String githubId = Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
        String email = oAuth2User.getAttribute("email");
        String githubLogin = oAuth2User.getAttribute("login");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        log.info("Github id: {}, email: {}, github login: {}, avatar url: {}", githubId, email, githubLogin, avatarUrl);
        if (email == null) {
            email = fetchEmailFromGitHub(userRequest);
            log.info("Fetched email from github: {}", email);
            if (email == null) {
                throw new RuntimeException("Email not found from github");
            }
        }

        // check if the user is an existing user
        AuthGithub authGithub = authGithubRepository.findByGithubId(githubId).orElse(null);
        if(authGithub != null){
            User existingUser = userRepository.findById(authGithub.getUserId()).orElse(null);
            if (existingUser != null) {
                List<GrantedAuthority> authorities = existingUser.getRoles().stream()
                        .map(Role::getAuthorities)
                        .flatMap(Set::stream)
                        .map(authority ->
                                new SimpleGrantedAuthority(authority.getPermission()))
                        .collect(Collectors.toList());
                DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "id");
                log.info("Existing User Authorities: {}", defaultOAuth2User.getAuthorities());
                return defaultOAuth2User;
            }
        }

        // Check if email exists for another auth method
        User emailUser = userRepository.findUserByEmail(email).orElse(null);
        if (emailUser != null && !emailUser.getAuthenticationMethod().equals("GITHUB")) {
            throw new RuntimeException("Email already used with email/password based authentication");
        }
        // Create new user
        // First get the role that needs to be assigned to the new user
        Role role = roleRepository.findById(2002L).orElseThrow(() -> new RuntimeException("Role not found"));
        User newUser = User.builder()
                .email(email)
                .authenticationMethod("GITHUB")
                .role(role)
                .build();
        newUser = userRepository.save(newUser);
        AuthGithub newAuthGithub = AuthGithub.builder()
                .userId(newUser.getUserId())
                .githubId(githubId)
                .githubLogin(githubLogin)
                .githubAvatarUrl(avatarUrl)
                .build();
        authGithubRepository.save(newAuthGithub);
        List<GrantedAuthority> authorities = role.getAuthorities().stream()
                .map(authority ->
                        new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toList());
        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "id");
        log.info("User Authorities: {}", defaultOAuth2User.getAuthorities());
        return defaultOAuth2User;
    }

    private String fetchEmailFromGitHub(OAuth2UserRequest userRequest) {
        String token = userRequest.getAccessToken().getTokenValue();
        String emailApiUrl = "https://api.github.com/user/emails";

        RestTemplate restTemplate = new RestTemplate();
        var headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        var entity = new org.springframework.http.HttpEntity<>(headers);
        var response = restTemplate.exchange(
                emailApiUrl,
                org.springframework.http.HttpMethod.GET,
                entity,
                org.springframework.core.ParameterizedTypeReference.forType(List.class)
        );

        List<Map<String, Object>> emails = (List<Map<String, Object>>) response.getBody();
        if (emails != null) {
            for (Map<String, Object> emailEntry : emails) {
                if (Boolean.TRUE.equals(emailEntry.get("primary")) &&
                        Boolean.TRUE.equals(emailEntry.get("verified"))) {
                    return (String) emailEntry.get("email");
                }
            }
        }

        return null;
    }
}
