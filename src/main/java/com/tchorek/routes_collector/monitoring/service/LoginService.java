package com.tchorek.routes_collector.monitoring.service;

import com.tchorek.routes_collector.database.repositories.AdminRepository;
import com.tchorek.routes_collector.database.repositories.UserRegistrationRepository;
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

    @Autowired
    UserRegistrationRepository userRegistrationRepository;

    public String adminLogin(String login, String password){
        String token = RandomStringUtils.randomAlphanumeric(30);
        if (passwordHashing.checkAdminPassword(login, password)){
            adminRepository.createUserSession(login, token, Timer.getCurrentTimeInSeconds());
            return token;
        }
        return null;
    }

    public String generateToken (String login, String password){
        String token = RandomStringUtils.randomAlphanumeric(30);
        if (passwordHashing.checkUserPassword(login, password)){
            userRegistrationRepository.createUserSession(login, token, Timer.getCurrentTimeInSeconds());
            return token;
        }
        return null;
    }

    public void logout(String token){
        adminRepository.removeUserSession(token);
        userRegistrationRepository.removeUserSession(token);
    }

    public boolean isAdminLogged(String login, String password){
        return adminRepository.getUserToken(login, password) != null && adminRepository.getUserToken(login, password).length() > 0;
    }

    public boolean isUserLogged(String login, String password){
        return userRegistrationRepository.getUserToken(login, password) != null && adminRepository.getUserToken(login, password).length() > 0;
    }

    public boolean isTokenValid(String token){
        String test = adminRepository.findUserSession(token);
        String test2 = userRegistrationRepository.findUserSession(token);
        return  test != null && !test.isEmpty() && !test.isBlank() || test2 != null && !test2.isEmpty() && !test2.isBlank();
    }

    public boolean isRegistered(String login){
        return adminRepository.findById(login).isPresent() || userRegistrationRepository.findById(login).isPresent();
    }

    public void extendUserSession(String token) {
        adminRepository.updateUserSessionTimestamp(Timer.getCurrentTimeInSeconds(), token);
        userRegistrationRepository.updateUserSessionTimestamp(Timer.getCurrentTimeInSeconds(), token);
    }
}
