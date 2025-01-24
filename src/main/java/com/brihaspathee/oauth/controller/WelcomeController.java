package com.brihaspathee.oauth.controller;

import com.brihaspathee.oauth.permissions.AuthorityCreatePermission;
import com.brihaspathee.oauth.permissions.NoteCreatePermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 22, January 2025
 * Time: 1:28 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.controller
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/oauth2/welcome")
public class WelcomeController {

    /**
     * Handles HTTP GET requests to provide a welcome message.
     *
     * @return ResponseEntity containing a success response with a welcome message.
     */
    @GetMapping
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to Oauth2 Notes");
    }

    /**
     * Handles HTTP GET requests to provide a secured welcome message.
     *
     * @return ResponseEntity containing a success response with a secured welcome message.
     */
    @GetMapping("/secured")
    @AuthorityCreatePermission
    public ResponseEntity<String> securedWelcome() {
        return ResponseEntity.ok("Welcome to Oauth2 Secured Notes");
    }

    @GetMapping("/create-note")
    @NoteCreatePermission
    public ResponseEntity<String> securedNoteCreation(Authentication authentication) {
        log.info("Welcome to creating notes securely");
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails){
            log.info("User Details: {}", userDetails.getUsername());
            return ResponseEntity.ok("Password based user - Welcome to creating notes securely");
        }else if (principal instanceof OAuth2User oAuth2User){
            log.info("OAuth2 User: {}", oAuth2User.getAttributes());
            return ResponseEntity.ok("OAuth2 User - Welcome to creating notes securely");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unknown user type");
        }
    }
}
