package com.brihaspathee.oauth.domain.repository;

import com.brihaspathee.oauth.domain.entity.AuthGoogle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, January 2025
 * Time: 1:59 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.domain.repository
 * To change this template use File | Settings | File and Code Template
 */
public interface AuthGoogleRepository extends JpaRepository<AuthGoogle, Long> {

    Optional<AuthGoogle> findByGoogleId(String googleId);
}
