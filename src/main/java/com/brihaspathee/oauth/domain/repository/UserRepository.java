package com.brihaspathee.oauth.domain.repository;

import com.brihaspathee.oauth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, January 2025
 * Time: 8:35 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.domain.repository
 * To change this template use File | Settings | File and Code Template
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user entity based on the provided email address.
     *
     * @param email the email address of the user to retrieve
     * @return an Optional containing the user if found, or an empty Optional if no user exists with the given email
     */
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByGithubId(String githubId);
}
