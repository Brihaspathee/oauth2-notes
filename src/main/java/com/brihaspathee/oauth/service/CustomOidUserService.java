package com.brihaspathee.oauth.service;

import com.brihaspathee.oauth.domain.entity.AuthGithub;
import com.brihaspathee.oauth.domain.entity.AuthGoogle;
import com.brihaspathee.oauth.domain.entity.Role;
import com.brihaspathee.oauth.domain.entity.User;
import com.brihaspathee.oauth.domain.repository.AuthGoogleRepository;
import com.brihaspathee.oauth.domain.repository.RoleRepository;
import com.brihaspathee.oauth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, January 2025
 * Time: 6:09 AM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.service
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidUserService extends OidcUserService {

    /**
     * Represents a repository for managing user-related data operations.
     * This variable is a final instance of {@link UserRepository}, ensuring
     * that it cannot be reassigned after initialization.
     * It is used for performing various operations such as retrieving,
     * saving, updating, or deleting user data in the application.
     */
    private final UserRepository userRepository;

    /**
     * Repository for accessing and managing role-related data.
     * This variable serves as the interface to the underlying data store
     * for operations such as retrieving, saving, and updating roles.
     * It abstracts the data access layer to enable easier interaction with role entities.
     */
    private final RoleRepository roleRepository;

    /**
     * The authGoogleRepository is a private final instance of AuthGoogleRepository.
     * It is used to handle Google authentication-related operations and interactions
     * with the associated repository layer. This instance is immutable and ensures
     * consistent access to authentication methods provided by AuthGoogleRepository.
     */
    private final AuthGoogleRepository authGoogleRepository;

    /**
     * Loads the user by processing the {@code OidcUserRequest} and creates an {@code OidcUser}
     * instance either for an existing user or a new user based on the request. It also handles
     * user role assignment and authentication details.
     *
     * @param userRequest the OpenID Connect user request containing user information and tokens
     *        provided by the identity provider
     * @return an {@code OidcUser} instance representing the authenticated user, complete with
     *         roles, permissions, and user information
     * @throws OAuth2AuthenticationException if an error occurs during the authentication process
     *        or if the email is already used with another authentication method
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException{
        log.info("Inside CustomOidUserService");
        log.info("User request:{}", userRequest);
        /*
        * Load the OIDC user based on the user request
         */
        OidcUser oidcUser = super.loadUser(userRequest);
        log.info("oidcUser:{}", oidcUser);
        log.info("User email:{}", oidcUser.getEmail());
        log.info("User name:{}", oidcUser.getName());
        log.info("User attributes:{}", oidcUser.getAttributes());
        log.info("User idToken:{}", oidcUser.getIdToken().getTokenValue());
        log.info("User userInfo:{}", oidcUser.getUserInfo());
        log.info("User Full Name:{}", oidcUser.getAttributes().get("name"));
        log.info("User First Name:{}", oidcUser.getAttributes().get("given_name"));
        log.info("User Last Name:{}", oidcUser.getAttributes().get("family_name"));
        log.info("User picture:{}", oidcUser.getAttributes().get("picture"));

        /*
         * Get the user profile data from the user data retrieved from Google
         */
        String googleId = oidcUser.getName();
        String email = oidcUser.getEmail();
        String googlePicture = oidcUser.getAttributes().get("picture").toString();
        String name = oidcUser.getAttributes().get("name").toString();

        // check if the user is an existing user
        AuthGoogle authGoogle = authGoogleRepository.findByGoogleId(googleId).orElse(null);
        if(authGoogle != null){
            /*
            * If the user already exist, get the user information from the DB
             */
            User existingUser = userRepository.findById(authGoogle.getUserId()).orElse(null);
            if (existingUser != null) {
                /*
                * Get the authorities of the user
                 */
                List<GrantedAuthority> authorities = existingUser.getRoles().stream()
                        .map(Role::getAuthorities)
                        .flatMap(Set::stream)
                        .map(authority ->
                                new SimpleGrantedAuthority(authority.getPermission()))
                        .collect(Collectors.toList());
                /*
                * Construct the OIDC User info
                 */
                OidcUserInfo oidcUserInfo = getOidcUserInfo(email, name, oidcUser, googlePicture);
                /*
                * Construct the default OIDC user
                 */
                DefaultOidcUser defaultOidcUser = new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUserInfo);
                log.info("Existing User Authorities: {}", defaultOidcUser.getAuthorities());
                return defaultOidcUser;
            }
        }

        /*
        * If the user has not authenticated using Google, check if the email is already used
         */
        User emailUser = userRepository.findUserByEmail(email).orElse(null);
        /*
        * If the email is already used by another authentication method then generate an exception
         */
        if (emailUser != null && !emailUser.getAuthenticationMethod().equals("GOOGLE")) {
            throw new RuntimeException("Email already used with email/password based authentication");
        }
        /*
        * Create new user
        * First get the role that needs to be assigned to the new user
        * The role for these users is "USER"
         */
        Role role = roleRepository.findById(2002L).orElseThrow(() -> new RuntimeException("Role not found"));
        User newUser = User.builder()
                .email(email)
                .authenticationMethod("GOOGLE")
                .role(role)
                .build();
        /*
        * Save the user
         */
        newUser = userRepository.save(newUser);
        AuthGoogle newAuthGoogle = AuthGoogle.builder()
                .userId(newUser.getUserId())
                .googleId(googleId)
                .googleName(name)
                .googlePictureUrl(googlePicture)
                .build();
        authGoogleRepository.save(newAuthGoogle);
        /*
        * Get all the authorities of the user and construct the default OIDC user
         */
        List<GrantedAuthority> authorities = role.getAuthorities().stream()
                .map(authority ->
                        new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toList());
        OidcUserInfo oidcUserInfo = getOidcUserInfo(email, name, oidcUser, googlePicture);
        DefaultOidcUser defaultOidcUser = new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUserInfo);
        log.info("Existing User Authorities: {}", defaultOidcUser.getAuthorities());
        return defaultOidcUser;
    }

    /**
     * Constructs an OidcUserInfo object containing user information claims such as email, name,
     * given name, family name, and picture extracted from the provided inputs.
     *
     * @param email the email address of the user
     * @param name the full name of the user
     * @param oidcUser the source OIDC user object containing additional attributes
     * @param googlePicture the URL of the user's profile picture
     * @return an OidcUserInfo object populated with the specified claims
     */
    private static OidcUserInfo getOidcUserInfo(String email, String name, OidcUser oidcUser, String googlePicture) {
        Map<String, Object> userInfoClaims = new HashMap<>();
        userInfoClaims.put("email", email);
        userInfoClaims.put("name", name);
        userInfoClaims.put("given_name", oidcUser.getAttributes().get("given_name"));
        userInfoClaims.put("family_name", oidcUser.getAttributes().get("family_name"));
        userInfoClaims.put("picture", googlePicture);
        OidcUserInfo oidcUserInfo = OidcUserInfo.builder()
                .claims(claims -> claims.putAll(userInfoClaims))
                .build();
        return oidcUserInfo;
    }
}
