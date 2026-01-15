import { useCallback, useMemo, useState, useEffect, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';

export interface AccountFilters {
  gameId?: string;
  minPrice?: number;
  maxPrice?: number;
  minLevel?: number;
  maxLevel?: number;
  rank?: string;
  status?: string;
  sortBy?: string;
  sortDirection?: string;
  q?: string;
}

interface UseFiltersResult {
  filters: AccountFilters;
  setFilter: (key: keyof AccountFilters, value: string | number | undefined | null) => void;
  setFilters: (filters: Partial<AccountFilters>) => void;
  clearFilters: () => void;
  hasActiveFilters: boolean;
  activeFilterCount: number;
}

const DEFAULT_SORT = 'createdAt';
const DEFAULT_SORT_DIRECTION = 'DESC';
const DEFAULT_STATUS = 'APPROVED';

/**
 * Custom hook for managing account filters with URL state persistence.
 * Provides filter validation, URL synchronization, and helper functions.
 */
export const useFilters = (): UseFiltersResult => {
  const [searchParams, setSearchParams] = useSearchParams();

  // Parse filters from URL search params
  const filters = useMemo<AccountFilters>(() => {
    return {
      gameId: searchParams.get('gameId') || undefined,
      minPrice: searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : undefined,
      maxPrice: searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : undefined,
      minLevel: searchParams.get('minLevel') ? Number(searchParams.get('minLevel')) : undefined,
      maxLevel: searchParams.get('maxLevel') ? Number(searchParams.get('maxLevel')) : undefined,
      rank: searchParams.get('rank') || undefined,
      status: searchParams.get('status') || undefined,
      sortBy: searchParams.get('sortBy') || DEFAULT_SORT,
      sortDirection: searchParams.get('sortDirection') || DEFAULT_SORT_DIRECTION,
      q: searchParams.get('q') || undefined,
    };
  }, [searchParams]);

  // Set a single filter
  const setFilter = useCallback((key: keyof AccountFilters, value: string | number | undefined | null) => {
    const params = new URLSearchParams(searchParams);

    if (value === undefined || value === null || value === '') {
      params.delete(key);
    } else {
      params.set(key, String(value));
    }

    // Reset to page 0 when filters change
    params.delete('page');

    setSearchParams(params);
  }, [searchParams, setSearchParams]);

  // Set multiple filters at once
  const setFilters = useCallback((newFilters: Partial<AccountFilters>) => {
    const params = new URLSearchParams(searchParams);

    Object.entries(newFilters).forEach(([key, value]) => {
      if (value === undefined || value === '') {
        params.delete(key);
      } else {
        params.set(key, String(value));
      }
    });

    // Reset to page 0 when filters change
    params.delete('page');

    setSearchParams(params);
  }, [searchParams, setSearchParams]);

  // Clear all filters (keep sort as default)
  const clearFilters = useCallback(() => {
    const params = new URLSearchParams();

    // Set default sort only
    params.set('sortBy', DEFAULT_SORT);
    params.set('sortDirection', DEFAULT_SORT_DIRECTION);

    setSearchParams(params);
  }, [setSearchParams]);

  // Check if there are active filters (excluding defaults)
  const hasActiveFilters = useMemo(() => {
    return !!(
      filters.gameId ||
      filters.minPrice !== undefined ||
      filters.maxPrice !== undefined ||
      filters.minLevel !== undefined ||
      filters.maxLevel !== undefined ||
      filters.rank ||
      filters.q
    );
  }, [filters]);

  // Count active filters
  const activeFilterCount = useMemo(() => {
    let count = 0;
    if (filters.gameId) count++;
    if (filters.minPrice !== undefined) count++;
    if (filters.maxPrice !== undefined) count++;
    if (filters.minLevel !== undefined) count++;
    if (filters.maxLevel !== undefined) count++;
    if (filters.rank) count++;
    if (filters.q) count++;
    return count;
  }, [filters]);

  return {
    filters,
    setFilter,
    setFilters,
    clearFilters,
    hasActiveFilters,
    activeFilterCount,
  };
};

/**
 * Validate filter values
 * @throws Error if validation fails
 */
export const validateFilters = (filters: Partial<AccountFilters>): void => {
  if (filters.minPrice !== undefined && filters.maxPrice !== undefined) {
    if (filters.minPrice > filters.maxPrice) {
      throw new Error('Minimum price cannot be greater than maximum price');
    }
  }

  if (filters.minLevel !== undefined && filters.maxLevel !== undefined) {
    if (filters.minLevel > filters.maxLevel) {
      throw new Error('Minimum level cannot be greater than maximum level');
    }
  }

  if (filters.minPrice !== undefined && filters.minPrice < 0) {
    throw new Error('Minimum price cannot be negative');
  }

  if (filters.maxPrice !== undefined && filters.maxPrice < 0) {
    throw new Error('Maximum price cannot be negative');
  }

  if (filters.minLevel !== undefined && filters.minLevel < 0) {
    throw new Error('Minimum level cannot be negative');
  }

  if (filters.maxLevel !== undefined && filters.maxLevel < 0) {
    throw new Error('Maximum level cannot be negative');
  }
};

/**
 * Hook for debounced search input with 300ms delay
 * Provides smooth UX by waiting for user to finish typing
 */
export const useDebouncedSearch = (delay: number = 300) => {
  const [searchParams] = useSearchParams();
  const [searchTerm, setSearchTerm] = useState(searchParams.get('q') || '');
  const [debouncedSearch, setDebouncedSearch] = useState(searchTerm);
  const timeoutRef = useRef<NodeJS.Timeout>();

  // Debounce the search term
  useEffect(() => {
    // Clear existing timeout
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }

    // Set new timeout
    timeoutRef.current = setTimeout(() => {
      setDebouncedSearch(searchTerm);
    }, delay);

    // Cleanup on unmount or when searchTerm changes
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, [searchTerm, delay]);

  // Sync with URL params when they change externally
  useEffect(() => {
    const urlSearch = searchParams.get('q') || '';
    if (urlSearch !== searchTerm) {
      setSearchTerm(urlSearch);
    }
  }, [searchParams]);

  return {
    searchTerm,
    setSearchTerm,
    debouncedSearch,
    isDebouncing: searchTerm !== debouncedSearch
  };
};
