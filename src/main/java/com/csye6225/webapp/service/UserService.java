package com.csye6225.webapp.service;

import com.csye6225.webapp.dto.UserResponseDto;
import com.csye6225.webapp.exception.*;
import com.csye6225.webapp.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface UserService {
    public UserResponseDto createUser(User user, String auth) throws UsernameAlreadyExistsException, IOException, ExecutionException, InterruptedException;

    public List<UserResponseDto> getAllUsers();

    public UserResponseDto updateUser(String basicAuth, User requestBody) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException, UserNotUpdatedException, UserNotVerifiedException;

    public Optional<UserResponseDto> findByUserNameAndPassword(String userName, String password);

    public UserResponseDto getUser(User requestBody, String userName) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException, UserNotVerifiedException;

    public UserResponseDto mapToDto(User user);

    public String verifyUser(String token) throws TokenExpiredException, UserNotFoundException;
}
