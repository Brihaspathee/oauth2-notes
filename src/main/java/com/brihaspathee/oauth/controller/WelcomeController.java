package com.brihaspathee.oauth.controller;

import com.brihaspathee.oauth.permissions.AuthorityCreatePermission;
import com.brihaspathee.oauth.permissions.NoteCreatePermission;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> securedNoteCreation() {
        return ResponseEntity.ok("Welcome to creating notes securely");
    }
}
