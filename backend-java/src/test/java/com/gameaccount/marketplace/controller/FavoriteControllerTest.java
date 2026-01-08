package com.gameaccount.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameaccount.marketplace.dto.request.AddFavoriteRequest;
import com.gameaccount.marketplace.dto.response.AccountResponse;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Favorite;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.service.FavoriteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for FavoriteController.
 * Tests REST endpoints for favorite management operations.
 * Spring Security filters are disabled to test controller logic in isolation.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FavoriteService favoriteService;

    private User testUser;
    private Game testGame;
    private Account testAccount1;
    private Account testAccount2;
    private Favorite testFavorite;
    private AddFavoriteRequest addFavoriteRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .fullName("Test User")
                .role(com.gameaccount.marketplace.entity.User.Role.BUYER)
                .build();

        testGame = Game.builder()
                .id(1L)
                .name("Test Game")
                .slug("test-game")
                .build();

        testAccount1 = Account.builder()
                .id(1L)
                .seller(testUser)
                .game(testGame)
                .title("Test Account 1")
                .description("Test Description 1")
                .level(50)
                .rank("Diamond")
                .price(100.0)
                .status(Account.AccountStatus.APPROVED)
                .viewsCount(0)
                .isFeatured(false)
                .images(Arrays.asList("image1.jpg"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAccount2 = Account.builder()
                .id(2L)
                .seller(testUser)
                .game(testGame)
                .title("Test Account 2")
                .description("Test Description 2")
                .level(60)
                .rank("Master")
                .price(200.0)
                .status(Account.AccountStatus.APPROVED)
                .viewsCount(0)
                .isFeatured(false)
                .images(Arrays.asList("image2.jpg"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testFavorite = Favorite.builder()
                .id(1L)
                .user(testUser)
                .account(testAccount1)
                .build();

        addFavoriteRequest = AddFavoriteRequest.builder()
                .accountId(1L)
                .build();
    }

    @AfterEach
    void tearDown() {
        reset(favoriteService);
    }

    @Test
    @WithMockUser(username = "1")
    void addFavorite_ShouldReturnCreatedAccount() throws Exception {
        // Given
        when(favoriteService.addToFavorites(eq(1L), eq(1L))).thenReturn(testFavorite);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addFavoriteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sellerId").value(1))
                .andExpect(jsonPath("$.gameId").value(1))
                .andExpect(jsonPath("$.title").value("Test Account 1"))
                .andExpect(jsonPath("$.price").value(100.0));

        verify(favoriteService, times(1)).addToFavorites(eq(1L), eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    void addFavorite_ShouldReturn400WhenAlreadyFavorited() throws Exception {
        // Given
        when(favoriteService.addToFavorites(eq(1L), eq(1L)))
                .thenThrow(new BusinessException("Account is already in favorites"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addFavoriteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Account is already in favorites"));

        verify(favoriteService, times(1)).addToFavorites(eq(1L), eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    void getFavorites_ShouldReturnFavoritesList() throws Exception {
        // Given
        List<Account> favorites = Arrays.asList(testAccount1, testAccount2);
        when(favoriteService.getUserFavorites(eq(1L))).thenReturn(favorites);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites")
                        .param("page", "0")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(favoriteService, times(1)).getUserFavorites(eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    void getFavorites_ShouldApplyPagination() throws Exception {
        // Given
        List<Account> favorites = Arrays.asList(testAccount1, testAccount2);
        when(favoriteService.getUserFavorites(eq(1L))).thenReturn(favorites);

        // When & Then - Request page 0 with limit 1
        mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites")
                        .param("page", "0")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));

        // When & Then - Request page 1 with limit 1
        mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites")
                        .param("page", "1")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    @WithMockUser(username = "1")
    void getFavorites_ShouldReturn400ForInvalidPage() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites")
                        .param("page", "-1")
                        .param("limit", "20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page number must be >= 0"));
    }

    @Test
    @WithMockUser(username = "1")
    void getFavorites_ShouldReturn400ForInvalidLimit() throws Exception {
        // When & Then - Limit too small
        mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites")
                        .param("page", "0")
                        .param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Limit must be between 1 and 100"));

        // When & Then - Limit too large
        mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites")
                        .param("page", "0")
                        .param("limit", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Limit must be between 1 and 100"));
    }

    @Test
    @WithMockUser(username = "1")
    void getFavorites_ShouldReturnEmptyList() throws Exception {
        // Given
        when(favoriteService.getUserFavorites(eq(1L))).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/favorites")
                        .param("page", "0")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(favoriteService, times(1)).getUserFavorites(eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    void removeFavorite_ShouldReturn204OnSuccess() throws Exception {
        // Given
        doNothing().when(favoriteService).removeFromFavorites(eq(1L), eq(1L));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites/1"))
                .andExpect(status().isNoContent());

        verify(favoriteService, times(1)).removeFromFavorites(eq(1L), eq(1L));
    }

    @Test
    @WithMockUser(username = "1")
    void removeFavorite_ShouldReturn404WhenNotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Favorite not found for userId: 1 and accountId: 1"))
                .when(favoriteService).removeFromFavorites(eq(1L), eq(1L));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorites/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Favorite not found for userId: 1 and accountId: 1"));

        verify(favoriteService, times(1)).removeFromFavorites(eq(1L), eq(1L));
    }

}
