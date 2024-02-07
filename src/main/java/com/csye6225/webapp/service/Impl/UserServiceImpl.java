package com.csye6225.webapp.service.Impl;

import com.csye6225.webapp.dto.UserResponseDto;
import com.csye6225.webapp.exception.IncorrectPasswordException;
import com.csye6225.webapp.exception.InvalidAuthorizationException;
import com.csye6225.webapp.exception.UserNotFoundException;
import com.csye6225.webapp.exception.UsernameAlreadyExistsException;
import com.csye6225.webapp.model.User;
import com.csye6225.webapp.repository.UserRepository;
import com.csye6225.webapp.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public UserResponseDto createUser(User user, String auth) throws UsernameAlreadyExistsException {
        if(null != auth && !auth.isEmpty())
        {
            throw new IllegalArgumentException();
        }

        validateUserForCreation(user);

        if(null == user.getPassword() || user.getPassword().isEmpty())
        {
            throw new IllegalArgumentException();
        }

        String hashedPassword = encodePassword(user.getPassword());
        user.setPassword(hashedPassword);

        User userResponse = userRepository.save(user);
        return mapToDto(userResponse);
    }

    private void validateUserForCreation(User user) throws UsernameAlreadyExistsException {
        if ((user.getAccountCreated() != null && !user.getAccountCreated().isEmpty()) ||
                (user.getAccountUpdated() != null && !user.getAccountUpdated().isEmpty())) {
            throw new IllegalArgumentException();
        }

        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }
    }

    private void validateUserForUpdate(User requestBody) {
        if((null != requestBody.getUserName() && !requestBody.getUserName().isEmpty()) ||
                (null != requestBody.getAccountCreated() && !requestBody.getAccountCreated().isEmpty()) ||
                (null != requestBody.getAccountUpdated() && !requestBody.getAccountUpdated().isEmpty()))
        {
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
                    throw new IncorrectPasswordException();
                }
            }
            else
            {
                throw new UserNotFoundException();
            }
        }
        else
        {
            throw new InvalidAuthorizationException();
        }
    }

    @Override
    public UserResponseDto updateUser(String basicAuth, User requestBody) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException {
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
