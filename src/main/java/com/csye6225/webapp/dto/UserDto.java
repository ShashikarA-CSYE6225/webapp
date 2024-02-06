package com.csye6225.webapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String accountCreated;
    private String accountUpdated;
}
