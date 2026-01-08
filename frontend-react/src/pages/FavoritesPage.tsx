import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useQuery } from '@apollo/client';
import { useNavigate } from 'react-router-dom';
import { GET_FAVORITES } from '../services/graphql/queries';
import AccountCard from '../components/account/AccountCard';
import RemoveFavoriteButton from '../components/favorites/RemoveFavoriteButton';
import LoadingSkeleton from '../components/common/LoadingSkeleton';
import ErrorMessage from '../components/common/ErrorMessage';

interface FavoritesPageProps {}

interface Account {
  id: string;
  title: string;
  price: number;
  images?: string[];
  game: {
    id: string;
    name: string;
    iconUrl?: string;
  };
  seller: {
    rating?: number;
    totalReviews?: number;
  };
}

interface FavoritesResponse {
  content: Account[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

const FavoritesPage: React.FC<FavoritesPageProps> = () => {
  const navigate = useNavigate();
  const [currentPage, setCurrentPage] = useState(0);
  const [allFavorites, setAllFavorites] = useState<Account[]>([]);
  const observerTarget = useRef<HTMLDivElement>(null);
  const isFetchingRef = useRef(false);

  // Favorites query
  // AC #1: FavoritesPage sorts by createdAt descending (newest first)
  const { data: favoritesData, loading, error, refetch } = useQuery(GET_FAVORITES, {
    variables: {
      page: currentPage,
      limit: 20,
      sortBy: 'createdAt',
      sortDirection: 'DESC',
    },
    fetchPolicy: 'cache-and-network',
    onCompleted: (data) => {
      if (currentPage === 0) {
        setAllFavorites(data.favorites.content);
      } else {
        setAllFavorites((prev) => [...prev, ...data.favorites.content]);
      }
      isFetchingRef.current = false;
    },
  });


  // Reset favorites when pagination resets
  useEffect(() => {
    setCurrentPage(0);
    setAllFavorites([]);
    isFetchingRef.current = false;
  }, []);

  // Memoized load more function
  const loadMoreFavorites = useCallback(() => {
    if (isFetchingRef.current || loading) {
      return;
    }

    const newFavoritesResponse = favoritesData?.favorites as FavoritesResponse | undefined;
    if (newFavoritesResponse && currentPage < newFavoritesResponse.totalPages - 1) {
      isFetchingRef.current = true;
      setCurrentPage((prev) => prev + 1);
      refetch({
        page: currentPage + 1,
        limit: 20,
        sortBy: 'createdAt',
        sortDirection: 'DESC',
      });
    }
  }, [currentPage, loading, favoritesData, refetch]);

  // Intersection Observer for infinite scroll
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadMoreFavorites();
        }
      },
      { threshold: 0.1 }
    );

    const currentTarget = observerTarget.current;
    if (currentTarget) {
      observer.observe(currentTarget);
    }

    return () => {
      if (currentTarget) {
        observer.unobserve(currentTarget);
      }
      observer.disconnect();
    };
  }, [loadMoreFavorites]);

  // Handle remove after button action
  const handleRemoved = useCallback(() => {
    // Refetch favorites after successful removal
    setCurrentPage(0);
    setAllFavorites([]);
    isFetchingRef.current = false;
    refetch({
      page: 0,
      limit: 20,
      sortBy: 'createdAt',
      sortDirection: 'DESC',
    });
  }, [refetch]);

  if (loading && currentPage === 0) {
    return <LoadingSkeleton type="grid" />;
  }

  if (error) {
    return <ErrorMessage message="Failed to load favorites" />;
  }

  const favoritesResponse = favoritesData?.favorites as FavoritesResponse | undefined;
  const hasMorePages = favoritesResponse ? currentPage < favoritesResponse.totalPages - 1 : false;
  const favoritesCount = favoritesResponse?.totalElements || 0;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="container mx-auto px-4 py-6">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">My Favorites</h1>
          {favoritesCount > 0 && (
            <p className="text-gray-600">
              {favoritesCount} {favoritesCount === 1 ? 'favorite' : 'favorites'}
            </p>
          )}
        </div>
      </header>

      <main className="container mx-auto px-4 py-8">
        {allFavorites.length === 0 && !loading ? (
          // Empty state
          <div className="text-center py-16 bg-white rounded-lg shadow-md">
            <svg
              className="w-20 h-20 mx-auto text-gray-300 mb-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
              />
            </svg>
            <h2 className="text-2xl font-semibold text-gray-700 mb-2">No favorites yet</h2>
            <p className="text-gray-500 mb-6">
              Start saving accounts you're interested in by clicking the heart icon on any listing.
            </p>
            <button
              onClick={() => navigate('/')}
              className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Browse Listings
            </button>
          </div>
        ) : (
          <>
            {/* Favorites grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {allFavorites.map((account: Account) => (
                <div key={account.id} className="relative">
                  {/* Remove button overlay */}
                  <div className="absolute top-2 right-2 z-10">
                    <RemoveFavoriteButton
                      accountId={account.id}
                      onRemove={handleRemoved}
                    />
                  </div>

                  {/* Account card */}
                  <AccountCard account={account} />
                </div>
              ))}
            </div>

            {/* Loading indicator */}
            {loading && currentPage > 0 && (
              <div className="text-center mt-8 text-gray-500">
                Loading more favorites...
              </div>
            )}

            {/* Intersection Observer target */}
            <div ref={observerTarget} className="h-4" aria-hidden="true" />

            {/* No more data message */}
            {!hasMorePages && allFavorites.length > 0 && (
              <div className="text-center mt-8 text-gray-500">
                No more favorites to load.
              </div>
            )}

            {/* Load More button as fallback */}
            {hasMorePages && (
              <div className="text-center mt-8">
                <button
                  onClick={loadMoreFavorites}
                  disabled={loading}
                  className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
                  aria-label="Load more favorites"
                >
                  {loading ? 'Loading...' : 'Load More Favorites'}
                </button>
              </div>
            )}
          </>
        )}
      </main>
    </div>
  );
};

export default FavoritesPage;
