import React from 'react';
import { useNavigate } from 'react-router-dom';

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
}

const AccountCard: React.FC<AccountCardProps> = ({ account }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/accounts/${account.id}`);
  };

  const mainImage = account.images && account.images.length > 0 ? account.images[0] : undefined;
  const gameIcon = account.game.iconUrl || '/placeholder-game.png';
  const priceDisplay = account.price.toFixed(2);
  const rating = account.seller.rating || 0;
  const reviewCount = account.seller.totalReviews || 0;

  return (
    <div
      onClick={handleClick}
      className="bg-white rounded-lg shadow-md overflow-hidden cursor-pointer hover:shadow-lg transition-shadow duration-300"
    >
      {/* Image */}
      <div className="aspect-w-16 aspect-h-9 bg-gray-200">
        {mainImage ? (
          <img
            src={mainImage}
            alt={account.title}
            className="w-full h-48 object-cover"
            loading="lazy"
          />
        ) : (
          <div className="w-full h-48 bg-gray-300 flex items-center justify-center">
            <span className="text-gray-500">No Image</span>
          </div>
        )}
      </div>

      {/* Content */}
      <div className="p-4">
        {/* Game Icon and Title */}
        <div className="flex items-center mb-2">
          <img
            src={gameIcon}
            alt={account.game.name}
            className="w-6 h-6 rounded mr-2"
            onError={(e) => {
              e.currentTarget.src = '/placeholder-game.png';
            }}
          />
          <h3 className="text-lg font-semibold text-gray-900 truncate flex-1">
            {account.title}
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
