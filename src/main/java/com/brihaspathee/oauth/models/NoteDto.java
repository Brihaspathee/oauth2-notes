package com.brihaspathee.oauth.models;

import lombok.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 1/22/25
 * Time: 7:24 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.models
 * To change this template use File | Settings | File and Code Template
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteDto {

    private Long noteId;

    private String noteTitle;

    private String noteContent;
}
