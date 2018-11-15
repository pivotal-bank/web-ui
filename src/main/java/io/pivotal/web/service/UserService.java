package io.pivotal.web.service;

import io.pivotal.web.domain.RegistrationRequest;
import io.pivotal.web.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RefreshScope
@Slf4j
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pivotal.userService.name}")
    private String userService;

    public void registerUser(RegistrationRequest registrationRequest) {
        log.debug("Creating user with userId: " + registrationRequest.getEmail());
        ResponseEntity<User> status = restTemplate.postForEntity("//" + userService + "/users/register/", registrationRequest, User.class);
        log.info("Status from registering account for " + registrationRequest.getEmail() + " is " + status);
    }

    public User getUser(String user) {
        log.debug("Looking for user with user name: " + user);
        User account = restTemplate.getForObject("http://" + userService + "/users/{user}", User.class, user);
        log.debug("Got user: " + account);
        return account;
    }

}
