package com.brihaspathee.oauth.domain.repository;

import com.brihaspathee.oauth.domain.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 1/22/25
 * Time: 7:27 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.domain.repository
 * To change this template use File | Settings | File and Code Template
 */
public interface NoteRepository extends JpaRepository<Note, Long> {
}
