package io.pivotal.web.domain;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * Represents a request for User Registration
 *
 * @author Simon Rowe
 */
@Data
public class RegistrationRequest {

    @NotNull
    @NotEmpty
    private String givenNames;

    private String surname;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @NotNull
    //TODO - more complex validation on password
    private String password;

    @NotEmpty
    @NotNull
    //TODO - more complex validation on password
    private String confirmPassword;
}
