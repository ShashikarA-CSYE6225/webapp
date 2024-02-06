package com.csye6225.webapp.controller;

import com.csye6225.webapp.dto.UserDto;
import com.csye6225.webapp.model.User;
import com.csye6225.webapp.service.UserService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
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
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        Optional<UserDto> existingUser = userService.findByUserNameAndPassword(user.getUserName(), user.getPassword());
        if (existingUser.isPresent()) {
            String errorMessage = "Username '" + user.getUserName() + "' already exists.";

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .headers(headers)
                    .body(Collections.singletonMap("error", errorMessage));
        }

        UserDto userDto = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(userDto);
    }

    @GetMapping("/self")
    public ResponseEntity<Object> getUser(@RequestBody(required = false) User requestBody, @RequestHeader("Authorization") String basicAuth) {
        if (requestBody != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .headers(headers)
                    .body(Collections.singletonMap("error", "Invalid request body"));
        }

        String[] authorization = decodeBasicAuth(basicAuth);
        System.out.println(Arrays.toString(authorization));

        if (authorization != null) {
            String userName = authorization[0];
            String password = authorization[1];

            Optional<UserDto> existingUser = userService.findByUserNameAndPassword(userName, password);

            return existingUser.<ResponseEntity<Object>>map(userDto -> ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(userDto))
                    .orElseGet(() -> ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .headers(headers)
                    .body(Collections.singletonMap("error", "Unauthorized")));
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .headers(headers)
                    .body(Collections.singletonMap("error", "Unauthorized"));
        }
    }

    @GetMapping("/all")
    public List<UserDto> getAllUsers()
    {
        return userService.getAllUsers();
    }


    public String[] decodeBasicAuth(String basicAuth) {
        String encodedAuth = basicAuth.substring("Basic ".length()).trim();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedAuth);
        String decodedAuth = new String(decodedBytes, StandardCharsets.UTF_8);
        return decodedAuth.split(":", 2);
    }

//    @PutMapping("self")
//    public UserDto updateUser(@RequestBody User user)
//    {
//        //userService.
//    }
}
