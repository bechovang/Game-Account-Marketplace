import React from 'react';
import { useQuery } from '@apollo/client';
import { useFilters, type AccountFilters } from '../../hooks/useFilters';
import { GET_GAMES } from '../../services/graphql/queries';

interface Game {
  id: string;
  name: string;
}

interface ActiveFilterChip {
  key: string;
  label: string;
  value: string | number;
}

const ActiveFilterChips: React.FC = () => {
  const { filters, setFilter, setFilters, hasActiveFilters } = useFilters();

  // Fetch games for display names
  const { data: gamesData } = useQuery(GET_GAMES, {
    fetchPolicy: 'cache-first',
    skip: !filters.gameId,
  });

  const games = gamesData?.games || [];

  // Convert active filters to chips
  const activeChips: ActiveFilterChip[] = React.useMemo(() => {
    const chips: ActiveFilterChip[] = [];

    // Game filter
    if (filters.gameId) {
      const game = games.find((g: Game) => g.id === filters.gameId);
      chips.push({
        key: 'gameId',
        label: `Game: ${game?.name || filters.gameId}`,
        value: filters.gameId,
      });
    }

    // Price range
    if (filters.minPrice !== undefined || filters.maxPrice !== undefined) {
      const priceLabel =
        filters.minPrice !== undefined && filters.maxPrice !== undefined
          ? `$${filters.minPrice} - $${filters.maxPrice}`
          : filters.minPrice !== undefined
          ? `From $${filters.minPrice}`
          : `Up to $${filters.maxPrice}`;
      chips.push({
        key: 'price',
        label: priceLabel,
        value: 'price',
      });
    }

    // Level range
    if (filters.minLevel !== undefined || filters.maxLevel !== undefined) {
      const levelLabel =
        filters.minLevel !== undefined && filters.maxLevel !== undefined
          ? `Lvl ${filters.minLevel}-${filters.maxLevel}`
          : filters.minLevel !== undefined
          ? `Lvl ${filters.minLevel}+`
          : `Up to Lvl ${filters.maxLevel}`;
      chips.push({
        key: 'level',
        label: levelLabel,
        value: 'level',
      });
    }

    // Rank
    if (filters.rank) {
      chips.push({
        key: 'rank',
        label: `Rank: ${filters.rank}`,
        value: filters.rank,
      });
    }

    // Search query
    if (filters.q) {
      chips.push({
        key: 'q',
        label: `Search: "${filters.q}"`,
        value: filters.q,
      });
    }

    return chips;
  }, [filters, games]);

  // Remove a single filter
  const handleRemoveChip = (chip: ActiveFilterChip) => {
    switch (chip.key) {
      case 'gameId':
        setFilter('gameId', undefined);
        break;
      case 'price':
        setFilters({ minPrice: undefined, maxPrice: undefined });
        break;
      case 'level':
        setFilters({ minLevel: undefined, maxLevel: undefined });
        break;
      case 'rank':
        setFilter('rank', undefined);
        break;
      case 'q':
        setFilter('q', undefined);
        break;
    }
  };

  // Remove all filters
  const handleClearAll = () => {
    setFilters({
      gameId: undefined,
      minPrice: undefined,
      maxPrice: undefined,
      minLevel: undefined,
      maxLevel: undefined,
      rank: undefined,
      q: undefined,
    });
  };

  if (!hasActiveFilters) {
    return null;
  }

  return (
    <div className="flex flex-wrap items-center gap-2 py-2">
      <span className="text-sm text-gray-600">Active filters:</span>

      {activeChips.map((chip) => (
        <button
          key={chip.key}
          onClick={() => handleRemoveChip(chip)}
          className="inline-flex items-center gap-1 px-3 py-1 bg-blue-50 text-blue-700 rounded-full text-sm hover:bg-blue-100 transition-colors"
          aria-label={`Remove ${chip.label} filter`}
        >
          <span>{chip.label}</span>
          <svg
            className="w-3 h-3"
            fill="currentColor"
            viewBox="0 0 20 20"
            aria-hidden="true"
          >
            <path
              fillRule="evenodd"
              d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
              clipRule="evenodd"
            />
          </svg>
        </button>
      ))}

      {activeChips.length > 1 && (
        <button
          onClick={handleClearAll}
          className="text-sm text-red-600 hover:text-red-700 px-2 py-1 hover:bg-red-50 rounded transition-colors"
          aria-label="Clear all active filters"
        >
          Clear all
        </button>
      )}
    </div>
  );
};

export default React.memo(ActiveFilterChips);
