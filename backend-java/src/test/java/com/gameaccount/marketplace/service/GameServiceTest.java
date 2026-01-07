package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GameService.
 * Tests business logic for game lookups.
 */
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;

    @BeforeEach
    void setUp() {
        testGame = Game.builder()
                .id(1L)
                .name("Test Game")
                .slug("test-game")
                .description("Test Description")
                .build();
    }

    // ==================== getAllGames() tests ====================

    @Test
    void getAllGames_ReturnsAllGames() {
        // Given
        List<Game> games = Arrays.asList(testGame);
        when(gameRepository.findAll()).thenReturn(games);

        // When
        List<Game> result = gameService.getAllGames();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Game");

        verify(gameRepository).findAll();
    }

    @Test
    void getAllGames_EmptyList_ReturnsEmpty() {
        // Given
        when(gameRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Game> result = gameService.getAllGames();

        // Then
        assertThat(result).isEmpty();
    }

    // ==================== getGameById() tests ====================

    @Test
    void getGameById_WithValidId_ReturnsGame() {
        // Given
        Long gameId = 1L;
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));

        // When
        Game result = gameService.getGameById(gameId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Game");

        verify(gameRepository).findById(gameId);
    }

    @Test
    void getGameById_WithNullId_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> gameService.getGameById(null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Valid game ID is required");
    }

    @Test
    void getGameById_WithNegativeId_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> gameService.getGameById(-1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Valid game ID is required");
    }

    @Test
    void getGameById_WithZeroId_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> gameService.getGameById(0L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Valid game ID is required");
    }

    @Test
    void getGameById_GameNotFound_ThrowsException() {
        // Given
        Long gameId = 999L;
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> gameService.getGameById(gameId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Game not found with id: " + gameId);
    }

    // ==================== getGameBySlug() tests ====================

    @Test
    void getGameBySlug_WithValidSlug_ReturnsGame() {
        // Given
        String slug = "test-game";
        when(gameRepository.findBySlug(slug)).thenReturn(testGame);

        // When
        Game result = gameService.getGameBySlug(slug);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("test-game");

        verify(gameRepository).findBySlug(slug);
    }

    @Test
    void getGameBySlug_WithNullSlug_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> gameService.getGameBySlug(null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Game slug is required");
    }

    @Test
    void getGameBySlug_WithBlankSlug_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> gameService.getGameBySlug("  "))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Game slug is required");
    }

    @Test
    void getGameBySlug_GameNotFound_ThrowsException() {
        // Given
        String slug = "non-existent";
        when(gameRepository.findBySlug(slug)).thenReturn(null);

        // When/Then
        assertThatThrownBy(() -> gameService.getGameBySlug(slug))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Game not found with slug: " + slug);
    }
}
