package com.tchorek.routes_collector.monitoring.controller;

import com.tchorek.routes_collector.database.model.Admin;
import com.tchorek.routes_collector.database.model.RegisteredUser;
import com.tchorek.routes_collector.encryption.Encryptor;
import com.tchorek.routes_collector.encryption.EncryptorProperties;
import com.tchorek.routes_collector.monitoring.service.LoginService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Controller
@CrossOrigin
public class LoginController {

    @Autowired
    LoginService loginService;

    @Autowired
    EncryptorProperties encryptorProperties;

    @ResponseBody
    @PostMapping(path = "/adminlogin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity loginAdmin(@RequestBody Admin credentials) {
        String decryptedLogin = decrypt(credentials.getLogin());
        String decryptedPassword = decrypt(credentials.getPassword());

        log.info("Login attempt for user {}", decryptedLogin);

        if (!loginService.isRegistered(decryptedLogin)) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }

        if (loginService.isAdminLogged(decryptedLogin, decryptedPassword))
            return ResponseEntity.ok(HttpStatus.IM_USED);

        try {
            return ResponseEntity.ok().body(loginService.adminLogin(decryptedLogin, decryptedPassword));
        } catch (Exception e) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity loginUser(@RequestBody RegisteredUser credentials) {
        String decryptedLogin = decrypt(credentials.getPhoneNumber());
        String decryptedPassword = decrypt(credentials.getPassword());

        log.info("Login attempt for user {}", decryptedLogin);

        if (!loginService.isRegistered(decryptedLogin)) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
        if (loginService.isUserLogged(decryptedLogin, decryptedPassword))
            return ResponseEntity.ok(HttpStatus.IM_USED);

        try {
            return ResponseEntity.ok().body(loginService.generateToken(decryptedLogin, decryptedPassword));
        } catch (Exception e) {
            return ResponseEntity.ok(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity logoutUser(@RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token)) {
            return ResponseEntity.ok(HttpStatus.FORBIDDEN);
        }
        loginService.logout(token);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(path = "/session")
    public ResponseEntity extendSession(@RequestParam(name = "token") String token) {
        if (!loginService.isTokenValid(token) && token != null && !token.isEmpty()) {
            return ResponseEntity.ok().body("");
        }

        log.info("Extending session for token: {}", token);
        loginService.extendUserSession(token);
        return ResponseEntity.ok().body(token);
    }

    private String decrypt(String data) {
        return Encryptor.decrypt(data, encryptorProperties.getKey(), encryptorProperties.getIv());
    }
}
