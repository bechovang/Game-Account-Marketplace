package com.gameaccount.marketplace.graphql.query;

import com.gameaccount.marketplace.entity.Game;
import com.gameaccount.marketplace.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Query Resolver for Game entities.
 * Uses Spring Boot 3's native @QueryMapping annotation.
 * Delegates all business logic to GameService (shared service layer pattern).
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GameQuery {

    private final GameService gameService;

    /**
     * Get all games.
     * Returns list of all available games in the marketplace.
     * Delegates to GameService.getAllGames()
     */
    @QueryMapping
    public List<Game> games() {
        log.debug("GraphQL games query");
        return gameService.getAllGames();
    }

    /**
     * Get a single game by ID.
     * Delegates to GameService.getGameById()
     */
    @QueryMapping
    public Game game(@Argument Long id) {
        log.debug("GraphQL game query - id: {}", id);
        return gameService.getGameById(id);
    }

    /**
     * Get a game by its unique slug.
     * Useful for SEO-friendly URLs.
     * Delegates to GameService.getGameBySlug()
     */
    @QueryMapping
    public Game gameBySlug(@Argument String slug) {
        log.debug("GraphQL gameBySlug query - slug: {}", slug);
        return gameService.getGameBySlug(slug);
    }
}
