package com.csye6225.webapp.controller;

import com.csye6225.webapp.dto.UserResponseDto;
import com.csye6225.webapp.exception.IncorrectPasswordException;
import com.csye6225.webapp.exception.InvalidAuthorizationException;
import com.csye6225.webapp.exception.UserNotFoundException;
import com.csye6225.webapp.exception.UsernameAlreadyExistsException;
import com.csye6225.webapp.model.User;
import com.csye6225.webapp.service.UserService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/user")
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
    public ResponseEntity<Object> createUser(@RequestBody User user, @RequestHeader(value = "Authorization", required = false) String auth) throws UsernameAlreadyExistsException{
        UserResponseDto userResponse = userService.createUser(user, auth);

        return ResponseEntity
                  .status(HttpStatus.OK)
                  .headers(headers)
                  .body(userResponse);
    }

    @GetMapping("/self")
    public ResponseEntity<Object> getUser(@RequestBody(required = false) User requestBody, @RequestHeader("Authorization") String basicAuth) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException {
        UserResponseDto userResponse = userService.getUser(requestBody, basicAuth);

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
    public ResponseEntity<Object> updateUser(@RequestHeader("Authorization") String basicAuth, @RequestBody User requestBody) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException {
        UserResponseDto userResponse = userService.updateUser(basicAuth, requestBody);

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
