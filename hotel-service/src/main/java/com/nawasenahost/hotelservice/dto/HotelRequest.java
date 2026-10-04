package com.nawasenahost.hotelservice.dto;

import jakarta.validation.constraints.*;

public class HotelRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Postal Code is required")
    private String postalCode;

    @NotBlank(message = "Phone is required")
    @Size(min = 8, max = 20, message = "Phone must be between 8 and 20 characters")
    @Pattern(
            regexp = "^\\+?[0-9][0-9\\s().-]*[0-9]$",
            message = "Invalid phone number format"
    )
    private String phone;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    public @NotBlank(message = "Name is required") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is required") String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotBlank(message = "Address is required") String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank(message = "Address is required") String address) {
        this.address = address;
    }

    public @NotBlank(message = "City is required") String getCity() {
        return city;
    }

    public void setCity(@NotBlank(message = "City is required") String city) {
        this.city = city;
    }

    public @NotBlank(message = "Country is required") String getCountry() {
        return country;
    }

    public void setCountry(@NotBlank(message = "Country is required") String country) {
        this.country = country;
    }

    public @NotBlank(message = "Postal Code is required") String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(@NotBlank(message = "Postal Code is required") String postalCode) {
        this.postalCode = postalCode;
    }

    public @NotBlank(message = "Phone is required") @Size(min = 8, max = 20, message = "Phone must be between 8 and 20 characters") @Pattern(
            regexp = "^\\+?[0-9][0-9\\s().-]*[0-9]$",
            message = "Invalid phone number format"
    ) String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank(message = "Phone is required") @Size(min = 8, max = 20, message = "Phone must be between 8 and 20 characters") @Pattern(
            regexp = "^\\+?[0-9][0-9\\s().-]*[0-9]$",
            message = "Invalid phone number format"
    ) String phone) {
        this.phone = phone;
    }

    public @NotBlank(message = "Email is required") @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email String email) {
        this.email = email;
    }
}
