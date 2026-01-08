import React, { useState } from 'react';
import { useQuery } from '@apollo/client';
import { GET_GAMES } from '../../services/graphql/queries';
import { useFilters, validateFilters, type AccountFilters } from '../../hooks/useFilters';

interface Game {
  id: string;
  name: string;
  iconUrl?: string;
}

const RANK_OPTIONS = [
  { value: '', label: 'All Ranks' },
  { value: 'IRON', label: 'Iron' },
  { value: 'BRONZE', label: 'Bronze' },
  { value: 'SILVER', label: 'Silver' },
  { value: 'GOLD', label: 'Gold' },
  { value: 'PLATINUM', label: 'Platinum' },
  { value: 'DIAMOND', label: 'Diamond' },
  { value: 'MASTER', label: 'Master' },
  { value: 'GRANDMASTER', label: 'Grandmaster' },
  { value: 'CHALLENGER', label: 'Challenger' },
];

const STATUS_OPTIONS = [
  { value: 'APPROVED', label: 'Approved Only' },
  { value: 'PENDING', label: 'Pending Only' },
  { value: '', label: 'All Status' },
];

interface FilterSidebarProps {
  isOpen?: boolean;
  onClose?: () => void;
}

const FilterSidebar: React.FC<FilterSidebarProps> = ({ isOpen = true, onClose }) => {
  const { filters, setFilter, setFilters, clearFilters, activeFilterCount } = useFilters();
  const [localPriceMin, setLocalPriceMin] = useState(filters.minPrice?.toString() || '');
  const [localPriceMax, setLocalPriceMax] = useState(filters.maxPrice?.toString() || '');
  const [localLevelMin, setLocalLevelMin] = useState(filters.minLevel?.toString() || '');
  const [localLevelMax, setLocalLevelMax] = useState(filters.maxLevel?.toString() || '');
  const [validationError, setValidationError] = useState<string | null>(null);

  const { data: gamesData, loading: gamesLoading } = useQuery(GET_GAMES, {
    fetchPolicy: 'cache-first',
  });

  const games = gamesData?.games || [];

  // Apply price range filter
  const applyPriceFilter = () => {
    const minPrice = localPriceMin ? parseFloat(localPriceMin) : undefined;
    const maxPrice = localPriceMax ? parseFloat(localPriceMax) : undefined;

    try {
      validateFilters({ minPrice, maxPrice });
      setFilters({ minPrice, maxPrice });
      setValidationError(null);
    } catch (error) {
      setValidationError(error instanceof Error ? error.message : 'Invalid price range');
    }
  };

  // Apply level range filter
  const applyLevelFilter = () => {
    const minLevel = localLevelMin ? parseInt(localLevelMin) : undefined;
    const maxLevel = localLevelMax ? parseInt(localLevelMax) : undefined;

    try {
      validateFilters({ minLevel, maxLevel });
      setFilters({ minLevel, maxLevel });
      setValidationError(null);
    } catch (error) {
      setValidationError(error instanceof Error ? error.message : 'Invalid level range');
    }
  };

  // Clear all filters
  const handleClearFilters = () => {
    clearFilters();
    setLocalPriceMin('');
    setLocalPriceMax('');
    setLocalLevelMin('');
    setLocalLevelMax('');
    setValidationError(null);
  };

  // Mobile responsive classes
  const sidebarClasses = isOpen
    ? 'fixed inset-y-0 left-0 z-40 w-80 bg-white shadow-lg transform transition-transform duration-300 ease-in-out md:relative md:transform-none md:shadow-none'
    : 'fixed inset-y-0 left-0 z-40 w-80 bg-white shadow-lg transform -translate-x-full transition-transform duration-300 ease-in-out md:relative md:translate-x-none md:shadow-none';

  return (
    <>
      {/* Mobile backdrop */}
      {isOpen && onClose && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-30 md:hidden"
          onClick={onClose}
          aria-hidden="true"
        />
      )}

      {/* Sidebar */}
      <aside className={sidebarClasses} aria-label="Filter sidebar">
        <div className="h-full flex flex-col">
          {/* Header */}
          <div className="p-4 border-b flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">Filters</h2>
            <div className="flex items-center gap-2">
              {/* Clear button */}
              <button
                onClick={handleClearFilters}
                className="text-sm text-blue-600 hover:text-blue-700 px-2 py-1 rounded hover:bg-blue-50 transition-colors"
                aria-label="Clear all filters"
              >
                Clear All
              </button>
              {/* Close button for mobile */}
              {onClose && (
                <button
                  onClick={onClose}
                  className="md:hidden text-gray-500 hover:text-gray-700 p-1"
                  aria-label="Close filters"
                >
                  ✕
                </button>
              )}
            </div>
          </div>

          {/* Scrollable filters */}
          <div className="flex-1 overflow-y-auto p-4 space-y-6">
            {/* Validation error */}
            {validationError && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-3 py-2 rounded text-sm" role="alert">
                {validationError}
              </div>
            )}

            {/* Game Dropdown */}
            <div>
              <label htmlFor="game-filter" className="block text-sm font-medium text-gray-700 mb-2">
                Game
              </label>
              <select
                id="game-filter"
                value={filters.gameId || ''}
                onChange={(e) => setFilter('gameId', e.target.value || undefined)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                aria-label="Filter by game"
              >
                <option value="">All Games</option>
                {games.map((game: Game) => (
                  <option key={game.id} value={game.id}>
                    {game.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Price Range */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Price Range
              </label>
              <div className="flex items-center gap-2">
                <div className="flex-1">
                  <input
                    type="number"
                    id="min-price"
                    placeholder="Min"
                    value={localPriceMin}
                    onChange={(e) => setLocalPriceMin(e.target.value)}
                    onBlur={applyPriceFilter}
                    min="0"
                    step="0.01"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    aria-label="Minimum price"
                  />
                </div>
                <span className="text-gray-500">-</span>
                <div className="flex-1">
                  <input
                    type="number"
                    id="max-price"
                    placeholder="Max"
                    value={localPriceMax}
                    onChange={(e) => setLocalPriceMax(e.target.value)}
                    onBlur={applyPriceFilter}
                    min="0"
                    step="0.01"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    aria-label="Maximum price"
                  />
                </div>
              </div>
            </div>

            {/* Level Range */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Account Level
              </label>
              <div className="flex items-center gap-2">
                <div className="flex-1">
                  <input
                    type="number"
                    id="min-level"
                    placeholder="Min"
                    value={localLevelMin}
                    onChange={(e) => setLocalLevelMin(e.target.value)}
                    onBlur={applyLevelFilter}
                    min="0"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    aria-label="Minimum level"
                  />
                </div>
                <span className="text-gray-500">-</span>
                <div className="flex-1">
                  <input
                    type="number"
                    id="max-level"
                    placeholder="Max"
                    value={localLevelMax}
                    onChange={(e) => setLocalLevelMax(e.target.value)}
                    onBlur={applyLevelFilter}
                    min="0"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    aria-label="Maximum level"
                  />
                </div>
              </div>
            </div>

            {/* Rank Dropdown */}
            <div>
              <label htmlFor="rank-filter" className="block text-sm font-medium text-gray-700 mb-2">
                Rank
              </label>
              <select
                id="rank-filter"
                value={filters.rank || ''}
                onChange={(e) => setFilter('rank', e.target.value || undefined)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                aria-label="Filter by rank"
              >
                {RANK_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Status Toggle */}
            <div>
              <label htmlFor="status-filter" className="block text-sm font-medium text-gray-700 mb-2">
                Status
              </label>
              <select
                id="status-filter"
                value={filters.status || 'APPROVED'}
                onChange={(e) => setFilter('status', e.target.value || undefined)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                aria-label="Filter by status"
              >
                {STATUS_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Footer - Show active count */}
          <div className="p-4 border-t bg-gray-50">
            <div className="text-sm text-gray-600">
              Active filters: <span className="font-semibold">{activeFilterCount}</span>
            </div>
          </div>
        </div>
      </aside>
    </>
  );
};

export default React.memo(FilterSidebar);
