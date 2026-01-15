import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

interface SellerCardProps {
  seller: {
    id: number;
    fullName: string;
    avatar: string;
    rating: number;
    totalReviews: number;
  };
  account: {
    id: number;
    status: string;
  };
  onFavoriteToggle: () => void;
  isFavorited: boolean;
}

const SellerCard: React.FC<SellerCardProps> = ({
  seller,
  account,
  onFavoriteToggle,
  isFavorited
}) => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const handleChatClick = () => {
    if (!user) {
      // If not logged in, redirect to login
      navigate('/login');
      return;
    }

    // If the user is the seller, show a message
    if (user.id === seller.id) {
      alert('You cannot chat with yourself!');
      return;
    }

    // Navigate to chat page with this account as a query parameter
    navigate(`/chat?accountId=${account.id}`);
  };
  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex items-center mb-4">
        <img
          src={seller.avatar || '/default-avatar.png'}
          alt={seller.fullName}
          className="w-16 h-16 rounded-full mr-4"
        />
        <div>
          <h3 className="text-lg font-semibold">{seller.fullName}</h3>
          <div className="flex items-center text-sm text-gray-600">
            <span className="text-yellow-500 mr-1">★</span>
            <span>{seller.rating.toFixed(1)}</span>
            <span className="mx-2">•</span>
            <span>{seller.totalReviews} reviews</span>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex flex-col md:flex-row space-y-2 md:space-y-0 md:space-x-4">
        <button
          onClick={handleChatClick}
          className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors"
        >
          Chat with Seller
        </button>
        <button
          onClick={onFavoriteToggle}
          className={`flex-1 py-2 px-4 rounded-lg transition-colors ${
            isFavorited
              ? 'bg-red-100 text-red-700 hover:bg-red-200'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          {isFavorited ? 'Remove from Favorites' : 'Add to Favorites'}
        </button>
        {account.status === 'APPROVED' && (
          <button
            className="flex-1 bg-green-600 text-white py-2 px-4 rounded-lg hover:bg-green-700 transition-colors"
          >
            Buy Now
          </button>
        )}
      </div>
    </div>
  );
};

export default SellerCard;
