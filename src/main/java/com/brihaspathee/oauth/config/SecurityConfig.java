package com.brihaspathee.oauth.config;

import com.brihaspathee.oauth.service.CustomOAuth2Service;
import com.brihaspathee.oauth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashSet;
import java.util.Set;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 1/22/25
 * Time: 7:06 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.config
 * To change this template use File | Settings | File and Code Template
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * A collection of endpoint URIs that are explicitly whitelisted and do not require authentication.
     * These endpoints are publicly accessible and serve specific purposes, such as the OAuth2 welcome
     * page and the OAuth2 callback URLs.
     *
     * This whitelisted array is commonly used in security configurations to bypass authentication
     * filters or to set specific access rules for the listed endpoints.
     */
    private static final String[] AUTH_WHITELIST = {
            "/api/v1/oauth2/welcome"
    };

    /**
     * An instance of {@link CustomUserDetailsService} used to provide custom user detail retrieval for
     * authentication processes. This service is primarily responsible for loading user-specific
     * data in the context of authentication and is typically invoked by the security framework.
     * It ensures user authorization by validating and fetching user-related details like roles
     * and authorities from the underlying data source.
     */
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * A reference to a custom implementation of an {@link OAuth2Service} used to handle
     * OAuth2 authentication workflows within the application.
     *
     * This service is likely designed to process OAuth2 user authentication and data management,
     * providing the necessary integration and customization for third-party providers. It is
     * used to enhance or replace default OAuth2 handling mechanisms with application-specific logic.
     */
    private final CustomOAuth2Service  customOAuth2Service;

    /**
     * Configures and provides an {@link AuthenticationManager} bean for the application.
     *
     * This method retrieves and returns the {@link AuthenticationManager} instance from the provided
     * {@link AuthenticationConfiguration} object. The {@link AuthenticationManager} is essential for
     * handling authentication in the application's security context.
     *
     * @param authenticationConfiguration the {@link AuthenticationConfiguration} object used to obtain
     *                                     the {@link AuthenticationManager} instance.
     * @return the configured {@link AuthenticationManager} for the application.
     * @throws Exception if an error occurs while retrieving the {@link AuthenticationManager}.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration
                                                                   authenticationConfiguration) throws  Exception {
        return authenticationConfiguration.getAuthenticationManager();

    }

    /**
     * Configures and provides an {@link AuthenticationProvider} bean for the application.
     *
     * This method initializes a {@link DaoAuthenticationProvider} with a custom*/
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * This method defines the security configuration by:
     * - Permitting access to certain whitelisted endpoints.
     * - Requiring authentication for all other requests.
     * - Setting up authentication providers and disabling CSRF protection.
     * - Enabling HTTP basic authentication.
     * - Configuring session management policy.
     * - Enabling form login and OAuth2 login with a custom user service.
     *
     * @param http the {@link HttpSecurity} object used to configure HTTP security settings.
     * @return the configured {@link SecurityFilterChain} for the application.
     * @throws Exception if an error occurs while configuring the security settings.
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .csrf(AbstractHttpConfigurer::disable)  // .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sessionManagement ->
                {
                    try {
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .and().securityContext().securityContextRepository(new HttpSessionSecurityContextRepository());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .formLogin(Customizer.withDefaults())
                .oauth2Login(oauth2 ->
                        oauth2.userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2Service)));
        return http.build();

    }

    /**
     * Creates and returns a {@link PasswordEncoder} bean for encoding passwords
     * using the BCrypt hashing algorithm.
     *
     * @return a {@link PasswordEncoder} instance configured with the BCrypt algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures a custom {@link OAuth2UserService} bean to handle OAuth2 and OIDC authentication.
     *
     * This method sets up a service to process authenticated users from third-party providers such
     * as Google and GitHub. For Google, which uses OpenID Connect (OIDC), it leverages an
     * {@link OidcUserService}. For other OAuth2 providers such as GitHub, it uses a
     * {@link DefaultOAuth2UserService}.
     *
     * The returned service determines the provider based on the client registration ID and processes
     * the user data accordingly. For non-OIDC providers, it assigns an additional authority
     * ("note.create") to the user's granted authorities.
     *
     * @return a configured {@link OAuth2UserService}, capable of handling both
     *         OIDC and OAuth2 user requests based on the provider type.
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
        /*
        * The CustomOAuth2Service will be used to load the OAuth2 user
        * The Github OAuth2 service is used in this application
        * */
        DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

        /*
        * The CustomOIDCUserService will be used to load the OIDC user
        * Google uses OpenIDConnect to authorize the users
        * */
        OidcUserService oidcUserService = new OidcUserService();

        return userRequest -> {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();

            // Check if this is an OIDC (OpenID Connect) provider
            if ("google".equals(registrationId)) {
                if (userRequest instanceof OidcUserRequest oidcUserRequest) {
                    OidcUser oidcUser = oidcUserService.loadUser(oidcUserRequest);
                    return oidcUser;
                } else {
                    throw new IllegalArgumentException("Expected OidcUserRequest for Google, but got OAuth2UserRequest.");
                }
            }

            // Handle non-OIDC providers (e.g., GitHub)
            OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
            Set<GrantedAuthority> authorities = new HashSet<>(oAuth2User.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("note.create"));

            return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "id");
        };
    }

    /**
     * Configures and provides a {@link CorsConfigurationSource} bean to define Cross-Origin Resource
     * Sharing (CORS) settings for the application.
     *
     * This method sets up a {@link CorsConfiguration} with the following configuration:
     * - Allows requests from the origin "https://oauth.pstmn.io".
     * - Permits all HTTP methods.
     * - Permits all HTTP headers.
     * - Enables the use of credentials.
     * The {@link CorsConfiguration} is then registered to apply to all endpoints.
     *
     * @return a configured {@link CorsConfigurationSource} instance for handling CORS requests.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("https://oauth.pstmn.io");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
