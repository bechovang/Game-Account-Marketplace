package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GameQuery GraphQL resolver.
 * Tests delegation to GameService.
 */
@ExtendWith(MockitoExtension.class)
class GameQueryTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameQuery gameQuery;

    private Game testGame;

    @BeforeEach
    void setUp() {
        testGame = Game.builder()
                .id(1L)
                .name("Test Game")
                .slug("test-game")
                .build();
    }

    // ==================== games() query tests ====================

    @Test
    void games_ReturnsAllGames() {
        // Given
        List<Game> games = Arrays.asList(testGame);
        when(gameService.getAllGames()).thenReturn(games);

        // When
        List<Game> result = gameQuery.games();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Game");

        verify(gameService).getAllGames();
    }

    @Test
    void games_EmptyList_ReturnsEmpty() {
        // Given
        when(gameService.getAllGames()).thenReturn(Collections.emptyList());

        // When
        List<Game> result = gameQuery.games();

        // Then
        assertThat(result).isEmpty();
    }

    // ==================== game() query tests ====================

    @Test
    void game_WithValidId_ReturnsGame() {
        // Given
        Long gameId = 1L;
        when(gameService.getGameById(gameId)).thenReturn(testGame);

        // When
        Game result = gameQuery.game(gameId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Game");

        verify(gameService).getGameById(gameId);
    }

    @Test
    void game_ServiceThrowsException_PropagatesException() {
        // Given
        Long gameId = 999L;
        when(gameService.getGameById(gameId))
                .thenThrow(new ResourceNotFoundException("Game not found with id: " + gameId));

        // When/Then
        assertThatThrownBy(() -> gameQuery.game(gameId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Game not found");

        verify(gameService).getGameById(gameId);
    }

    // ==================== gameBySlug() query tests ====================

    @Test
    void gameBySlug_WithValidSlug_ReturnsGame() {
        // Given
        String slug = "test-game";
        when(gameService.getGameBySlug(slug)).thenReturn(testGame);

        // When
        Game result = gameQuery.gameBySlug(slug);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("test-game");

        verify(gameService).getGameBySlug(slug);
    }

    @Test
    void gameBySlug_ServiceThrowsException_PropagatesException() {
        // Given
        String slug = "non-existent";
        when(gameService.getGameBySlug(slug))
                .thenThrow(new ResourceNotFoundException("Game not found with slug: " + slug));

        // When/Then
        assertThatThrownBy(() -> gameQuery.gameBySlug(slug))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Game not found");

        verify(gameService).getGameBySlug(slug);
    }
}
