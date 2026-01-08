import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { MockedProvider } from '@apollo/client/testing';
import { GET_ACCOUNTS, GET_GAMES } from '../services/graphql/queries';
import SearchPage from './SearchPage';

// Mock IntersectionObserver
global.IntersectionObserver = class IntersectionObserver {
  constructor() {}
  observe() {}
  disconnect() {}
  unobserve() {}
} as any;

const mockGames = [
  { id: '1', name: 'World of Warcraft', iconUrl: '/wow-icon.png' },
  { id: '2', name: 'League of Legends', iconUrl: '/lol-icon.png' },
];

const mockAccounts = [
  {
    id: '1',
    title: 'Epic Warrior Account',
    price: 150,
    images: ['/warrior.jpg'],
    game: { id: '1', name: 'World of Warcraft', iconUrl: '/wow-icon.png' },
    seller: { rating: 4.5, totalReviews: 10 },
  },
  {
    id: '2',
    title: 'Pro LoL Account Diamond',
    price: 200,
    images: ['/lol.jpg'],
    game: { id: '2', name: 'League of Legends', iconUrl: '/lol-icon.png' },
    seller: { rating: 5.0, totalReviews: 25 },
  },
];

const accountsMock = {
  request: {
    query: GET_ACCOUNTS,
    variables: {
      gameId: null,
      minPrice: undefined,
      maxPrice: undefined,
      minLevel: undefined,
      maxLevel: undefined,
      rank: undefined,
      status: 'APPROVED',
      sortBy: 'createdAt',
      sortDirection: 'DESC',
      page: 0,
      limit: 12,
      q: undefined,
    },
  },
  result: {
    data: {
      accounts: {
        content: mockAccounts,
        totalElements: 2,
        totalPages: 1,
        currentPage: 0,
        pageSize: 12,
      },
    },
  },
};

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

const createWrapper = (initialEntries = ['/']) => {
  return ({ children }: { children: React.ReactNode }) => (
    <MemoryRouter initialEntries={initialEntries}>
      <MockedProvider mocks={[accountsMock, gamesMock]} addTypename={false}>
        {children}
      </MockedProvider>
    </MemoryRouter>
  );
};

describe('SearchPage Integration', () => {
  it('renders search page with header and results', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByText('Browse All')).toBeInTheDocument();
      expect(screen.getByText(/2 results found/)).toBeInTheDocument();
    });
  });

  it('displays search query in header when provided', async () => {
    render(<SearchPage />, { wrapper: createWrapper(['/search?q=warrior']) });

    await waitFor(() => {
      expect(screen.getByText('Search: "warrior"')).toBeInTheDocument();
    });
  });

  it('displays accounts in grid layout', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByText('Epic Warrior Account')).toBeInTheDocument();
      expect(screen.getByText('Pro LoL Account Diamond')).toBeInTheDocument();
    });
  });

  it('shows empty state when no results found', async () => {
    const emptyMock = {
      request: {
        query: GET_ACCOUNTS,
        variables: expect.any(Object),
      },
      result: {
        data: {
          accounts: {
            content: [],
            totalElements: 0,
            totalPages: 0,
            currentPage: 0,
            pageSize: 12,
          },
        },
      },
    };

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter>
        <MockedProvider mocks={[emptyMock, gamesMock]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    render(<SearchPage />, { wrapper });

    await waitFor(() => {
      expect(screen.getByText(/No accounts found/)).toBeInTheDocument();
    });
  });

  it('shows helpful empty state message with search query', async () => {
    const emptyMock = {
      request: {
        query: GET_ACCOUNTS,
        variables: expect.any(Object),
      },
      result: {
        data: {
          accounts: {
            content: [],
            totalElements: 0,
            totalPages: 0,
            currentPage: 0,
            pageSize: 12,
          },
        },
      },
    };

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/search?q=nonexistent']}>
        <MockedProvider mocks={[emptyMock, gamesMock]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    render(<SearchPage />, { wrapper });

    await waitFor(() => {
      expect(screen.getByText(/No results found for "nonexistent"/)).toBeInTheDocument();
    });
  });

  it('displays result count', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByText(/2 results found/)).toBeInTheDocument();
    });
  });

  it('displays FilterSidebar on desktop', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      const filterSidebar = document.querySelector('[aria-label="Filter sidebar"]');
      expect(filterSidebar).toBeInTheDocument();
    });
  });

  it('displays SortDropdown in header', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByLabelText(/Sort options/)).toBeInTheDocument();
    });
  });

  it('toggles mobile filter sidebar', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      const filterButton = screen.getByLabelText('Toggle filters');
      expect(filterButton).toBeInTheDocument();
    });

    const filterButton = screen.getByLabelText('Toggle filters');
    fireEvent.click(filterButton);

    await waitFor(() => {
      const mobileSidebar = document.querySelector('[aria-label="Mobile filter sidebar"]');
      expect(mobileSidebar).toBeInTheDocument();
    });
  });

  it('has "Back" button that navigates to home', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      const backButton = screen.getByLabelText('Back to home');
      expect(backButton).toBeInTheDocument();
    });
  });

  it('displays account cards with correct data', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByText('Epic Warrior Account')).toBeInTheDocument();
      expect(screen.getByText(/\$150/)).toBeInTheDocument();
    });
  });

  it('shows loading skeleton on initial load', () => {
    const loadingMock = {
      request: {
        query: GET_ACCOUNTS,
        variables: expect.any(Object),
      },
      result: {
        data: {
          accounts: {
            content: mockAccounts,
            totalElements: 2,
            totalPages: 1,
            currentPage: 0,
            pageSize: 12,
          },
        },
      },
      delay: 100,
    };

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter>
        <MockedProvider mocks={[loadingMock, gamesMock]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    render(<SearchPage />, { wrapper });

    // LoadingSkeleton should be rendered initially
    const skeleton = document.querySelector('.animate-pulse');
    expect(skeleton).toBeInTheDocument();
  });
});

describe('SearchPage - Filter Integration', () => {
  it('displays active filter chips when filters are applied', async () => {
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/?gameId=1&minPrice=100']}>
        <MockedProvider mocks={[accountsMock, gamesMock]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    render(<SearchPage />, { wrapper });

    await waitFor(() => {
      // ActiveFilterChips should be rendered
      expect(screen.getByText(/Active filters:/)).toBeInTheDocument();
    });
  });

  it('passes filter variables to GET_ACCOUNTS query', async () => {
    const filteredAccountsMock = {
      request: {
        query: GET_ACCOUNTS,
        variables: {
          gameId: '1',
          minPrice: 100,
          maxPrice: 500,
          minLevel: undefined,
          maxLevel: undefined,
          rank: undefined,
          status: 'APPROVED',
          sortBy: 'createdAt',
          sortDirection: 'DESC',
          page: 0,
          limit: 12,
          q: undefined,
        },
      },
      result: {
        data: {
          accounts: {
            content: [mockAccounts[0]],
            totalElements: 1,
            totalPages: 1,
            currentPage: 0,
            pageSize: 12,
          },
        },
      },
    };

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/?gameId=1&minPrice=100&maxPrice=500']}>
        <MockedProvider mocks={[filteredAccountsMock, gamesMock]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    render(<SearchPage />, { wrapper });

    await waitFor(() => {
      expect(screen.getByText(/1 result found/)).toBeInTheDocument();
    });
  });
});

describe('SearchPage - Sort Integration', () => {
  it('displays sort dropdown in header', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      const sortButton = screen.getByLabelText(/Sort options/);
      expect(sortButton).toBeInTheDocument();
    });
  });

  it('passes sort variables to GET_ACCOUNTS query', async () => {
    const sortedAccountsMock = {
      request: {
        query: GET_ACCOUNTS,
        variables: {
          gameId: null,
          minPrice: undefined,
          maxPrice: undefined,
          minLevel: undefined,
          maxLevel: undefined,
          rank: undefined,
          status: 'APPROVED',
          sortBy: 'price',
          sortDirection: 'ASC',
          page: 0,
          limit: 12,
          q: undefined,
        },
      },
      result: {
        data: {
          accounts: {
            content: mockAccounts,
            totalElements: 2,
            totalPages: 1,
            currentPage: 0,
            pageSize: 12,
          },
        },
      },
    };

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/?sortBy=price&sortDirection=ASC']}>
        <MockedProvider mocks={[sortedAccountsMock, gamesMock]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    render(<SearchPage />, { wrapper });

    await waitFor(() => {
      expect(screen.getByText(/2 results found/)).toBeInTheDocument();
    });
  });
});

describe('SearchPage - Accessibility', () => {
  it('has proper heading hierarchy', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      const h1 = screen.getByRole('heading', { level: 1 });
      expect(h1).toBeInTheDocument();
    });
  });

  it('has aria-labels on interactive elements', async () => {
    render(<SearchPage />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByLabelText('Back to home')).toBeInTheDocument();
      expect(screen.getByLabelText('Sort options')).toBeInTheDocument();
      expect(screen.getByLabelText('Toggle filters')).toBeInTheDocument();
    });
  });
});
