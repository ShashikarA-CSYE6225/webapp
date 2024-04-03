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

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public UserResponseDto createUser(User user, String auth) throws UsernameAlreadyExistsException, IOException, ExecutionException, InterruptedException {
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

            String projectId = "csye6225-dev-414805";
            String topicId = "verify-email";

            String uuid = generateUUID();

            userResponse.setEmailVerificationToken(uuid);
            userRepository.save(userResponse);

            publishEmail(projectId, topicId, userResponse.getUserName(), uuid);

            return mapToDto(userResponse);
    }


    public static void publishEmail(String projectId, String topicId, String email, String uuid)
            throws IOException, ExecutionException, InterruptedException {
        TopicName topicName = TopicName.of(projectId, topicId);

        Publisher publisher = null;
        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();

            ByteString data = ByteString.copyFromUtf8(uuid + ":" + email);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            String messageId = messageIdFuture.get();
            System.out.println("Published message ID: " + messageId);
        }
        catch (Exception e)
        {
            System.out.println("Error in publishing message: " + e.getMessage());
        }
        finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
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

    private User authenticateUser(String basicAuth) throws IncorrectPasswordException, UserNotFoundException, InvalidAuthorizationException, UserNotVerifiedException {
        String[] authorization = decodeBasicAuth(basicAuth);
        //System.out.println(Arrays.toString(authorization));

        if (authorization != null) {
            String userName = authorization[0];
            String password = authorization[1];

            Optional<User> existingUser = userRepository.findByUserName(userName);

            if(existingUser.isPresent())
            {
                if(existingUser.get().isVerified())
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
                    throw new UserNotVerifiedException();
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
    public UserResponseDto updateUser(String basicAuth, User requestBody) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException, UserNotUpdatedException, UserNotVerifiedException {
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
    public UserResponseDto getUser(User requestBody, String basicAuth) throws UserNotFoundException, IncorrectPasswordException, InvalidAuthorizationException, UserNotVerifiedException {
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

    @Override
    public String verifyUser(String token) throws TokenExpiredException, UserNotFoundException {
        // Split token to extract UUID and expiry
        String[] parts = token.split(":");
        String uuid = parts[0];
        String email = parts[1];

        Optional<User> userOptional = userRepository.findByUserName(email);

        if(userOptional.isPresent())
        {
            User user = userOptional.get();
            if(!user.isVerified())
            {
                // Get the emailSentTime from user
                Date emailExpiryTimeLoc = user.getEmailExpiryTime();

                // Convert emailSentTime from UTC to Instant
                Instant emailExpiryTime = emailExpiryTimeLoc.toInstant();

                // Get the current time in UTC
                Instant currentTime = Instant.now();

                // Calculate the duration between email sent time and current time
                Duration duration = Duration.between(emailExpiryTime, currentTime);

                System.out.println(emailExpiryTime);
                System.out.println(currentTime);
                System.out.println(duration.toSeconds());

                if (user.getEmailVerificationToken().equals(uuid) && duration.toSeconds() < 0) {
                    user.setVerified(true);
                    userRepository.save(user);
                    return "User Verified Successfully";
                }
                else
                {
                    throw new TokenExpiredException();
                }
            }
            else
            {
                return "User Already Verified!!";
            }
        }
        else
        {
            throw new UserNotFoundException();
        }
    }
}
