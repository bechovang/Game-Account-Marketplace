import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useApolloClient } from '@apollo/client';
import { removeAccountFromCache, updateAccountInCache } from '../../utils/apolloCache';
import type { AccountStatusChangedMessage } from '../../types/accountUpdates';

interface Account {
  id: string;
  title: string;
  description?: string;
  price: number;
  level?: number;
  rank?: string;
  status: string;
  viewsCount?: number;
  isFeatured?: boolean;
  images?: string[];
  createdAt?: string;
  updatedAt?: string;
  seller: {
    id: string;
    fullName: string;
    avatar?: string;
    rating?: number;
    totalReviews?: number;
    email?: string;
    role?: string;
  };
  game: {
    id: string;
    name: string;
    slug: string;
    iconUrl?: string;
    description?: string;
  };
}

interface AccountCardProps {
  account: Account;
  onRemove?: (accountId: string) => void;
}

const AccountCard: React.FC<AccountCardProps> = ({ account, onRemove }) => {
  const navigate = useNavigate();
  const client = useApolloClient();
  const [iconFailed, setIconFailed] = useState(false);
  const [isRemoving, setIsRemoving] = useState(false);
  const [currentAccount, setCurrentAccount] = useState(account);

  // Listen for real-time account status changes
  useEffect(() => {
    const handleAccountUpdate = (event: CustomEvent<AccountStatusChangedMessage>) => {
      const message = event.detail;

      // Only process if this update is for this account
      if (message.accountId !== account.id) return;

      if (message.newStatus === 'SOLD') {
        // Animate removal then remove from cache
        setIsRemoving(true);
        setTimeout(() => {
          removeAccountFromCache(client, account.id);
          onRemove?.(account.id);
        }, 300); // Match animation duration
      } else {
        // Update status in cache and local state
        updateAccountInCache(client, account.id, { status: message.newStatus });
        setCurrentAccount((prev) => ({ ...prev, status: message.newStatus }));
      }
    };

    window.addEventListener('account-update', handleAccountUpdate as EventListener);

    return () => {
      window.removeEventListener('account-update', handleAccountUpdate as EventListener);
    };
  }, [account.id, account, client, onRemove]);

  // If removing, show fade-out animation
  if (isRemoving) {
    return (
      <div
        className="bg-white rounded-lg shadow-md overflow-hidden opacity-0 transition-opacity duration-300"
        style={{ height: '320px' }}
      />
    );
  }

  const handleClick = () => {
    navigate(`/accounts/${currentAccount.id}`);
  };

  const mainImage = currentAccount.images && currentAccount.images.length > 0 ? currentAccount.images[0] : undefined;
  const gameIcon = currentAccount.game.iconUrl && !iconFailed ? currentAccount.game.iconUrl : null;
  const priceDisplay = currentAccount.price.toFixed(2);
  const rating = currentAccount.seller.rating || 0;
  const reviewCount = currentAccount.seller.totalReviews || 0;

  // Status badge styling
  const getStatusBadgeStyle = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'SOLD':
        return 'bg-red-100 text-red-800 border-red-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  return (
    <div
      onClick={handleClick}
      className={`bg-white rounded-lg shadow-md overflow-hidden cursor-pointer hover-lift ${isRemoving ? 'opacity-0 transition-opacity duration-300' : ''}`}
    >
      {/* Image */}
      <div className="aspect-w-16 aspect-h-9 bg-gray-200 relative">
        {mainImage ? (
          <img
            src={mainImage}
            alt={currentAccount.title}
            className="w-full h-48 object-cover"
            loading="lazy"
          />
        ) : (
          <div className="w-full h-48 bg-gray-300 flex items-center justify-center">
            <span className="text-gray-500">No Image</span>
          </div>
        )}
        {/* Status Badge */}
        <div className={`absolute top-2 right-2 px-3 py-1 rounded-full text-xs font-semibold border ${getStatusBadgeStyle(currentAccount.status)}`}>
          {currentAccount.status}
        </div>
      </div>

      {/* Content */}
      <div className="p-4">
        {/* Game Icon and Title */}
        <div className="flex items-center mb-2">
          {gameIcon && (
            <img
              src={gameIcon}
              alt={currentAccount.game.name}
              className="w-6 h-6 rounded mr-2"
              onError={() => setIconFailed(true)}
            />
          )}
          <h3 className="text-lg font-semibold text-gray-900 truncate flex-1">
            {currentAccount.title}
          </h3>
        </div>

        {/* Price */}
        <div className="text-xl font-bold text-green-600 mb-2">
          ${priceDisplay}
        </div>

        {/* Seller Rating */}
        <div className="flex items-center text-sm text-gray-600">
          <span className="text-yellow-500 mr-1">★</span>
          <span>{rating.toFixed(1)}</span>
          <span className="mx-1">•</span>
          <span>{reviewCount} {reviewCount === 1 ? 'review' : 'reviews'}</span>
        </div>
      </div>
    </div>
  );
};

export default AccountCard;
