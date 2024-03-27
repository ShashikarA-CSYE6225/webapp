package com.csye6225.webapp.controller;

import com.csye6225.webapp.dto.UserResponseDto;
import com.csye6225.webapp.exception.*;
import com.csye6225.webapp.model.User;
import com.csye6225.webapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/v1/user")
@Slf4j
public class UserController {

    UserService userService;

    HttpHeaders headers;

    public UserController(UserService userService) {
        this.userService = userService;
        this.headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());
        headers.setPragma("no-cache");
        headers.add("X-Content-Type-Options", "nosniff");
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user, @RequestHeader(value = "Authorization", required = false) String auth) throws UsernameAlreadyExistsException, UserNotUpdatedException, IOException, ExecutionException, InterruptedException {
        UserResponseDto userResponse = userService.createUser(user, auth);
        log.info("User Creation Request successful with status code - 201 for user: " + userResponse.getId());
        log.debug("User Request Payload: " + user);
        return ResponseEntity
                  .status(HttpStatus.CREATED)
                  .headers(headers)
                  .body(userResponse);
    }

    @GetMapping("/verify")
    public ResponseEntity<Object> verifyUser(@RequestParam("token") String token, @RequestHeader(value = "Testing", required = false) String skipTests) throws UserNotVerifiedException {
        String isVerified = userService.verifyUser(token, skipTests);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(Collections.singletonMap("Verification Status", isVerified));
    }

    @GetMapping("/self")
    public ResponseEntity<Object> getUser(@RequestBody(required = false) User requestBody, @RequestHeader("Authorization") String basicAuth) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException, UserNotVerifiedException {
        UserResponseDto userResponse = userService.getUser(requestBody, basicAuth);
        log.info("User GET Request successful with status code - 200 for user: " + userResponse.getId());
        log.debug("User Response Payload: " + userResponse);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(userResponse);
    }

    @GetMapping("/all")
    public List<UserResponseDto> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @PutMapping("self")
    public ResponseEntity<Object> updateUser(@RequestHeader("Authorization") String basicAuth, @RequestBody User requestBody) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException, UserNotUpdatedException, UserNotVerifiedException {
        UserResponseDto userResponse = userService.updateUser(basicAuth, requestBody);
        log.info("User Update Request successful with status code - 204 for user: " + userResponse.getId());
        log.debug("User Request Payload: " + requestBody);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .headers(headers)
                .build();
    }

    @RequestMapping(path = {"","/self"}, method = {RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> handleSelfHeadAndOptionsMethods() {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(headers)
                .build();
    }
}
