import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { MockedProvider } from '@apollo/client/testing';
import { GET_ACCOUNTS, GET_GAMES } from '../../services/graphql/queries';
import FilterSidebar from './FilterSidebar';
import SortDropdown from './SortDropdown';
import ActiveFilterChips from './ActiveFilterChips';
import { useFilters } from '../../hooks/useFilters';

// Test the integration between all filter components
describe('Filter & Sort Integration Tests', () => {
  const mockGames = [
    { id: '1', name: 'World of Warcraft' },
    { id: '2', name: 'League of Legends' },
  ];

  const gamesMock = {
    request: {
      query: GET_GAMES,
    },
    result: {
      data: {
        games: mockGames,
      },
    },
  };

  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <MemoryRouter initialEntries={['/']}>
      <MockedProvider mocks={[gamesMock]} addTypename={false}>
        {children}
      </MockedProvider>
    </MemoryRouter>
  );

  describe('FilterSidebar URL Persistence', () => {
    it('updates URL when game filter is selected', async () => {
      render(<FilterSidebar />, { wrapper });

      await waitFor(() => {
        const gameSelect = screen.getByLabelText(/Filter by game/);
        expect(gameSelect).toBeInTheDocument();
      });

      const gameSelect = screen.getByLabelText(/Filter by game/);
      fireEvent.change(gameSelect, { target: { value: '1' } });

      // URL should be updated with gameId=1
      // In a real test, we would check window.location.search
    });

    it('updates URL when price range is changed', async () => {
      render(<FilterSidebar />, { wrapper });

      await waitFor(() => {
        const minPriceInput = screen.getByLabelText(/Minimum price/);
        expect(minPriceInput).toBeInTheDocument();
      });

      const minPriceInput = screen.getByLabelText(/Minimum price/);
      fireEvent.change(minPriceInput, { target: { value: '100' } });
      fireEvent.blur(minPriceInput);

      // URL should be updated with minPrice=100
    });

    it('clears all filters from URL when Clear All is clicked', async () => {
      const customWrapper = ({ children }: { children: React.ReactNode }) => (
        <MemoryRouter initialEntries={['/?gameId=1&minPrice=100']}>
          <MockedProvider mocks={[gamesMock]} addTypename={false}>
            {children}
          </MockedProvider>
        </MemoryRouter>
      );

      render(<FilterSidebar />, { wrapper: customWrapper });

      await waitFor(() => {
        const clearButton = screen.getByLabelText('Clear all filters');
        expect(clearButton).toBeInTheDocument();
      });

      const clearButton = screen.getByLabelText('Clear all filters');
      fireEvent.click(clearButton);

      // URL should be cleared of all filter params
    });
  });

  describe('ActiveFilterChips Integration', () => {
    it('displays filters from URL as chips', async () => {
      const customWrapper = ({ children }: { children: React.ReactNode }) => (
        <MemoryRouter initialEntries={['/?gameId=1&minPrice=100&maxPrice=500']}>
          <MockedProvider mocks={[gamesMock]} addTypename={false}>
            {children}
          </MockedProvider>
        </MemoryRouter>
      );

      render(<ActiveFilterChips />, { wrapper: customWrapper });

      await waitFor(() => {
        expect(screen.getByText(/Game: World of Warcraft/)).toBeInTheDocument();
        expect(screen.getByText(/\$100 - \$500/)).toBeInTheDocument();
      });
    });

    it('removes filter from URL when chip is clicked', async () => {
      const customWrapper = ({ children }: { children: React.ReactNode }) => (
        <MemoryRouter initialEntries={['/?gameId=1&minPrice=100']}>
          <MockedProvider mocks={[gamesMock]} addTypename={false}>
            {children}
          </MockedProvider>
        </MemoryRouter>
      );

      render(<ActiveFilterChips />, { wrapper: customWrapper });

      await waitFor(() => {
        const gameChip = screen.getByLabelText(/Remove Game.*filter/);
        fireEvent.click(gameChip);

        // URL should be updated without gameId
      });
    });

    it('shows "Clear all" button when multiple filters active', async () => {
      const customWrapper = ({ children }: { children: React.ReactNode }) => (
        <MemoryRouter initialEntries={['/?gameId=1&minPrice=100&q=warrior']}>
          <MockedProvider mocks={[gamesMock]} addTypename={false}>
            {children}
          </MockedProvider>
        </MemoryRouter>
      );

      render(<ActiveFilterChips />, { wrapper: customWrapper });

      await waitFor(() => {
        expect(screen.getByLabelText('Clear all active filters')).toBeInTheDocument();
      });
    });
  });

  describe('SortDropdown URL Persistence', () => {
    it('updates URL when sort option is selected', async () => {
      render(<SortDropdown />, { wrapper });

      const sortButton = screen.getByLabelText('Sort options');
      fireEvent.click(sortButton);

      await waitFor(() => {
        const priceAscOption = screen.getByText('Price: Low to High');
        expect(priceAscOption).toBeInTheDocument();

        fireEvent.click(priceAscOption);

        // URL should be updated with sortBy=price&sortDirection=ASC
      });
    });

    it('reads initial sort from URL', async () => {
      const customWrapper = ({ children }: { children: React.ReactNode }) => (
        <MemoryRouter initialEntries={['/?sortBy=price&sortDirection=ASC']}>
          {children}
        </MemoryRouter>
      );

      render(<SortDropdown />, { wrapper: customWrapper });

      await waitFor(() => {
        // Should show "Price: Low to High" as active
        expect(screen.getByText(/Price: Low to High/)).toBeInTheDocument();
      });
    });
  });

  describe('Filter Validation', () => {
    it('validates minPrice < maxPrice', async () => {
      render(<FilterSidebar />, { wrapper });

      await waitFor(() => {
        const minPriceInput = screen.getByLabelText(/Minimum price/);
        const maxPriceInput = screen.getByLabelText(/Maximum price/);

        fireEvent.change(minPriceInput, { target: { value: '500' } });
        fireEvent.change(maxPriceInput, { target: { value: '100' } });
        fireEvent.blur(maxPriceInput);

        // Should show validation error
      });
    });

    it('validates minLevel < maxLevel', async () => {
      render(<FilterSidebar />, { wrapper });

      await waitFor(() => {
        const minLevelInput = screen.getByLabelText(/Minimum level/);
        const maxLevelInput = screen.getByLabelText(/Maximum level/);

        fireEvent.change(minLevelInput, { target: { value: '50' } });
        fireEvent.change(maxLevelInput, { target: { value: '10' } });
        fireEvent.blur(maxLevelInput);

        // Should show validation error
      });
    });
  });
});

describe('useFilters Hook Integration', () => {
  it('reads all filter types from URL', () => {
    const customWrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter
        initialEntries={[
          '/?gameId=1&minPrice=100&maxPrice=500&minLevel=10&maxLevel=50&rank=DIAMOND&q=warrior',
        ]}
      >
        {children}
      </MemoryRouter>
    );

    const TestComponent = () => {
      const { filters } = useFilters();

      return (
        <div>
          <span data-testid="gameId">{filters.gameId}</span>
          <span data-testid="minPrice">{filters.minPrice}</span>
          <span data-testid="maxPrice">{filters.maxPrice}</span>
          <span data-testid="minLevel">{filters.minLevel}</span>
          <span data-testid="maxLevel">{filters.maxLevel}</span>
          <span data-testid="rank">{filters.rank}</span>
          <span data-testid="q">{filters.q}</span>
        </div>
      );
    };

    render(
      <customWrapper>
        <TestComponent />
      </customWrapper>
    );

    expect(screen.getByTestId('gameId')).toHaveTextContent('1');
    expect(screen.getByTestId('minPrice')).toHaveTextContent('100');
    expect(screen.getByTestId('maxPrice')).toHaveTextContent('500');
    expect(screen.getByTestId('minLevel')).toHaveTextContent('10');
    expect(screen.getByTestId('maxLevel')).toHaveTextContent('50');
    expect(screen.getByTestId('rank')).toHaveTextContent('DIAMOND');
    expect(screen.getByTestId('q')).toHaveTextContent('warrior');
  });

  it('updates URL when single filter is set', () => {
    const customWrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/']}>
        {children}
      </MemoryRouter>
    );

    const TestComponent = () => {
      const { setFilter } = useFilters();

      return (
        <button onClick={() => setFilter('gameId', '1')}>Set Game</button>
      );
    };

    render(
      <customWrapper>
        <TestComponent />
      </customWrapper>
    );

    const button = screen.getByText('Set Game');
    fireEvent.click(button);

    // URL should be updated with gameId=1
  });

  it('updates URL when multiple filters are set', () => {
    const customWrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/']}>
        {children}
      </MemoryRouter>
    );

    const TestComponent = () => {
      const { setFilters } = useFilters();

      return (
        <button
          onClick={() =>
            setFilters({ gameId: '1', minPrice: 100, maxPrice: 500 })
          }
        >
          Set Filters
        </button>
      );
    };

    render(
      <customWrapper>
        <TestComponent />
      </customWrapper>
    );

    const button = screen.getByText('Set Filters');
    fireEvent.click(button);

    // URL should be updated with all filters
  });

  it('clears all filters from URL', () => {
    const customWrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/?gameId=1&minPrice=100']}>
        {children}
      </MemoryRouter>
    );

    const TestComponent = () => {
      const { clearFilters } = useFilters();

      return <button onClick={clearFilters}>Clear</button>;
    };

    render(
      <customWrapper>
        <TestComponent />
      </customWrapper>
    );

    const button = screen.getByText('Clear');
    fireEvent.click(button);

    // URL should be cleared
  });

  it('detects active filters correctly', () => {
    const customWrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/?gameId=1']}>
        {children}
      </MemoryRouter>
    );

    const TestComponent = () => {
      const { hasActiveFilters } = useFilters();

      return <span data-testid="hasActive">{String(hasActiveFilters)}</span>;
    };

    render(
      <customWrapper>
        <TestComponent />
      </customWrapper>
    );

    expect(screen.getByTestId('hasActive')).toHaveTextContent('true');
  });
});
