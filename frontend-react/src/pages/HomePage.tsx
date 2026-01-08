import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useQuery } from '@apollo/client';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { GET_GAMES, GET_ACCOUNTS } from '../services/graphql/queries';
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

interface AccountsResponse {
  content: Account[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

const HomePage: React.FC<HomePageProps> = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const [searchQuery, setSearchQuery] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');

  // Game filter from URL
  const gameFilter = searchParams.get('game');
  const [currentPage, setCurrentPage] = useState(0);
  const [allAccounts, setAllAccounts] = useState<Account[]>([]);
  const observerTarget = useRef<HTMLDivElement>(null);
  const isFetchingRef = useRef(false);

  // Games query - cached since games change infrequently
  const { data: gamesData, loading: gamesLoading, error: gamesError } = useQuery(GET_GAMES, {
    fetchPolicy: 'cache-first'
  });

  // Featured accounts query
  const { data: featuredData, loading: featuredLoading, error: featuredError } = useQuery(GET_ACCOUNTS, {
    variables: {
      gameId: gameFilter || null,
      status: 'APPROVED' as const,
      isFeatured: true,
      page: 0,
      limit: 6
    },
    fetchPolicy: 'cache-and-network'
  });

  // New accounts query with pagination
  const { data: newAccountsData, loading: newAccountsLoading, error: newAccountsError, refetch } = useQuery(GET_ACCOUNTS, {
    variables: {
      gameId: gameFilter || null,
      status: 'APPROVED' as const,
      isFeatured: false,
      page: currentPage,
      limit: 12,
      sortBy: 'createdAt',
      sortDirection: 'DESC' as const
    },
    fetchPolicy: 'cache-and-network',
    onCompleted: (data) => {
      if (currentPage === 0) {
        setAllAccounts(data.accounts.content);
      } else {
        setAllAccounts(prev => [...prev, ...data.accounts.content]);
      }
      isFetchingRef.current = false;
    }
  });

  // Reset accounts when game filter changes
  useEffect(() => {
    setCurrentPage(0);
    setAllAccounts([]);
    isFetchingRef.current = false;
  }, [gameFilter]);

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

  // Memoized refetch function to prevent unnecessary re-renders
  const loadMoreAccounts = useCallback(() => {
    if (isFetchingRef.current || newAccountsLoading) {
      return;
    }

    const newAccountsResponse = newAccountsData?.accounts as AccountsResponse | undefined;
    if (newAccountsResponse && currentPage < newAccountsResponse.totalPages - 1) {
      isFetchingRef.current = true;
      setCurrentPage(prev => prev + 1);
      refetch({
        gameId: gameFilter || null,
        status: 'APPROVED' as const,
        isFeatured: false,
        page: currentPage + 1,
        limit: 12,
        sortBy: 'createdAt',
        sortDirection: 'DESC' as const
      });
    }
  }, [currentPage, gameFilter, newAccountsData, newAccountsLoading, refetch]);

  // Intersection Observer for infinite scroll - fixed race condition
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadMoreAccounts();
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
  }, [loadMoreAccounts]);

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

  // Error state
  if (gamesError || featuredError || newAccountsError) {
    return <ErrorMessage message="Failed to load marketplace data" />;
  }

  const games = gamesData?.games || [];
  const featuredAccounts = featuredData?.accounts?.content || [];
  const newAccountsResponse = newAccountsData?.accounts as AccountsResponse | undefined;
  const hasMorePages = newAccountsResponse ? currentPage < newAccountsResponse.totalPages - 1 : false;

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
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {allAccounts.map((account: Account) => (
              <AccountCard key={account.id} account={account} />
            ))}
          </div>

          {/* Loading indicator */}
          {newAccountsLoading && currentPage > 0 && (
            <div className="text-center mt-8 text-gray-500">
              Loading more accounts...
            </div>
          )}

          {/* Intersection Observer target */}
          <div ref={observerTarget} className="h-4" aria-hidden="true" />

          {/* No more data message */}
          {!hasMorePages && allAccounts.length > 0 && (
            <div className="text-center mt-8 text-gray-500">
              No more accounts to load.
            </div>
          )}

          {/* Load More button as fallback */}
          {hasMorePages && (
            <div className="text-center mt-8">
              <button
                onClick={loadMoreAccounts}
                disabled={newAccountsLoading}
                aria-label="Load more accounts"
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {newAccountsLoading ? 'Loading...' : 'Load More Accounts'}
              </button>
            </div>
          )}

          {allAccounts.length === 0 && !newAccountsLoading && (
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
