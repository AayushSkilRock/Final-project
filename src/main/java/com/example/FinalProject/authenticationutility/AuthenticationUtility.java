package com.example.FinalProject.authenticationutility;

import com.example.FinalProject.security.JwtUtil;
import com.example.FinalProject.usermodule.Entity.Role;
import com.example.FinalProject.usermodule.Entity.User;
import com.example.FinalProject.accountmodule.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
public class AuthenticationUtility {

    private final JwtUtil jwtUtil;
    private final AccountService accountService;

    @Autowired
    public AuthenticationUtility(JwtUtil jwtUtil, AccountService accountService) {
        this.jwtUtil = jwtUtil;
        this.accountService = accountService;
    }

    public ResponseEntity<?> validateTokenAndGetUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Auth token required");
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String username = jwtUtil.extractUsername(token);
            User user = accountService.findUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found for username: " + username);
            }

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error validating token or retrieving user: " + e.getMessage());
        }
    }
    public ResponseEntity<?> validateAdminUser(HttpServletRequest request) {
        ResponseEntity<?> authResponse = validateTokenAndGetUser(request);
        if (authResponse.getStatusCode().value() != 200) {
            return authResponse;
        }

        User user = (User) authResponse.getBody();


        if (Objects.nonNull(user) && !user.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).body("Access denied: Admin role required");
        }

        return ResponseEntity.ok(user);
    }

}
