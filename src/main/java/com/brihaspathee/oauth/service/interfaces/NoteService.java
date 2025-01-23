package com.brihaspathee.oauth.service.interfaces;

import com.brihaspathee.oauth.models.NoteDto;

import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 1/22/25
 * Time: 7:23 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.service.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface NoteService {

    NoteDto save(NoteDto noteDto);

    List<NoteDto> getNotes(String userEmail);
}
