package com.tchorek.routes_collector.encryption;

import com.tchorek.routes_collector.database.model.Admin;
import com.tchorek.routes_collector.database.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Component
public class PasswordHashing {

    @Value("${PASS}")
    private String adminPassword;

    @Value("${LOGIN}")
    private String adminLogin;

    @Autowired
    AdminRepository adminRepository;

    @PostConstruct
    public void init() {
        adminRepository.save(new Admin(adminLogin, BCrypt.hashpw(adminPassword, BCrypt.gensalt(10)), null, null));
    }

    public boolean checkPassword(String login, String password){
        Optional<Admin> credentials = adminRepository.findById(login);
        return credentials.isPresent() && BCrypt.checkpw(password, credentials.get().getPassword());
    }
}
