package com.brihaspathee.oauth.controller;

import com.brihaspathee.oauth.models.NoteDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 23, January 2025
 * Time: 8:19 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.controller
 * To change this template use File | Settings | File and Code Template
 */
@RestController
@RequestMapping("/api/v1/oauth2/notes")
public class NotesController {

    @PostMapping
    public ResponseEntity<String> saveNote(@RequestBody NoteDto noteDto) {
        return ResponseEntity.ok("Note Saved Successfully");
    }
}
