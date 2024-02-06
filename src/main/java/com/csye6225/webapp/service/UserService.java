package com.csye6225.webapp.service;

import com.csye6225.webapp.dto.UserResponseDto;
import com.csye6225.webapp.exception.IncorrectPasswordException;
import com.csye6225.webapp.exception.UserNotFoundException;
import com.csye6225.webapp.exception.UsernameAlreadyExistsException;
import com.csye6225.webapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public UserResponseDto createUser(User user) throws UsernameAlreadyExistsException;

    public List<UserResponseDto> getAllUsers();

    public UserResponseDto updateUser(String basicAuth, User requestBody) throws UserNotFoundException, IncorrectPasswordException;

    public Optional<UserResponseDto> findByUserNameAndPassword(String userName, String password);

    public UserResponseDto getUser(User requestBody, String userName) throws UserNotFoundException, IncorrectPasswordException;

    public UserResponseDto mapToDto(User user);
}
