import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import AccountCard from './AccountCard';

// Mock useNavigate
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn(),
}));

const mockAccount = {
  id: '1',
  title: 'Level 100 Warrior Account',
  description: 'High-level warrior with epic gear',
  price: 149.99,
  level: 100,
  rank: 'Diamond',
  status: 'APPROVED',
  viewsCount: 150,
  isFeatured: true,
  images: ['https://example.com/image1.jpg', 'https://example.com/image2.jpg'],
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
  seller: {
    id: 'seller1',
    fullName: 'John Doe',
    avatar: 'https://example.com/avatar.jpg',
    rating: 4.5,
    totalReviews: 25,
    email: 'john@example.com',
    role: 'SELLER',
  },
  game: {
    id: 'game1',
    name: 'World of Warcraft',
    slug: 'wow',
    iconUrl: 'https://example.com/wow-icon.png',
    description: 'Epic MMORPG',
  },
};

describe('AccountCard', () => {
  it('renders account information correctly', () => {
    render(
      <MemoryRouter>
        <AccountCard account={mockAccount} />
      </MemoryRouter>
    );

    expect(screen.getByText('Level 100 Warrior Account')).toBeInTheDocument();
    expect(screen.getByText('$149.99')).toBeInTheDocument();
    expect(screen.getByText('4.5')).toBeInTheDocument();
    expect(screen.getByText('25 reviews')).toBeInTheDocument();
  });

  it('displays main image when available', () => {
    render(
      <MemoryRouter>
        <AccountCard account={mockAccount} />
      </MemoryRouter>
    );

    const image = screen.getByAltText('Level 100 Warrior Account');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'https://example.com/image1.jpg');
  });

  it('displays game icon when available', () => {
    render(
      <MemoryRouter>
        <AccountCard account={mockAccount} />
      </MemoryRouter>
    );

    const gameIcon = screen.getByAltText('World of Warcraft');
    expect(gameIcon).toBeInTheDocument();
    expect(gameIcon).toHaveAttribute('src', 'https://example.com/wow-icon.png');
  });

  it('formats price correctly with 2 decimal places', () => {
    render(
      <MemoryRouter>
        <AccountCard account={mockAccount} />
      </MemoryRouter>
    );

    expect(screen.getByText('$149.99')).toBeInTheDocument();
  });

  it('displays "1 review" when there is exactly one review', () => {
    const accountWithOneReview = {
      ...mockAccount,
      seller: { ...mockAccount.seller, totalReviews: 1 },
    };

    render(
      <MemoryRouter>
        <AccountCard account={accountWithOneReview} />
      </MemoryRouter>
    );

    expect(screen.getByText('1 review')).toBeInTheDocument();
  });

  it('displays "No Image" placeholder when no images available', () => {
    const accountWithoutImages = {
      ...mockAccount,
      images: [],
    };

    render(
      <MemoryRouter>
        <AccountCard account={accountWithoutImages} />
      </MemoryRouter>
    );

    expect(screen.getByText('No Image')).toBeInTheDocument();
  });

  it('is clickable and navigates to account detail page', () => {
    const mockNavigate = jest.fn();
    jest.spyOn(require('react-router-dom'), 'useNavigate').mockReturnValue(mockNavigate);

    render(
      <MemoryRouter>
        <AccountCard account={mockAccount} />
      </MemoryRouter>
    );

    const card = screen.getByText('Level 100 Warrior Account').closest('div');
    fireEvent.click(card!);

    expect(mockNavigate).toHaveBeenCalledWith('/accounts/1');
  });

  it('handles missing game icon gracefully', () => {
    const accountWithoutGameIcon = {
      ...mockAccount,
      game: { ...mockAccount.game, iconUrl: undefined },
    };

    render(
      <MemoryRouter>
        <AccountCard account={accountWithoutGameIcon} />
      </MemoryRouter>
    );

    const gameIcon = screen.queryByAltText('World of Warcraft');
    expect(gameIcon).toBeInTheDocument();
    expect(gameIcon).toHaveAttribute('src', '/placeholder-game.png');
  });

  it('handles missing seller rating', () => {
    const accountWithoutRating = {
      ...mockAccount,
      seller: { ...mockAccount.seller, rating: undefined },
    };

    render(
      <MemoryRouter>
        <AccountCard account={accountWithoutRating} />
      </MemoryRouter>
    );

    expect(screen.getByText('0.0')).toBeInTheDocument();
  });
});
