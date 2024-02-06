package com.csye6225.webapp.service.Impl;

import com.csye6225.webapp.dto.UserDto;
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
    public UserDto updateUser(String userName, User requestBody) {
        Optional<User> existingUserOptional = userRepository.findByUserName(userName);

        if (existingUserOptional.isPresent()) {
            User existingUserResponse = existingUserOptional.get();
            existingUserResponse.setFirstName(requestBody.getFirstName());
            existingUserResponse.setLastName(requestBody.getLastName());

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(requestBody.getPassword());
            existingUserResponse.setPassword(hashedPassword);

            return mapToDto(userRepository.save(existingUserResponse));
        }

        return null;
    }

    @Override
    public Optional<UserDto> findByUserNameAndPassword(String userName, String password) {
        return userRepository.findByUserNameAndPassword(userName, password).map(this::mapToDto);
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
    @Override
    public UserDto mapToDto(User user) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(user, UserDto.class);
    }
}
