package com.brihaspathee.oauth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 24, January 2025
 * Time: 2:59 PM
 * Project: oauth2-notes
 * Package Name: com.brihaspathee.oauth.controller
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@RestController
@RequestMapping("/login/oauth2/code/github")
public class RedirectController {

    @GetMapping
    public void handleCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Step 1: Extract parameters from the callback request
        String authorizationCode = request.getParameter("code");
        String state = request.getParameter("state"); // Contains either the original URL or "postman"

        if (authorizationCode == null || state == null) {
            throw new RuntimeException("Missing authorization code or state parameter");
        }

        // Step 2: Exchange authorization code for access token
        String accessToken = fetchAccessToken(authorizationCode);

        // Step 3: Handle Postman callback
        if ("postman".equals(state)) {
            // Redirect back to Postman’s callback URL
            response.sendRedirect("https://oauth.pstmn.io/v1/callback?access_token=" + accessToken);
            return;
        }

        // Step 4: Validate the state (for security and to handle application-specific requests)
        if (!state.startsWith("http://localhost:8080")) { // Update this to match your application's domain
            throw new SecurityException("Invalid redirect URL in state parameter");
        }

        // Step 5: Store the access token (optional)
        // Store the access token in a session or database for later use
        request.getSession().setAttribute("githubAccessToken", accessToken);

        // Step 6: Redirect the user to their originally requested URL
        response.sendRedirect(state);
    }

    // Helper Method: Fetch access token from GitHub
    private String fetchAccessToken(String authorizationCode) {
        // Use RestTemplate to make a POST request to GitHub's token endpoint
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("client_id", "your_client_id");
        tokenRequest.put("client_secret", "your_client_secret");
        tokenRequest.put("code", authorizationCode);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(tokenRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://github.com/login/oauth/access_token", entity, Map.class
            );

            // Parse the response to extract the access token
            Map<String, String> responseBody = response.getBody();
            if (responseBody != null) {
                return responseBody.get("access_token");
            } else {
                throw new RuntimeException("Failed to retrieve access token");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error while fetching access token: " + ex.getMessage(), ex);
        }
    }

}
