package com.kohhx.authservice.controller;

import com.kohhx.authservice.DTO.MessageDTO;
import com.kohhx.authservice.DTO.TokenResponseDTO;
import com.kohhx.authservice.entity.UserCredential;
import com.kohhx.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageDTO> addNewUser(@RequestBody UserCredential userCredential) {
        String message =  authService.saveUser(userCredential);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.CREATED);
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDTO> getToken(@RequestBody UserCredential userCredential){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredential.getUsername(), userCredential.getPassword()));
        if (authenticate.isAuthenticated()) {
            String token = authService.generateToken(userCredential.getUsername());
            return new ResponseEntity<>(new TokenResponseDTO(token), HttpStatus.OK);
        } else {
            throw new RuntimeException("Authentication failed");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<MessageDTO> validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return  new ResponseEntity<>(new MessageDTO("Token is valid"), HttpStatus.OK);
    }

}
