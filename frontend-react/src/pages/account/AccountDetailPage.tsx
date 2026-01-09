import React, { useEffect, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery, useMutation } from '@apollo/client';
import { GET_ACCOUNT } from '../../services/graphql/queries';
import { ADD_TO_FAVORITES, REMOVE_FROM_FAVORITES } from '../../services/graphql/mutations';
import ImageGallery from '../../components/account/ImageGallery';
import SellerCard from '../../components/account/SellerCard';
import LoadingSkeleton from '../../components/common/LoadingSkeleton';
import ErrorMessage from '../../components/common/ErrorMessage';
import ReactMarkdown from 'react-markdown';
import toast from 'react-hot-toast';

interface AccountDetailPageProps {}

const AccountDetailPage: React.FC<AccountDetailPageProps> = () => {
  const { accountId } = useParams<{ accountId: string }>();
  const incrementTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  // GraphQL query for account details
  const { data, loading, error } = useQuery(GET_ACCOUNT, {
    variables: { id: accountId ? parseInt(accountId) : 0 },
    fetchPolicy: 'cache-and-network',
    onError: (error) => {
      console.error('Failed to load account:', error);
      toast.error('Failed to load account details');
    }
  });

  // Mutations for favorites
  const [addToFavorites] = useMutation(ADD_TO_FAVORITES, {
    update: (cache, { data }) => {
      if (data?.addToFavorites) {
        cache.modify({
          id: cache.identify(data.addToFavorites),
          fields: {
            isFavorited: () => true
          }
        });
      }
    },
    onCompleted: () => {
      toast.success('Added to favorites');
    },
    onError: (error) => {
      console.error('Failed to add to favorites:', error);
      toast.error('Failed to add to favorites');
    }
  });

  const [removeFromFavorites] = useMutation(REMOVE_FROM_FAVORITES, {
    update: (cache, { data }) => {
      if (data?.removeFromFavorites) {
        cache.modify({
          id: cache.identify({
            __typename: 'Account',
            id: data?.account?.id || accountId
          }),
          fields: {
            isFavorited: () => false
          }
        });
      }
    },
    onCompleted: () => {
      toast.success('Removed from favorites');
    },
    onError: (error) => {
      console.error('Failed to remove from favorites:', error);
      toast.error('Failed to remove from favorites');
    }
  });

  // Increment view count on mount with debouncing
  useEffect(() => {
    if (accountId) {
      // Clear previous timeout if exists
      if (incrementTimeoutRef.current) {
        clearTimeout(incrementTimeoutRef.current);
      }

      // Debounce the API call by 1 second
      incrementTimeoutRef.current = setTimeout(() => {
        incrementViewCount(parseInt(accountId));
      }, 1000);

      // Cleanup function to clear timeout on unmount
      return () => {
        if (incrementTimeoutRef.current) {
          clearTimeout(incrementTimeoutRef.current);
        }
      };
    }
  }, [accountId]);

  const incrementViewCount = async (id: number) => {
    try {
      const token = localStorage.getItem('access_token');
      await fetch(`${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/accounts/${id}/view`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` })
        }
      });
    } catch (error) {
      console.error('Failed to increment view count:', error);
      // Don't show toast for view count errors - it's not critical
    }
  };

  const handleFavoriteToggle = () => {
    if (!data?.account) return;

    if (data.account.isFavorited) {
      removeFromFavorites({ variables: { accountId: data.account.id } });
    } else {
      addToFavorites({ variables: { accountId: data.account.id } });
    }
  };

  // Loading state
  if (loading) {
    return <LoadingSkeleton type="detail" />;
  }

  // Error state
  if (error || !data?.account) {
    return <ErrorMessage message="Account not found or failed to load" />;
  }

  const account = data.account;

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header with title, price, level, rank */}
      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        <div className="flex flex-col md:flex-row justify-between items-start">
          <div className="flex-1">
            <h1 className="text-3xl font-bold text-gray-900 mb-3">{account.title}</h1>
            <div className="flex flex-wrap items-center gap-4">
              <span className="text-2xl font-semibold text-green-600">
                ${account.price.toFixed(2)}
              </span>
              <span className="text-gray-600">Level: {account.level || 'N/A'}</span>
              <span className="text-gray-600">Rank: {account.rank || 'N/A'}</span>
              <span className={`px-3 py-1 rounded-full text-sm ${
                account.status === 'APPROVED'
                  ? 'bg-green-100 text-green-800'
                  : account.status === 'PENDING'
                  ? 'bg-yellow-100 text-yellow-800'
                  : 'bg-red-100 text-red-800'
              }`}>
                {account.status}
              </span>
              <span className="text-gray-500 text-sm">
                {account.viewsCount || 0} views
              </span>
            </div>
          </div>
          {/* Game info */}
          <div className="mt-4 md:mt-0 text-right">
            {account.game?.iconUrl && (
              <img
                src={account.game.iconUrl}
                alt={account.game.name}
                className="w-16 h-16 rounded inline-block"
              />
            )}
            <p className="mt-1 text-sm text-gray-600">{account.game?.name || 'Unknown Game'}</p>
          </div>
        </div>
      </div>

      {/* Image Gallery */}
      <ImageGallery images={account.images || []} />

      {/* Description */}
      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        <h2 className="text-xl font-semibold mb-4">Description</h2>
        <div className="prose max-w-none">
          <ReactMarkdown>{account.description || 'No description provided.'}</ReactMarkdown>
        </div>
      </div>

      {/* Seller Card with Actions */}
      <SellerCard
        seller={account.seller}
        account={account}
        onFavoriteToggle={handleFavoriteToggle}
        isFavorited={account.isFavorited || false}
      />
    </div>
  );
};

export default AccountDetailPage;
