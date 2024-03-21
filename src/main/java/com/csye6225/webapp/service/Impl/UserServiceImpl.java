package com.csye6225.webapp.service.Impl;

import com.csye6225.webapp.dto.UserResponseDto;
import com.csye6225.webapp.exception.*;
import com.csye6225.webapp.model.User;
import com.csye6225.webapp.repository.UserRepository;
import com.csye6225.webapp.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public UserResponseDto createUser(User user, String auth) throws UsernameAlreadyExistsException {
            if(null != auth && !auth.isEmpty())
            {
                log.error("Authorization is given but not required");
                throw new IllegalArgumentException();
            }

            validateUserForCreation(user);

            if(validateForEmptyAndNullValues(user))
            {
                log.error("required request fields are null or empty");
                throw new IllegalArgumentException();
            }

            String hashedPassword = encodePassword(user.getPassword());
            user.setPassword(hashedPassword);

            User userResponse = userRepository.save(user);

            log.debug("User Response Payload: " + userResponse);

            return mapToDto(userResponse);
    }

    private boolean validateForEmptyAndNullValues(User user) {
        return (null == user.getPassword() || user.getPassword().isEmpty()) ||
                (null == user.getFirstName() || user.getFirstName().isEmpty()) || (null == user.getLastName() || user.getLastName().isEmpty());
    }

    private void validateUserForCreation(User user) throws UsernameAlreadyExistsException {
        if ((user.getAccountCreated() != null && !user.getAccountCreated().isEmpty()) ||
                (user.getAccountUpdated() != null && !user.getAccountUpdated().isEmpty())) {
            log.error("Invalid request fields given");
            throw new IllegalArgumentException();
        }

        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            log.error("Error creating user, username already exists");
            throw new UsernameAlreadyExistsException();
        }
    }

    private void validateUserForUpdate(User requestBody) {
        if((null != requestBody.getUserName() && !requestBody.getUserName().isEmpty()) ||
                (null != requestBody.getAccountCreated() && !requestBody.getAccountCreated().isEmpty()) ||
                (null != requestBody.getAccountUpdated() && !requestBody.getAccountUpdated().isEmpty()))
        {
            log.error("invalid request details given");
            throw new IllegalArgumentException();
        }
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    @Override
    public List<UserResponseDto> getAllUsers()
    {
        List<User> userResponseList = userRepository.findAll();
        return userResponseList.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private User authenticateUser(String basicAuth) throws IncorrectPasswordException, UserNotFoundException, InvalidAuthorizationException {
        String[] authorization = decodeBasicAuth(basicAuth);
        //System.out.println(Arrays.toString(authorization));

        if (authorization != null) {
            String userName = authorization[0];
            String password = authorization[1];

            Optional<User> existingUser = userRepository.findByUserName(userName);

            if(existingUser.isPresent())
            {
                String hashedPassword = existingUser.get().getPassword();
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

                if(passwordEncoder.matches(password, hashedPassword))
                {
                    return existingUser.get();
                }
                else
                {
                    log.error("Invalid login details");
                    throw new IncorrectPasswordException();
                }
            }
            else
            {
                log.error("Invalid login details");
                throw new UserNotFoundException();
            }
        }
        else
        {
            log.error("No authorization given");
            throw new InvalidAuthorizationException();
        }
    }

    @Override
    public UserResponseDto updateUser(String basicAuth, User requestBody) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException, UserNotUpdatedException {
            if(null == requestBody.getFirstName() && null == requestBody.getLastName()
                    && null == requestBody.getPassword())
            {
                log.error("invalid request details given");
                throw new UserNotUpdatedException();
            }

            if((null!= requestBody.getFirstName() && requestBody.getFirstName().isBlank()) ||
                    (null!= requestBody.getLastName() && requestBody.getLastName().isBlank()) || (null!= requestBody.getPassword() && requestBody.getPassword().isBlank()))
            {
                log.error("invalid request details given");
                throw new UserNotUpdatedException();
            }

            validateUserForUpdate(requestBody);

            User authenticatedUser = authenticateUser(basicAuth);

            if(null != requestBody.getFirstName() && !requestBody.getFirstName().isEmpty())
            {
                authenticatedUser.setFirstName(requestBody.getFirstName());
            }

            if(null != requestBody.getLastName() && !requestBody.getLastName().isEmpty())
            {
                authenticatedUser.setLastName(requestBody.getLastName());
            }

            if(null != requestBody.getPassword() && !requestBody.getPassword().isEmpty())
            {
                log.warn("Password Updated!!");
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode(requestBody.getPassword());
                authenticatedUser.setPassword(hashedPassword);
            }

            return mapToDto(userRepository.save(authenticatedUser));
    }

    @Override
    public Optional<UserResponseDto> findByUserNameAndPassword(String userName, String password) {
        return userRepository.findByUserNameAndPassword(userName, password).map(this::mapToDto);
    }

    @Override
    public UserResponseDto getUser(User requestBody, String basicAuth) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException {
        if (requestBody != null) {
            log.error("invalid request details given");
            throw new IllegalArgumentException();
        }

      return mapToDto(authenticateUser(basicAuth));
    }

    private String[] decodeBasicAuth(String basicAuth) {
        if (basicAuth == null || !basicAuth.startsWith("Basic ")) {
            return null;
        }
        String encodedAuth = basicAuth.substring("Basic ".length()).trim();
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(encodedAuth);
        String decodedAuth = new String(decodedBytes);
        return decodedAuth.split(":", 2);
    }

    @Override
    public UserResponseDto mapToDto(User user) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(user, UserResponseDto.class);
    }
}
