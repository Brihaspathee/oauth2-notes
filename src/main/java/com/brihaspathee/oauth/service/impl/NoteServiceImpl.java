package com.brihaspathee.oauth.service.impl;

import com.brihaspathee.oauth.domain.repository.NoteRepository;
import com.brihaspathee.oauth.models.NoteDto;
import com.brihaspathee.oauth.service.interfaces.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 1/22/25
 * Time: 7:24 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.service.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Override
    public NoteDto save(NoteDto noteDto) {
        return null;
    }

    @Override
    public List<NoteDto> getNotes(String userEmail) {
        return List.of();
    }
}
