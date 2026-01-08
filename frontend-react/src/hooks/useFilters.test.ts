import { renderHook, act, waitFor } from '@testing-library/react';
import { MemoryRouter, RouterWrapper } from 'react-router-dom';
import { useFilters, validateFilters } from './useFilters';

// Wrapper for router context
const wrapper = ({ children }: { children: React.ReactNode }) => (
  <MemoryRouter>{children}</MemoryRouter>
);

describe('useFilters', () => {
  it('reads initial filters from URL', () => {
    const { result } = renderHook(() => useFilters(), { wrapper });

    expect(result.current.filters.sortBy).toBe('createdAt');
    expect(result.current.filters.sortDirection).toBe('DESC');
    expect(result.current.filters.status).toBe('APPROVED');
  });

  it('updates URL when filters change', async () => {
    const { result } = renderHook(() => useFilters(), { wrapper });

    act(() => {
      result.current.setFilter('gameId', 'game-123');
    });

    await waitFor(() => {
      expect(result.current.filters.gameId).toBe('game-123');
    });
  });

  it('sets multiple filters at once', async () => {
    const { result } = renderHook(() => useFilters(), { wrapper });

    act(() => {
      result.current.setFilters({
        gameId: 'game-1',
        minPrice: 100,
        maxPrice: 500,
      });
    });

    await waitFor(() => {
      expect(result.current.filters.gameId).toBe('game-1');
      expect(result.current.filters.minPrice).toBe(100);
      expect(result.current.filters.maxPrice).toBe(500);
    });
  });

  it('clears all filters', async () => {
    const { result } = renderHook(() => useFilters(), { wrapper });

    // Set some filters first
    act(() => {
      result.current.setFilters({
        gameId: 'game-1',
        minPrice: 100,
        q: 'warrior',
      });
    });

    // Clear them
    act(() => {
      result.current.clearFilters();
    });

    await waitFor(() => {
      expect(result.current.filters.gameId).toBeUndefined();
      expect(result.current.filters.minPrice).toBeUndefined();
      expect(result.current.filters.q).toBeUndefined();
      // Defaults should remain
      expect(result.current.filters.sortBy).toBe('createdAt');
      expect(result.current.filters.status).toBe('APPROVED');
    });
  });

  it('removes filter when value is undefined', async () => {
    const { result } = renderHook(() => useFilters(), { wrapper });

    // Set a filter
    act(() => {
      result.current.setFilter('gameId', 'game-123');
    });

    await waitFor(() => {
      expect(result.current.filters.gameId).toBe('game-123');
    });

    // Remove it
    act(() => {
      result.current.setFilter('gameId', undefined);
    });

    await waitFor(() => {
      expect(result.current.filters.gameId).toBeUndefined();
    });
  });

  it('calculates active filter count correctly', () => {
    const { result } = renderHook(() => useFilters(), { wrapper });

    expect(result.current.activeFilterCount).toBe(0);
    expect(result.current.hasActiveFilters).toBe(false);

    act(() => {
      result.current.setFilters({
        gameId: 'game-1',
        minPrice: 100,
        maxPrice: 500,
      });
    });

    waitFor(() => {
      expect(result.current.activeFilterCount).toBe(3);
      expect(result.current.hasActiveFilters).toBe(true);
    });
  });

  it('does not count defaults as active filters', () => {
    const { result } = renderHook(() => useFilters(), { wrapper });

    waitFor(() => {
      // sortBy, sortDirection, and status are defaults
      expect(result.current.hasActiveFilters).toBe(false);
      expect(result.current.activeFilterCount).toBe(0);
    });
  });

  it('removes page parameter when filters change', async () => {
    const { result } = renderHook(() => useFilters(), { wrapper });

    // Simulate having a page parameter
    act(() => {
      result.current.setFilter('page', '2');
    });

    act(() => {
      result.current.setFilter('gameId', 'game-1');
    });

    await waitFor(() => {
      // Page should be removed when filters change
      expect(result.current.filters.gameId).toBe('game-1');
    });
  });
});

describe('validateFilters', () => {
  it('passes validation for valid filters', () => {
    expect(() => {
      validateFilters({
        minPrice: 0,
        maxPrice: 1000,
        minLevel: 1,
        maxLevel: 100,
      });
    }).not.toThrow();
  });

  it('throws error when minPrice > maxPrice', () => {
    expect(() => {
      validateFilters({
        minPrice: 500,
        maxPrice: 100,
      });
    }).toThrow('Minimum price cannot be greater than maximum price');
  });

  it('throws error when minLevel > maxLevel', () => {
    expect(() => {
      validateFilters({
        minLevel: 50,
        maxLevel: 10,
      });
    }).toThrow('Minimum level cannot be greater than maximum level');
  });

  it('throws error when minPrice is negative', () => {
    expect(() => {
      validateFilters({
        minPrice: -10,
      });
    }).toThrow('Minimum price cannot be negative');
  });

  it('throws error when maxPrice is negative', () => {
    expect(() => {
      validateFilters({
        maxPrice: -5,
      });
    }).toThrow('Maximum price cannot be negative');
  });

  it('throws error when minLevel is negative', () => {
    expect(() => {
      validateFilters({
        minLevel: -1,
      });
    }).toThrow('Minimum level cannot be negative');
  });

  it('throws error when maxLevel is negative', () => {
    expect(() => {
      validateFilters({
        maxLevel: -5,
      });
    }).toThrow('Maximum level cannot be negative');
  });

  it('allows equal min and max values', () => {
    expect(() => {
      validateFilters({
        minPrice: 100,
        maxPrice: 100,
        minLevel: 50,
        maxLevel: 50,
      });
    }).not.toThrow();
  });

  it('allows undefined values', () => {
    expect(() => {
      validateFilters({});
    }).not.toThrow();
  });
});
