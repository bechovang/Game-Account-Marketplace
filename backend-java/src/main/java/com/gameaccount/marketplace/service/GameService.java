package com.gameaccount.marketplace.service;

import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.exception.ResourceNotFoundException;
import com.gameaccount.marketplace.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Game entity operations.
 * Provides business logic for game lookups used by GraphQL resolvers.
 * Follows shared service layer pattern - both GraphQL and REST use this service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;

    /**
     * Get all games.
     *
     * @return List of all games
     */
    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        log.debug("Fetching all games");
        return gameRepository.findAll();
    }

    /**
     * Get a game by ID.
     *
     * @param id Game ID
     * @return Game entity
     * @throws ResourceNotFoundException if game not found
     */
    @Transactional(readOnly = true)
    public Game getGameById(Long id) {
        log.debug("Fetching game by id: {}", id);

        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Valid game ID is required");
        }

        return gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));
    }

    /**
     * Get a game by its unique slug.
     * Useful for SEO-friendly URLs.
     *
     * @param slug Game slug
     * @return Game entity
     * @throws ResourceNotFoundException if game not found
     */
    @Transactional(readOnly = true)
    public Game getGameBySlug(String slug) {
        log.debug("Fetching game by slug: {}", slug);

        if (slug == null || slug.isBlank()) {
            throw new ResourceNotFoundException("Game slug is required");
        }

        Game game = gameRepository.findBySlug(slug);
        if (game == null) {
            throw new ResourceNotFoundException("Game not found with slug: " + slug);
        }
        return game;
    }
}
