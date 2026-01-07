package com.gameaccount.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameaccount.marketplace.dto.request.CreateAccountRequest;
import com.gameaccount.marketplace.dto.request.UpdateAccountRequest;
import com.gameaccount.marketplace.entity.Account;
import com.gameaccount.marketplace.entity.Account.AccountStatus;
import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.entity.User;
import com.gameaccount.marketplace.exception.BusinessException;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for AccountController.
 * Tests REST endpoints for account CRUD operations.
 * Spring Security filters are disabled to test controller logic in isolation.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private User testSeller;
    private Game testGame;
    private Account testAccount;
    private CreateAccountRequest createRequest;
    private UpdateAccountRequest updateRequest;
    private UsernamePasswordAuthenticationToken userAuth;
    private UsernamePasswordAuthenticationToken adminAuth;

    @BeforeEach
    void setUp() {
        // Setup test data
        testSeller = User.builder()
                .id(1L)
                .email("seller@test.com")
                .fullName("Test Seller")
                .role(com.gameaccount.marketplace.entity.User.Role.SELLER)
                .build();

        testGame = Game.builder()
                .id(1L)
                .name("Test Game")
                .slug("test-game")
                .build();

        testAccount = Account.builder()
                .id(1L)
                .seller(testSeller)
                .game(testGame)
                .title("Test Account")
                .description("Test Description")
                .level(50)
                .rank("Diamond")
                .price(100.0)
                .status(AccountStatus.APPROVED)
                .viewsCount(100)
                .isFeatured(false)
                .images(Arrays.asList("http://image1.jpg"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new CreateAccountRequest();
        createRequest.setGameId(1L);
        createRequest.setTitle("Test Account");
        createRequest.setDescription("Test Description");
        createRequest.setLevel(50);
        createRequest.setRank("Diamond");
        createRequest.setPrice(100.0);
        createRequest.setImages(Arrays.asList("http://image1.jpg"));

        updateRequest = new UpdateAccountRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setLevel(60);
        updateRequest.setRank("Master");
        updateRequest.setPrice(150.0);
        updateRequest.setImages(Arrays.asList("http://image2.jpg"));

        // Setup authentication
        userAuth = new UsernamePasswordAuthenticationToken(
                "1", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER")));

        adminAuth = new UsernamePasswordAuthenticationToken(
                "1", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== createAccount (JSON) tests ====================

    @Test
    void createAccountJson_WithValidData_Returns201() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        when(accountService.createAccount(any(CreateAccountRequest.class), eq(1L)))
                .thenReturn(testAccount);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Account"))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    // Note: Authorization tests (401 responses) are handled by Spring Security integration tests.
    // Unit tests focus on controller logic and business rules.

    @Test
    void createAccountJson_WithInvalidTitle_Returns400() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        createRequest.setTitle("X"); // Too short

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==================== getAccountById tests ====================

    @Test
    void getAccountById_WithValidId_ReturnsAccount() throws Exception {
        // Given
        when(accountService.getAccountById(1L)).thenReturn(testAccount);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Account"))
                .andExpect(jsonPath("$.sellerName").value("Test Seller"))
                .andExpect(jsonPath("$.gameName").value("Test Game"));
    }

    @Test
    void getAccountById_WithInvalidId_Returns404() throws Exception {
        // Given
        when(accountService.getAccountById(999L))
                .thenThrow(new ResourceNotFoundException("Account not found with id: 999"));

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    // ==================== updateAccount tests ====================

    @Test
    void updateAccount_WithValidData_Returns200() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        Account updatedAccount = Account.builder()
                .id(1L)
                .seller(testSeller)
                .game(testGame)
                .title("Updated Title")
                .price(150.0)
                .status(AccountStatus.APPROVED)
                .build();
        when(accountService.updateAccount(eq(1L), any(UpdateAccountRequest.class), eq(1L)))
                .thenReturn(updatedAccount);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.price").value(150.0));
    }

    // ==================== deleteAccount tests ====================

    @Test
    void deleteAccount_AsOwner_Returns204() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        // deleteAccount returns void

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/accounts/1"))
                .andExpect(status().isNoContent());
    }

    // ==================== searchAccounts tests ====================

    @Test
    void searchAccounts_WithFilters_ReturnsPaginatedResults() throws Exception {
        // Given
        org.springframework.data.domain.Page<Account> page =
                new org.springframework.data.domain.PageImpl<>(Arrays.asList(testAccount));
        when(accountService.searchAccounts(eq(1L), eq(50.0), eq(200.0), eq(AccountStatus.APPROVED), any()))
                .thenReturn(page);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts")
                        .param("gameId", "1")
                        .param("minPrice", "50.0")
                        .param("maxPrice", "200.0")
                        .param("status", "APPROVED")
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void searchAccounts_WithNoFilters_ReturnsAllResults() throws Exception {
        // Given
        org.springframework.data.domain.Page<Account> page =
                new org.springframework.data.domain.PageImpl<>(Arrays.asList(testAccount));
        when(accountService.searchAccounts(eq(null), eq(null), eq(null), eq(null), any()))
                .thenReturn(page);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ==================== getMyAccounts tests ====================

    @Test
    void getMyAccounts_WithAuthenticatedUser_ReturnsSellerAccounts() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(userAuth);

        org.springframework.data.domain.Page<Account> page =
                new org.springframework.data.domain.PageImpl<>(Arrays.asList(testAccount));
        when(accountService.getSellerAccounts(eq(1L), eq(AccountStatus.APPROVED), any()))
                .thenReturn(page);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts/seller/my-accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].sellerId").value(1));
    }
}
