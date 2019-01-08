package io.pivotal.web.domain;

import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Represents the account.
 *
 * Entity object that represents a user account.
 *
 * @author David Ferreira Pinto
 * @author Simon Rowe
 *
 */
@Data
public class User implements Serializable {

    private String id;
    private String email;
    private String givenNames;
    private String surname;
    private DateTime createdDate;
    private String jwt;
}
