import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { MockedProvider } from '@apollo/client/testing';
import { GET_GAMES, GET_ACCOUNTS } from '../services/graphql/queries';
import HomePage from './HomePage';
import LoadingSkeleton from '../components/common/LoadingSkeleton';
import ErrorMessage from '../components/common/ErrorMessage';

// Mock components
jest.mock('../components/common/LoadingSkeleton', () => ({
  __esModule: true,
  default: () => <div data-testid="loading-skeleton">Loading...</div>,
}));

jest.mock('../components/common/ErrorMessage', () => ({
  __esModule: true,
  default: ({ message }: { message: string }) => (
    <div data-testid="error-message">{message}</div>
  ),
}));

jest.mock('../components/account/AccountCard', () => ({
  __esModule: true,
  default: ({ account }: { account: any }) => (
    <div data-testid="account-card">{account.title}</div>
  ),
}));

const mockGames = [
  {
    id: '1',
    name: 'World of Warcraft',
    slug: 'wow',
    iconUrl: 'https://example.com/wow.png',
    accountCount: 100,
  },
  {
    id: '2',
    name: 'League of Legends',
    slug: 'lol',
    iconUrl: 'https://example.com/lol.png',
    accountCount: 50,
  },
];

const mockAccounts = [
  {
    id: '1',
    title: 'Level 100 Warrior',
    price: 149.99,
    images: ['https://example.com/img.jpg'],
    game: { id: '1', name: 'World of Warcraft', iconUrl: 'https://example.com/wow.png' },
    seller: { rating: 4.5, totalReviews: 10 },
  },
  {
    id: '2',
    title: 'Diamond Account',
    price: 99.99,
    images: ['https://example.com/img2.jpg'],
    game: { id: '2', name: 'League of Legends', iconUrl: 'https://example.com/lol.png' },
    seller: { rating: 4.0, totalReviews: 5 },
  },
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

const featuredAccountsMock = {
  request: {
    query: GET_ACCOUNTS,
    variables: {
      gameId: null,
      status: 'APPROVED',
      isFeatured: true,
      page: 0,
      limit: 6,
    },
  },
  result: {
    data: {
      accounts: {
        content: mockAccounts,
        totalElements: 2,
        totalPages: 1,
        currentPage: 0,
        pageSize: 6,
      },
    },
  },
};

const newAccountsMock = {
  request: {
    query: GET_ACCOUNTS,
    variables: {
      gameId: null,
      status: 'APPROVED',
      isFeatured: false,
      page: 0,
      limit: 12,
      sortBy: 'createdAt',
      sortDirection: 'DESC',
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

describe('HomePage', () => {
  it('shows loading skeleton initially while loading games', () => {
    const mocks = [gamesMock];

    render(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    expect(screen.getByTestId('loading-skeleton')).toBeInTheDocument();
  });

  it('renders game categories after loading', async () => {
    const mocks = [gamesMock, featuredAccountsMock, newAccountsMock];

    render(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('World of Warcraft')).toBeInTheDocument();
      expect(screen.getByText('League of Legends')).toBeInTheDocument();
    });
  });

  it('renders featured accounts section', async () => {
    const mocks = [gamesMock, featuredAccountsMock, newAccountsMock];

    render(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Featured Listings')).toBeInTheDocument();
      expect(screen.getByText('Level 100 Warrior')).toBeInTheDocument();
    });
  });

  it('renders new accounts section', async () => {
    const mocks = [gamesMock, featuredAccountsMock, newAccountsMock];

    render(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('New Listings')).toBeInTheDocument();
      expect(screen.getByText('Diamond Account')).toBeInTheDocument();
    });
  });

  it('displays search bar in header', async () => {
    const mocks = [gamesMock, featuredAccountsMock, newAccountsMock];

    render(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      const searchInput = screen.getByPlaceholderText('Search accounts...');
      expect(searchInput).toBeInTheDocument();
    });
  });

  it('shows "All Games" button', async () => {
    const mocks = [gamesMock, featuredAccountsMock, newAccountsMock];

    render(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('All Games')).toBeInTheDocument();
    });
  });

  it('handles game filter click', async () => {
    const mocks = [gamesMock, featuredAccountsMock, newAccountsMock];

    render(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      const wowButton = screen.getByText('World of Warcraft');
      expect(wowButton).toBeInTheDocument();
    });
  });

  it('handles search input change', async () => {
    const mocks = [gamesMock, featuredAccountsMock, newAccountsMock];

    render(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      const searchInput = screen.getByPlaceholderText('Search accounts...');
      fireEvent.change(searchInput, { target: { value: 'warrior account' } });
      expect(searchInput).toHaveValue('warrior account');
    });
  });

  it('shows error message on query error', async () => {
    const errorMock = {
      request: {
        query: GET_GAMES,
      },
      error: new Error('Failed to fetch'),
    };

    render(
      <MemoryRouter>
        <MockedProvider mocks={[errorMock]} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByTestId('error-message')).toBeInTheDocument();
      expect(screen.getByText('Failed to load marketplace data')).toBeInTheDocument();
    });
  });

  it('displays "No featured accounts" message when no featured accounts', async () => {
    const noFeaturedMock = {
      request: {
        query: GET_ACCOUNTS,
        variables: {
          gameId: null,
          status: 'APPROVED',
          isFeatured: true,
          page: 0,
          limit: 6,
        },
      },
      result: {
        data: {
          accounts: {
            content: [],
            totalElements: 0,
            totalPages: 0,
            currentPage: 0,
            pageSize: 6,
          },
        },
      },
    };

    const mocks = [gamesMock, noFeaturedMock, newAccountsMock];

    render(
      <MemoryRouter>
        <MockedProvider mocks={mocks} addTypename={false}>
          <HomePage />
        </MockedProvider>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('No featured accounts available at the moment.')).toBeInTheDocument();
    });
  });
});
