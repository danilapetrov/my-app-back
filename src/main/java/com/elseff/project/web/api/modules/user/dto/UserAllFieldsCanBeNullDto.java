package com.elseff.project.web.api.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAllFieldsCanBeNullDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Pattern(regexp = "([A-Z][a-zA-Z]*)", message = "firstname should be valid")
    @Size(min = 2, max = 40, message = "firstname size should be between 2 and 40")
    private String firstName;

    @Pattern(regexp = "([A-Z][a-zA-Z]*)", message = "lastname should be valid")
    @Size(min = 2, max = 40, message = "lastname size should be between 2 and 40")
    private String lastName;

    @Email(message = "email should be valid")
    private String email;

    @Pattern(regexp = "([A-Z][a-zA-Z]*)", message = "country should be valid")
    private String country;
}