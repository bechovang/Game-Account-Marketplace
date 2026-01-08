import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import SellerCard from './SellerCard';

describe('SellerCard', () => {
  const mockSeller = {
    id: 1,
    fullName: 'Test Seller',
    avatar: '/avatar.jpg',
    rating: 4.5,
    totalReviews: 10
  };

  const mockAccount = {
    id: 1,
    status: 'APPROVED'
  };

  const mockOnFavoriteToggle = jest.fn();

  it('displays seller information', () => {
    render(
      <SellerCard
        seller={mockSeller}
        account={mockAccount}
        onFavoriteToggle={mockOnFavoriteToggle}
        isFavorited={false}
      />
    );

    expect(screen.getByText('Test Seller')).toBeInTheDocument();
    expect(screen.getByText('4.5')).toBeInTheDocument();
    expect(screen.getByText('10 reviews')).toBeInTheDocument();
  });

  it('displays "Add to Favorites" when not favorited', () => {
    render(
      <SellerCard
        seller={mockSeller}
        account={mockAccount}
        onFavoriteToggle={mockOnFavoriteToggle}
        isFavorited={false}
      />
    );

    expect(screen.getByText('Add to Favorites')).toBeInTheDocument();
  });

  it('displays "Remove from Favorites" when favorited', () => {
    render(
      <SellerCard
        seller={mockSeller}
        account={mockAccount}
        onFavoriteToggle={mockOnFavoriteToggle}
        isFavorited={true}
      />
    );

    expect(screen.getByText('Remove from Favorites')).toBeInTheDocument();
  });

  it('calls onFavoriteToggle when favorites button is clicked', () => {
    render(
      <SellerCard
        seller={mockSeller}
        account={mockAccount}
        onFavoriteToggle={mockOnFavoriteToggle}
        isFavorited={false}
      />
    );

    const favoriteButton = screen.getByText('Add to Favorites');
    fireEvent.click(favoriteButton);

    expect(mockOnFavoriteToggle).toHaveBeenCalledTimes(1);
  });

  it('shows "Buy Now" button when account is APPROVED', () => {
    render(
      <SellerCard
        seller={mockSeller}
        account={{ ...mockAccount, status: 'APPROVED' }}
        onFavoriteToggle={mockOnFavoriteToggle}
        isFavorited={false}
      />
    );

    expect(screen.getByText('Buy Now')).toBeInTheDocument();
  });

  it('does not show "Buy Now" button when account is PENDING', () => {
    render(
      <SellerCard
        seller={mockSeller}
        account={{ ...mockAccount, status: 'PENDING' }}
        onFavoriteToggle={mockOnFavoriteToggle}
        isFavorited={false}
      />
    );

    expect(screen.queryByText('Buy Now')).not.toBeInTheDocument();
  });

  it('shows "Chat with Seller" button', () => {
    render(
      <SellerCard
        seller={mockSeller}
        account={mockAccount}
        onFavoriteToggle={mockOnFavoriteToggle}
        isFavorited={false}
      />
    );

    expect(screen.getByText('Chat with Seller')).toBeInTheDocument();
  });
});
