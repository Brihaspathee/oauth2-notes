package com.brihaspathee.oauth.service;

import com.brihaspathee.oauth.domain.entity.Role;
import com.brihaspathee.oauth.domain.entity.User;
import com.brihaspathee.oauth.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 1/22/25
 * Time: 6:20 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.service
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * The UserRepository is used to perform database operations related to the User entity.
     * This repository provides methods for retrieving user details, specifically finding a user by their email address.
     * It serves as a primary component for accessing user data in the underlying data source.
     */
    private final UserRepository userRepository;

    /**
     * Loads a user by their email address and returns the corresponding UserDetails instance.
     * This method is used to authenticate a user and retrieve their details such as roles and authorities.
     * If the user is not found in the database or the user's authentication method is not EMAIL, appropriate exceptions are thrown.
     *
     * @param email the email address of the user to be loaded
     * @return a UserDetails instance containing the user's information
     * @throws UsernameNotFoundException if no user is found with the provided email address
     * @throws IllegalArgumentException if the user's account is not registered with email/password authentication
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        /*
        * Find the user based on the email id provided in the request
        * If th user is not found then generate an exception
         */
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email " +email));
        user.getRoles().forEach(role -> log.info("Role:{}", role.getRoleName()));
        user.getAuthorities().forEach(authority ->
                log.info("Authority:{}", authority.getAuthority()));

        /*
        * If the authentication method of the user is not "EMAIL"
        * then generate an exception
         */
        if(!user.getAuthenticationMethod().equals("EMAIL")){
            throw new IllegalArgumentException("This account is not registered with email/password");
        }
        return user;
    }
}
