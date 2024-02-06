package com.csye6225.webapp.service;

import com.csye6225.webapp.dto.UserDto;
import com.csye6225.webapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public UserDto createUser(User user);

    public List<UserDto> getAllUsers();

    public UserDto updateUser(String userName, User requestBody);

    public Optional<UserDto> findByUserNameAndPassword(String userName, String password);

    public Optional<User> findByUserName(String userName);

    public UserDto mapToDto(User user);
}
