package com.csye6225.webapp.service;

import com.csye6225.webapp.dto.UserResponseDto;
import com.csye6225.webapp.exception.*;
import com.csye6225.webapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public UserResponseDto createUser(User user, String auth) throws UsernameAlreadyExistsException;

    public List<UserResponseDto> getAllUsers();

    public UserResponseDto updateUser(String basicAuth, User requestBody) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException;

    public Optional<UserResponseDto> findByUserNameAndPassword(String userName, String password);

    public UserResponseDto getUser(User requestBody, String userName) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException;

    public UserResponseDto mapToDto(User user);
}
