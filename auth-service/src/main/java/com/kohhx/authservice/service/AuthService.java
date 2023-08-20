package com.kohhx.authservice.service;

import com.kohhx.authservice.entity.UserCredential;
import com.kohhx.authservice.jwt.JwtService;
import com.kohhx.authservice.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    @Autowired
    public AuthService(UserCredentialRepository userCredentialRepository, PasswordEncoder bCryptPasswordEncoder, JwtService jwtService) {
        this.userCredentialRepository = userCredentialRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }

    public String saveUser (UserCredential userCredential) {
        userCredential.setPassword(bCryptPasswordEncoder.encode(userCredential.getPassword()));
        userCredentialRepository.save(userCredential);
        return "Added user successfully";
    }

    public String generateToken(String userName) {
        return jwtService.generateToken(userName);
    }

    public void validateToken(String token) {
        try {
            jwtService.validateToken(token);
        } catch (Exception e) {
            throw new RuntimeException("Expired or invalid token");
        }
    }
}
