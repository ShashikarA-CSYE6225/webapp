package com.csye6225.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotNull
    private String id;

    @Column(name = "first_name", nullable = false)
    @JsonProperty("first_name")
    @NotNull
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @JsonProperty("last_name")
    @NotNull
    private String lastName;

    @Column(name = "username", nullable = false)
    @JsonProperty("username")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @NotNull
    private String userName;

    @Column(name = "password", nullable = false)
    @JsonProperty("password")
    @NotNull
    private String password;

    @Column(name = "account_updated")
    @UpdateTimestamp
    @JsonProperty("account_updated")
    private String accountUpdated;

    @Column(name = "account_created")
    @CreationTimestamp
    @JsonProperty("account_created")
    private String accountCreated;
}
