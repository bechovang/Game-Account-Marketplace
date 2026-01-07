package com.gameaccount.marketplace.repository;

import com.gameaccount.marketplace.entity.Game;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    boolean existsBySlug(String slug);

    Game findBySlug(String slug);

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Override
    List<Game> findAll();
}
