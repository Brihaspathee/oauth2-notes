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

    /**
     * A private, final instance of UserRepository used to handle
     * interactions with the user data store. This variable is
     * intended for managing, retrieving, and performing operations
     * related to user entities within the application. It ensures
     * consistent access to user-related data throughout the class.
     */
    private final UserRepository userRepository;

    /**
     * A repository interface for managing Role entities.
     * This variable provides the necessary methods to perform
     * CRUD operations and query executions related to roles.
     * It serves as a dependency for accessing and interacting
     * with the underlying persistence mechanism for role data.
     */
    private final RoleRepository roleRepository;

    /**
     * A repository responsible for handling authentication-related operations
     * with GitHub. This variable represents a dependency required for
     * managing authentication flows, such as token storage, retrieval,
     * and user validation against GitHub's API.
     */
    private final AuthGithubRepository authGithubRepository;

    /**
     * Loads the user details for OAuth2 authentication using the provided user request.
     * This method fetches user information from GitHub and manages the creation or retrieval
     * of a corresponding user entity in the local system.
     *
     * @param userRequest the OAuth2 user request containing the client and user information.
     * @return a {@code DefaultOAuth2User} containing the user's details, including attributes and authorities.
     * @throws OAuth2AuthenticationException if an authentication error occurs or user details cannot be loaded.
     * @throws RuntimeException if an email cannot be retrieved from GitHub or if the email is already
     *                          associated with another authentication method.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Loading user for registration");
        log.info("User request:{}", userRequest);
        /*
          Load user based on the user request
         */
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("Loaded user: {}", oAuth2User.getAttributes());
        /*
          Get all the github details of the user rom the attributes
         */
        String githubId = Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
        String email = oAuth2User.getAttribute("email");
        String githubLogin = oAuth2User.getAttribute("login");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        log.info("Github id: {}, email: {}, github login: {}, avatar url: {}", githubId, email, githubLogin, avatarUrl);
        /*
        * If email of the user was not returned from github then try ti fetch the emai
        * explicitly
        * */
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
            // Retrieve the user
            User existingUser = userRepository.findById(authGithub.getUserId()).orElse(null);
            if (existingUser != null) {
                // Get all the authorities of the user from the roles
                List<GrantedAuthority> authorities = existingUser.getRoles().stream()
                        .map(Role::getAuthorities)
                        .flatMap(Set::stream)
                        .map(authority ->
                                new SimpleGrantedAuthority(authority.getPermission()))
                        .collect(Collectors.toList());
                // Construct the default oauth2 user
                DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "id");
                log.info("Existing User Authorities: {}", defaultOAuth2User.getAuthorities());
                return defaultOAuth2User;
            }
        }

        // Check if email exists for another auth method
        User emailUser = userRepository.findUserByEmail(email).orElse(null);
        /*
        * If the email is used by another user in the system and Github is not the authentication
        * method of the user then generate an exception
        */
        if (emailUser != null && !emailUser.getAuthenticationMethod().equals("GITHUB")) {
            throw new RuntimeException("Email already used with email/password based authentication");
        }
         /*
         * Create new user
         * First get the role that needs to be assigned to the new user
         * The role for this user is "USER"
         */
        Role role = roleRepository.findById(2002L).orElseThrow(() -> new RuntimeException("Role not found"));
        User newUser = User.builder()
                .email(email)
                .authenticationMethod("GITHUB")
                .role(role)
                .build();
        /*
        * Save the new user
        */
        newUser = userRepository.save(newUser);
        AuthGithub newAuthGithub = AuthGithub.builder()
                .userId(newUser.getUserId())
                .githubId(githubId)
                .githubLogin(githubLogin)
                .githubAvatarUrl(avatarUrl)
                .build();
        authGithubRepository.save(newAuthGithub);
        /*
         * Get the authorities that were assigned to the user
         */
        List<GrantedAuthority> authorities = role.getAuthorities().stream()
                .map(authority ->
                        new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toList());
        /*
        * Create the default user based on the authorities assigned for the user
        */
        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "id");
        log.info("User Authorities: {}", defaultOAuth2User.getAuthorities());
        return defaultOAuth2User;
    }

    /**
     * Fetches the primary and verified email address associated with the GitHub user account.
     * Uses the GitHub API to retrieve the user's emails.
     *
     * @param userRequest The OAuth2UserRequest containing the access token required for authentication.
     * @return The primary and verified email address of the GitHub user, or null if no such email is found.
     */
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
