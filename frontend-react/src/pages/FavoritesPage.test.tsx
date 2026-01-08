import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { MockedProvider } from '@apollo/client/testing';
import { GET_FAVORITES } from '../../services/graphql/queries';
import FavoritesPage from '../FavoritesPage';
import LoadingSkeleton from '../../components/common/LoadingSkeleton';

const mockFavorites = [
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

const favoritesMock = {
  request: {
    query: GET_FAVORITES,
    variables: {
      page: 0,
      limit: 20,
    },
  },
  result: {
    data: {
      favorites: {
        content: mockFavorites,
        totalElements: 2,
        totalPages: 1,
        currentPage: 0,
        pageSize: 20,
      },
    },
  },
};

const emptyFavoritesMock = {
  request: {
    query: GET_FAVORITES,
    variables: {
      page: 0,
      limit: 20,
    },
  },
  result: {
    data: {
      favorites: {
        content: [],
        totalElements: 0,
        totalPages: 0,
        currentPage: 0,
        pageSize: 20,
      },
    },
  },
};

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <MemoryRouter initialEntries={['/favorites']}>
    <MockedProvider mocks={[favoritesMock]} addTypename={false}>
      {children}
    </MockedProvider>
  </MemoryRouter>
);

describe('FavoritesPage', () => {
  it('renders favorites in grid layout', async () => {
    render(<FavoritesPage />, { wrapper });

    await waitFor(() => {
      expect(screen.getByText('My Favorites')).toBeInTheDocument();
      expect(screen.getByText(/2 favorites/)).toBeInTheDocument();
    });
  });

  it('shows loading skeleton while fetching', () => {
    const customWrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/favorites']}>
        <MockedProvider mocks={[]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    const { container } = render(<FavoritesPage />, { wrapper: customWrapper });
    expect(container.querySelector('.animate-pulse')).toBeInTheDocument();
  });

  it('shows empty state when no favorites', async () => {
    const customWrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/favorites']}>
        <MockedProvider mocks={[emptyFavoritesMock]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    render(<FavoritesPage />, { wrapper: customWrapper });

    await waitFor(() => {
      expect(screen.getByText('No favorites yet')).toBeInTheDocument();
      expect(screen.getByText(/Start saving accounts you're interested in/)).toBeInTheDocument();
      expect(screen.getByText('Browse Listings')).toBeInTheDocument();
    });
  });

  it('shows error message if query fails', async () => {
    const errorMock = {
      request: {
        query: GET_FAVORITES,
        variables: {
          page: 0,
          limit: 20,
        },
      },
      error: new Error('Failed to fetch'),
    };

    const customWrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/favorites']}>
        <MockedProvider mocks={[errorMock]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    render(<FavoritesPage />, { wrapper: customWrapper });

    await waitFor(() => {
      expect(screen.getByText('Failed to load favorites')).toBeInTheDocument();
    });
  });

  it('displays favorite count', async () => {
    render(<FavoritesPage />, { wrapper });

    await waitFor(() => {
      expect(screen.getByText(/2 favorites/)).toBeInTheDocument();
    });
  });

  it('displays singular "favorite" when count is 1', async () => {
    const singleFavoriteMock = {
      request: {
        query: GET_FAVORITES,
        variables: {
          page: 0,
          limit: 20,
        },
      },
      result: {
        data: {
          favorites: {
            content: [mockFavorites[0]],
            totalElements: 1,
            totalPages: 1,
            currentPage: 0,
            pageSize: 20,
          },
        },
      },
    };

    const customWrapper = ({ children }: { children: React.ReactNode }) => (
      <MemoryRouter initialEntries={['/favorites']}>
        <MockedProvider mocks={[singleFavoriteMock]} addTypename={false}>
          {children}
        </MockedProvider>
      </MemoryRouter>
    );

    render(<FavoritesPage />, { wrapper: customWrapper });

    await waitFor(() => {
      expect(screen.getByText(/1 favorite/)).toBeInTheDocument();
    });
  });
});
