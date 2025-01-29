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

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AuthGoogleRepository authGoogleRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException{
        log.info("Inside CustomOidUserService");
        log.info("User request:{}", userRequest);
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

        String googleId = oidcUser.getName();
        String email = oidcUser.getEmail();
        String googlePicture = oidcUser.getAttributes().get("picture").toString();
        String name = oidcUser.getAttributes().get("name").toString();

        // check if the user is an existing user
        AuthGoogle authGoogle = authGoogleRepository.findByGoogleId(googleId).orElse(null);
        if(authGoogle != null){
            User existingUser = userRepository.findById(authGoogle.getUserId()).orElse(null);
            if (existingUser != null) {
                List<GrantedAuthority> authorities = existingUser.getRoles().stream()
                        .map(Role::getAuthorities)
                        .flatMap(Set::stream)
                        .map(authority ->
                                new SimpleGrantedAuthority(authority.getPermission()))
                        .collect(Collectors.toList());
                OidcUserInfo oidcUserInfo = getOidcUserInfo(email, name, oidcUser, googlePicture);
                DefaultOidcUser defaultOidcUser = new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUserInfo);
                log.info("Existing User Authorities: {}", defaultOidcUser.getAuthorities());
                return defaultOidcUser;
            }
        }

        // Check if email exists for another auth method
        User emailUser = userRepository.findUserByEmail(email).orElse(null);
        if (emailUser != null && !emailUser.getAuthenticationMethod().equals("GOOGLE")) {
            throw new RuntimeException("Email already used with email/password based authentication");
        }
        // Create new user
        // First get the role that needs to be assigned to the new user
        Role role = roleRepository.findById(2002L).orElseThrow(() -> new RuntimeException("Role not found"));
        User newUser = User.builder()
                .email(email)
                .authenticationMethod("GOOGLE")
                .role(role)
                .build();
        newUser = userRepository.save(newUser);
        AuthGoogle newAuthGoogle = AuthGoogle.builder()
                .userId(newUser.getUserId())
                .googleId(googleId)
                .googleName(name)
                .googlePictureUrl(googlePicture)
                .build();
        authGoogleRepository.save(newAuthGoogle);
        List<GrantedAuthority> authorities = role.getAuthorities().stream()
                .map(authority ->
                        new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toList());
        OidcUserInfo oidcUserInfo = getOidcUserInfo(email, name, oidcUser, googlePicture);
        DefaultOidcUser defaultOidcUser = new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUserInfo);
        log.info("Existing User Authorities: {}", defaultOidcUser.getAuthorities());
        return defaultOidcUser;
    }

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
