package org.kharkiv.javapracticaltestassignment.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @NotEmpty(message = "Email can not be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotEmpty(message = "First name can not be empty")
    private String firstName;

    @NotEmpty(message = "Last name can not be empty")
    private String lastName;

    @NotNull(message = "Birth date can not be empty")
    @Past(message = "Date of birth must be in the past")
    private LocalDate birthDate;
    private String address;
    @Pattern(regexp = "\\+?[0-9]+", message = "Phone number must contain only digits and may start with '+'")
    private String phoneNumber;


}
