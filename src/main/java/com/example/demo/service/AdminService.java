package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.configuration.UserAuthenticationProvider;
import com.example.demo.dto.request.AdminLoginRequest;
import com.example.demo.dto.response.TokenResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Admin;
import com.example.demo.model.InvalidToken;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.InvalidTokenRepository;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminService {

    AdminRepository adminRepository;

    InvalidTokenRepository invalidTokenRepository;

    UserAuthenticationProvider userAuthenticationProvider;

    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getCurrentJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(String.valueOf(authentication));
        if (authentication != null && authentication.isAuthenticated()) {
            Object credentials = authentication.getCredentials();
            log.info((String) credentials);
            if (credentials != null) {
                return (String) credentials;
            }
        }
        return null;
    }


    public String getCurrentJti() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object credentials = authentication.getCredentials();
            if (credentials instanceof String) {
                String token = (String) credentials;
                DecodedJWT decodedJWT = JWT.decode(token);
                return decodedJWT.getId();  // Lấy giá trị jti từ token
            }
        }
        return null;
    }

    public Admin getAdmin() {
        var authentication = getCurrentAuthentication();

        if (authentication == null) {
            throw new IllegalArgumentException("No JWT found in Security Context");
        }

        Admin admin = (Admin) authentication.getPrincipal();
        String username = admin.getUsername();
        System.out.println(username);
        return adminRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public TokenResponse login(AdminLoginRequest adminLoginRequest) {
        var admin = adminRepository.findByUsername(adminLoginRequest.getUsername()).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        System.out.println("in login");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(adminLoginRequest.getPassword(), admin.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.WRONG_PASSWORD);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(userAuthenticationProvider.createToken(admin));

        return tokenResponse;
    }

    public void logout() throws ParseException, JOSEException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object credentials = authentication.getCredentials();
            if (credentials instanceof String) {
                String token = (String) credentials;
                DecodedJWT decodedJWT = JWT.decode(token);
                String id = decodedJWT.getId();
                Date expiry = decodedJWT.getExpiresAt();
                InvalidToken invalidToken = InvalidToken.builder()
                        .id(id)
                        .expiryTime(expiry)
                        .build();
                invalidTokenRepository.insert(invalidToken);
            }
        }
    }

    public void changePassword(String newPassword, String oldPassword) {
        if (newPassword.isEmpty()) {
            throw new AppException(ErrorCode.PASSWORD_BLANK);
        }
        String token = getCurrentJwtToken();
        Authentication authentication = userAuthenticationProvider.validateToken(token);
        if (authentication != null) {
            DecodedJWT jwt = userAuthenticationProvider.decodeToken(token);
            if (jwt != null) {
                Admin admin = adminRepository.findById(jwt.getSubject()).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                boolean authenticated = passwordEncoder.matches(oldPassword, admin.getPassword());

                if (!authenticated) {
                    throw new AppException(ErrorCode.OLD_PASS_INCORRECT);
                }

                admin.setPassword(passwordEncoder.encode(newPassword));
                admin.setUpdated_at(new Date());
                adminRepository.save(admin);
            } else throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }


}
