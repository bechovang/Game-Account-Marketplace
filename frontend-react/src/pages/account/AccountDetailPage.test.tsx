import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { MockedProvider } from '@apollo/client/testing';
import AccountDetailPage from './AccountDetailPage';
import { GET_ACCOUNT } from '../../services/graphql/queries';
import { ADD_TO_FAVORITES, REMOVE_FROM_FAVORITES } from '../../services/graphql/mutations';

const mockAccount = {
  __typename: 'Account',
  id: '1',
  title: 'Test Account',
  description: '**Bold** description with *markdown*',
  price: 100.0,
  level: 50,
  rank: 'Diamond',
  status: 'APPROVED',
  viewsCount: 100,
  isFavorited: false,
  images: ['image1.jpg', 'image2.jpg'],
  createdAt: '2024-01-01T00:00:00',
  updatedAt: '2024-01-01T00:00:00',
  seller: {
    __typename: 'User',
    id: '1',
    fullName: 'Test Seller',
    avatar: '/avatar.jpg',
    rating: 4.5,
    totalReviews: 10,
    email: 'seller@test.com',
    role: 'SELLER'
  },
  game: {
    __typename: 'Game',
    id: '1',
    name: 'Test Game',
    slug: 'test-game',
    description: 'Test game description',
    iconUrl: '/game-icon.jpg'
  }
};

const mocks = [
  {
    request: {
      query: GET_ACCOUNT,
      variables: { id: 1 }
    },
    result: {
      data: {
        account: mockAccount
      }
    }
  }
];

describe('AccountDetailPage', () => {
  it('shows loading skeleton while fetching', () => {
    render(
      <MockedProvider mocks={[]}>
        <MemoryRouter initialEntries={['/accounts/1']}>
          <Routes>
            <Route path="/accounts/:accountId" element={<AccountDetailPage />} />
          </Routes>
        </MemoryRouter>
      </MockedProvider>
    );

    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  it('displays account details after loading', async () => {
    render(
      <MockedProvider mocks={mocks} addTypename={false}>
        <MemoryRouter initialEntries={['/accounts/1']}>
          <Routes>
            <Route path="/accounts/:accountId" element={<AccountDetailPage />} />
          </Routes>
        </MemoryRouter>
      </MockedProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('Test Account')).toBeInTheDocument();
      expect(screen.getByText('$100.00')).toBeInTheDocument();
      expect(screen.getByText('Level: 50')).toBeInTheDocument();
      expect(screen.getByText('Rank: Diamond')).toBeInTheDocument();
      expect(screen.getByText('APPROVED')).toBeInTheDocument();
    });
  });

  it('renders markdown description', async () => {
    render(
      <MockedProvider mocks={mocks} addTypename={false}>
        <MemoryRouter initialEntries={['/accounts/1']}>
          <Routes>
            <Route path="/accounts/:accountId" element={<AccountDetailPage />} />
          </Routes>
        </MemoryRouter>
      </MockedProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('Bold description with markdown')).toBeInTheDocument();
    });
  });

  it('shows error message on failure', async () => {
    const errorMock = {
      request: {
        query: GET_ACCOUNT,
        variables: { id: 1 }
      },
      error: new Error('Failed to fetch')
    };

    render(
      <MockedProvider mocks={[errorMock]}>
        <MemoryRouter initialEntries={['/accounts/1']}>
          <Routes>
            <Route path="/accounts/:accountId" element={<AccountDetailPage />} />
          </Routes>
        </MemoryRouter>
      </MockedProvider>
    );

    await waitFor(() => {
      expect(screen.getByText(/Account not found or failed to load/i)).toBeInTheDocument();
    });
  });

  it('displays favorites toggle button', async () => {
    render(
      <MockedProvider mocks={mocks} addTypename={false}>
        <MemoryRouter initialEntries={['/accounts/1']}>
          <Routes>
            <Route path="/accounts/:accountId" element={<AccountDetailPage />} />
          </Routes>
        </MemoryRouter>
      </MockedProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('Add to Favorites')).toBeInTheDocument();
    });
  });
});
