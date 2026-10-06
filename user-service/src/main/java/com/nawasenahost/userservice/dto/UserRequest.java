package com.nawasenahost.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^\\+?[0-9][0-9\\s().-]{6,20}[0-9]$",
            message = "Invalid phone number format"
    )
    private String phone;

    public UserRequest() {
    }

    public @NotBlank(message = "First name is required") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank(message = "First name is required") String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank(message = "Last name is required") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank(message = "Last name is required") String lastName) {
        this.lastName = lastName;
    }

    public @NotBlank(message = "Email is required") @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email String email) {
        this.email = email;
    }

    public @NotBlank(message = "Phone is required") @Pattern(
            regexp = "^\\+?[0-9][0-9\\s().-]{6,20}[0-9]$",
            message = "Invalid phone number format"
    ) String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank(message = "Phone is required") @Pattern(
            regexp = "^\\+?[0-9][0-9\\s().-]{6,20}[0-9]$",
            message = "Invalid phone number format"
    ) String phone) {
        this.phone = phone;
    }
}
