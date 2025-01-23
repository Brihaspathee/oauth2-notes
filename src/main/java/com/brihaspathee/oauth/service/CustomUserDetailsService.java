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

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email " +email));
        user.getRoles().forEach(role -> log.info("Role:{}", role.getRoleName()));
        user.getAuthorities().forEach(authority ->
                log.info("Authority:{}", authority.getAuthority()));

        if(!user.getAuthenticationMethod().equals("EMAIL")){
            throw new IllegalArgumentException("This account is not registered with email/password");
        }
        return user;
    }
}
