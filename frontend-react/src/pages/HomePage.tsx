import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useQuery } from '@apollo/client';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { GET_GAMES, GET_ACCOUNTS_CONNECTION, GET_ACCOUNTS } from '../services/graphql/queries';
import AccountCard from '../components/account/AccountCard';
import LoadingSkeleton from '../components/common/LoadingSkeleton';
import ErrorMessage from '../components/common/ErrorMessage';

interface HomePageProps {}

interface Game {
  id: string;
  name: string;
  slug: string;
  iconUrl?: string;
  description?: string;
  accountCount?: number;
}

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

interface AccountEdge {
  node: Account;
  cursor: string;
}

interface PageInfo {
  hasNextPage: boolean;
  hasPreviousPage: boolean;
  startCursor?: string;
  endCursor?: string;
}

interface AccountConnection {
  edges: AccountEdge[];
  pageInfo: PageInfo;
  totalCount: number;
}

const HomePage: React.FC<HomePageProps> = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const [searchQuery, setSearchQuery] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const observerTarget = useRef<HTMLDivElement>(null);

  // Game filter from URL
  const gameFilter = searchParams.get('game');

  // Local state for paginated accounts - MUST be before any early returns
  const [allAccounts, setAllAccounts] = useState<Account[]>([]);
  const [hasNextPage, setHasNextPage] = useState(false);
  const [isFetchingNextPage, setIsFetchingNextPage] = useState(false);

  // Games query - cached since games change infrequently
  const { data: gamesData, loading: gamesLoading, error: gamesError } = useQuery(GET_GAMES, {
    fetchPolicy: 'cache-first'
  });

  // Featured accounts query (still using old pagination for featured section)
  const { data: featuredData, loading: featuredLoading, error: featuredError } = useQuery(GET_ACCOUNTS, {
    variables: {
      isFeatured: true,
      gameId: gameFilter ? parseInt(gameFilter) : undefined,
      page: 0,
      limit: 6,
      sortBy: 'createdAt',
      sortDirection: 'DESC'
    },
    fetchPolicy: 'cache-and-network'
  });

  // New accounts query with cursor-based pagination using standard useQuery + fetchMore
  const {
    data: newAccountsData,
    loading: newAccountsLoading,
    error: newAccountsError,
    fetchMore,
    refetch
  } = useQuery(GET_ACCOUNTS_CONNECTION, {
    variables: {
      filters: {
        isFeatured: false,
        gameId: gameFilter ? parseInt(gameFilter) : undefined
      },
      sort: {
        field: 'createdAt',
        direction: 'DESC'
      },
      first: 12
    },
    notifyOnNetworkStatusChange: true,
    errorPolicy: 'all'
  });

  // Update local state when query data changes
  useEffect(() => {
    if (newAccountsData?.accountsConnection) {
      const accounts = newAccountsData.accountsConnection.edges.map((edge: AccountEdge) => edge.node);
      setAllAccounts(accounts);
      setHasNextPage(newAccountsData.accountsConnection.pageInfo.hasNextPage);
    }
  }, [newAccountsData]);

  // Load more handler for pagination
  const loadMore = useCallback(() => {
    if (!hasNextPage || isFetchingNextPage || !newAccountsData?.accountsConnection) return;

    const endCursor = newAccountsData.accountsConnection.pageInfo.endCursor;
    if (!endCursor) return;

    setIsFetchingNextPage(true);

    fetchMore({
      variables: {
        after: endCursor,
        first: 12
      },
      updateQuery: (prev, { fetchMoreResult }) => {
        setIsFetchingNextPage(false);

        if (!fetchMoreResult) return prev;

        const newEdges = fetchMoreResult.accountsConnection?.edges || [];
        const prevEdges = prev.accountsConnection?.edges || [];

        // Check if there's more data
        setHasNextPage(fetchMoreResult.accountsConnection?.pageInfo?.hasNextPage || false);

        // Combine accounts
        const combinedAccounts = [
          ...prevEdges.map((edge: AccountEdge) => edge.node),
          ...newEdges.map((edge: AccountEdge) => edge.node)
        ];
        setAllAccounts(combinedAccounts);

        return {
          accountsConnection: {
            ...fetchMoreResult.accountsConnection,
            edges: [...prevEdges, ...newEdges]
          }
        };
      }
    });
  }, [hasNextPage, isFetchingNextPage, newAccountsData, fetchMore]);

  // Debounce search input
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(searchQuery);
    }, 300);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Navigate to search when debounced search changes
  useEffect(() => {
    if (debouncedSearch.trim()) {
      navigate(`/search?q=${encodeURIComponent(debouncedSearch)}${gameFilter ? `&game=${gameFilter}` : ''}`);
    }
  }, [debouncedSearch, gameFilter, navigate]);

  // Reset accounts when game filter changes
  useEffect(() => {
    setAllAccounts([]);
    setHasNextPage(false);
  }, [gameFilter]);

  // Intersection Observer for infinite scroll with improved memory management
  useEffect(() => {
    if (!hasNextPage || isFetchingNextPage || newAccountsError) {
      return; // Don't set up observer if we don't need it
    }

    const observer = new IntersectionObserver(
      (entries) => {
        const [entry] = entries;
        if (entry.isIntersecting && hasNextPage && !isFetchingNextPage && !newAccountsError) {
          loadMore();
        }
      },
      {
        threshold: 0.1,
        rootMargin: '50px' // Start loading 50px before the element comes into view
      }
    );

    const currentTarget = observerTarget.current;
    if (currentTarget) {
      observer.observe(currentTarget);
    }

    return () => {
      // Properly disconnect the observer to prevent memory leaks
      observer.disconnect();
    };
  }, [loadMore, hasNextPage, isFetchingNextPage, newAccountsError]);

  // Memoized game filter handler
  const handleGameFilter = useCallback((gameId: string | null) => {
    if (gameId) {
      setSearchParams({ game: gameId });
    } else {
      setSearchParams({});
    }
  }, [setSearchParams]);

  // Loading state
  if (gamesLoading && !gamesData) {
    return <LoadingSkeleton />;
  }

  // Error state with retry options
  if (gamesError || featuredError || newAccountsError) {
    const retryActions = [];

    if (newAccountsError) {
      retryActions.push(
        <button
          key="retry-accounts"
          onClick={() => refetch()}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          Retry Loading Accounts
        </button>
      );
    }

    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <ErrorMessage message="Failed to load marketplace data" />
          <div className="mt-4 space-x-2">
            {retryActions}
            <button
              onClick={() => window.location.reload()}
              className="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors"
            >
              Reload Page
            </button>
          </div>
        </div>
      </div>
    );
  }

  const games = gamesData?.games || [];
  const featuredAccounts = featuredData?.accounts?.content || [];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header with Search */}
      <header className="bg-white shadow-sm border-b sticky top-0 z-10">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold text-gray-900">Game Account Marketplace</h1>
            <div className="flex-1 max-w-md mx-8">
              <input
                type="text"
                placeholder="Search accounts..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && searchQuery.trim()) {
                    navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}${gameFilter ? `&game=${gameFilter}` : ''}`);
                  }
                }}
                aria-label="Search for game accounts"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div className="flex items-center space-x-4">
              <a
                href="/favorites"
                className="text-gray-600 hover:text-blue-600 transition-colors"
                aria-label="Favorites"
              >
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
              </a>
              <a
                href="/profile"
                className="flex items-center space-x-2 text-gray-600 hover:text-blue-600 transition-colors"
                aria-label="Profile"
              >
                {user?.avatar ? (
                  <img
                    src={user.avatar}
                    alt={user.fullName}
                    className="w-8 h-8 rounded-full object-cover"
                  />
                ) : (
                  <div className="w-8 h-8 rounded-full bg-blue-500 flex items-center justify-center text-white text-sm font-semibold">
                    {user?.fullName?.charAt(0).toUpperCase() || 'U'}
                  </div>
                )}
                <span className="hidden md:inline text-sm">
                  {user?.fullName}
                </span>
                <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                  user?.role === 'ADMIN'
                    ? 'bg-purple-100 text-purple-800'
                    : user?.role === 'SELLER'
                    ? 'bg-green-100 text-green-800'
                    : 'bg-blue-100 text-blue-800'
                }`}>
                  {user?.role || 'GUEST'}
                </span>
              </a>
            </div>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-4 py-8">
        {/* Game Categories - only show if games exist */}
        {games.length > 0 && (
          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">Browse by Game</h2>
            <div className="flex space-x-4 overflow-x-auto pb-4">
              <button
                onClick={() => handleGameFilter(null)}
                aria-label="Show all games"
                className={`flex-shrink-0 px-4 py-2 rounded-full border transition-colors ${
                  !gameFilter
                    ? 'bg-blue-600 text-white border-blue-600'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                }`}
              >
                All Games
              </button>
              {games.map((game: Game) => (
                <button
                  key={game.id}
                  onClick={() => handleGameFilter(game.id)}
                  aria-label={`Filter by ${game.name}`}
                  className={`flex-shrink-0 px-4 py-2 rounded-full border transition-colors whitespace-nowrap ${
                    gameFilter === game.id
                      ? 'bg-blue-600 text-white border-blue-600'
                      : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                  }`}
                >
                  {game.iconUrl && (
                    <img
                      src={game.iconUrl}
                      alt={game.name}
                      className="w-6 h-6 inline mr-2 rounded"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none';
                      }}
                    />
                  )}
                  {game.name}
                </button>
              ))}
            </div>
            {gameFilter && (
              <button
                onClick={() => handleGameFilter(null)}
                aria-label="Clear game filter"
                className="mt-2 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors"
              >
                Clear Filter
              </button>
            )}
          </section>
        )}

        {/* Featured Accounts */}
        <section className="mb-12">
          <h2 className="text-xl font-semibold mb-4">Featured Listings</h2>
          {featuredLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(6)].map((_, i) => (
                <div key={i} className="bg-white rounded-lg shadow-md p-4 animate-pulse">
                  <div className="w-full h-48 bg-gray-200 rounded mb-4"></div>
                  <div className="h-4 bg-gray-200 rounded mb-2"></div>
                  <div className="h-4 bg-gray-200 rounded w-2/3"></div>
                </div>
              ))}
            </div>
          ) : featuredAccounts.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {featuredAccounts.map((account: Account) => (
                <AccountCard key={account.id} account={account} />
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-gray-500 bg-white rounded-lg shadow-md">
              No featured accounts available at the moment.
            </div>
          )}
        </section>

        {/* New Accounts */}
        <section>
          <h2 className="text-xl font-semibold mb-4">New Listings</h2>

          {/* Error state for pagination */}
          {newAccountsError && allAccounts.length > 0 && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
              <div className="flex items-center justify-between">
                <div className="flex items-center">
                  <div className="text-red-600 mr-3">
                    <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                    </svg>
                  </div>
                  <div>
                    <p className="text-red-800 font-medium">Failed to load more accounts</p>
                    <p className="text-red-600 text-sm">Check your connection and try again</p>
                  </div>
                </div>
                <button
                  onClick={loadMore}
                  className="px-3 py-1 bg-red-600 text-white text-sm rounded hover:bg-red-700 transition-colors"
                >
                  Retry
                </button>
              </div>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {allAccounts.map((account: Account) => (
              <AccountCard key={account.id} account={account} />
            ))}
          </div>

          {/* Loading indicator for additional pages */}
          {isFetchingNextPage && (
            <div className="text-center mt-8">
              <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
              <p className="mt-2 text-gray-600">Loading more accounts...</p>
            </div>
          )}

          {/* Intersection Observer target - only show if no errors and more data available */}
          {!newAccountsError && hasNextPage && (
            <div
              ref={observerTarget}
              className="h-10 flex items-center justify-center"
              aria-hidden="true"
              role="progressbar"
              aria-label="Loading more content"
            >
              <div className="w-8 h-1 bg-gray-200 rounded"></div>
            </div>
          )}

          {/* No more data message */}
          {!hasNextPage && allAccounts.length > 0 && !isFetchingNextPage && !newAccountsError && (
            <div className="text-center mt-8 text-gray-500">
              No more accounts to load.
            </div>
          )}

          {/* Load More button as fallback - show on error or when intersection observer fails */}
          {hasNextPage && !isFetchingNextPage && (
            <div className="text-center mt-8">
              <button
                onClick={loadMore}
                disabled={newAccountsLoading}
                aria-label="Load more accounts"
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {newAccountsError ? 'Retry Loading Accounts' : 'Load More Accounts'}
              </button>
            </div>
          )}

          {/* Empty state */}
          {allAccounts.length === 0 && !newAccountsLoading && !newAccountsError && (
            <div className="text-center py-12 text-gray-500 bg-white rounded-lg shadow-md">
              No new accounts available at the moment.
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

export default HomePage;
