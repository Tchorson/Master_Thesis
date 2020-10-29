package com.tchorek.routes_collector.monitoring.service;

import com.tchorek.routes_collector.database.repositories.AdminRepository;
import com.tchorek.routes_collector.encryption.PasswordHashing;
import com.tchorek.routes_collector.utils.Timer;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Setter
@Service
public class LoginService {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    PasswordHashing passwordHashing;

    public String login(String login, String password){
        String token = RandomStringUtils.randomAlphanumeric(30);
        if (passwordHashing.checkPassword(login, password)){
            adminRepository.createUserSession(login, token, Timer.getCurrentTimeInSeconds());
            return token;
        }
        return null;
    }

    public void logout(String token){
        adminRepository.removeUserSession(token);
    }

    public boolean isLogged(String login, String password){
        return adminRepository.getUserToken(login, password) != null && adminRepository.getUserToken(login, password).length() > 0;
    }

    public boolean isTokenValid(String token){
        String test = adminRepository.findUserSession(token);
        return test != null && !test.isEmpty() && !test.isBlank();
    }

    public boolean isRegistered(String login){
        return adminRepository.findById(login).isPresent();
    }

    public void extendUserSession(String token) {
        adminRepository.updateUserSessionTimestamp(Timer.getCurrentTimeInSeconds(), token);
    }
}
