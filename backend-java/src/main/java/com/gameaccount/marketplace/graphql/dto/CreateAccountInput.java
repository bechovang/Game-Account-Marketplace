package com.gameaccount.marketplace.graphql.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GraphQL Input type for creating a new account listing.
 * Maps from GraphQL CreateAccountInput to service layer DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountInput {

    @NotNull(message = "Game ID is required")
    private Long gameId;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private Integer level;

    @Size(max = 50, message = "Rank must not exceed 50 characters")
    private String rank;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    @Size(max = 10, message = "Maximum 10 images allowed")
    private List<@NotBlank String> images;

    @NotBlank(message = "Game username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Game password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}
