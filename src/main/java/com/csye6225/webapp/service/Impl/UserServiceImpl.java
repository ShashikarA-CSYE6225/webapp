package com.csye6225.webapp.service.Impl;

import com.csye6225.webapp.dto.UserDto;
import com.csye6225.webapp.model.User;
import com.csye6225.webapp.repository.UserRepository;
import com.csye6225.webapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public UserDto createUser(User user)
    {
        User userResponse = userRepository.save(user);
        return mapToDto(userResponse);
    }

    @Override
    public List<UserDto> getAllUsers()
    {
        List<User> userResponseList = userRepository.findAll();
        return userResponseList.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(String userId, User newUser) {
        User existingUserResponse = userRepository.findById(userId).get();
        existingUserResponse.setFirstName(newUser.getFirstName());
        existingUserResponse.setLastName(newUser.getLastName());
        existingUserResponse.setUserName(newUser.getUserName());
        existingUserResponse.setPassword(newUser.getPassword());
        return mapToDto(existingUserResponse);
    }

    @Override
    public Optional<UserDto> findByUserNameAndPassword(String userName, String password) {
        return userRepository.findByUserNameAndPassword(userName, password).map(this::mapToDto);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .accountCreated(user.getAccountCreated())
                .accountUpdated(user.getAccountUpdated())
                .build();
    }
}
