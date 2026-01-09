import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useQuery } from '@apollo/client';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { GET_ACCOUNTS, GET_GAMES } from '../services/graphql/queries';
import { useFilters, useDebouncedSearch } from '../hooks/useFilters';
import AccountCard from '../components/account/AccountCard';
import FilterSidebar from '../components/search/FilterSidebar';
import SortDropdown from '../components/search/SortDropdown';
import ActiveFilterChips from '../components/search/ActiveFilterChips';
import LoadingSkeleton from '../components/common/LoadingSkeleton';
import ErrorMessage from '../components/common/ErrorMessage';
import { ProgressBar, CornerLoader } from '../components/common/ProgressBar';

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
  const { user } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const { filters, setFilter, clearFilters } = useFilters();
  const { searchTerm, setSearchTerm, debouncedSearch, isDebouncing } = useDebouncedSearch(300);
  const [allAccounts, setAllAccounts] = useState<Account[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const observerTarget = useRef<HTMLDivElement>(null);
  const isFetchingRef = useRef(false);

  // Games query
  const { data: gamesData, loading: gamesLoading, error: gamesError } = useQuery(GET_GAMES, {
    fetchPolicy: 'cache-first'
  });

  const games = gamesData?.games || [];

  // Accounts query with all filters (using debounced search)
  const { data: accountsData, loading: accountsLoading, error: accountsError, refetch, previousData } = useQuery(GET_ACCOUNTS, {
    variables: {
      gameId: filters.gameId || undefined,
      minPrice: filters.minPrice,
      maxPrice: filters.maxPrice,
      minLevel: filters.minLevel,
      maxLevel: filters.maxLevel,
      rank: filters.rank,
      status: (filters.status || 'APPROVED') as 'APPROVED' | 'PENDING',
      sortBy: filters.sortBy || 'createdAt',
      sortDirection: (filters.sortDirection || 'DESC') as 'ASC' | 'DESC',
      page: currentPage,
      limit: 12,
      q: debouncedSearch || undefined, // Use debounced search
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

  // Sync debounced search with URL filter
  useEffect(() => {
    if (debouncedSearch !== filters.q) {
      if (debouncedSearch) {
        setFilter('q', debouncedSearch);
      } else {
        setFilter('q', undefined);
      }
    }
  }, [debouncedSearch, filters.q, setFilter]);

  // Reset accounts when filters change (including debounced search)
  useEffect(() => {
    setCurrentPage(0);
    setAllAccounts([]);
    isFetchingRef.current = false;
  }, [filters.gameId, filters.minPrice, filters.maxPrice, filters.minLevel, filters.maxLevel, filters.rank, filters.status, filters.sortBy, filters.sortDirection, debouncedSearch]);

  // Memoized load more function
  const loadMoreAccounts = useCallback(() => {
    if (isFetchingRef.current || accountsLoading) {
      return;
    }

    const accountsResponse = accountsData?.accounts as AccountsResponse | undefined;
    if (accountsResponse && currentPage < accountsResponse.totalPages - 1) {
      isFetchingRef.current = true;
      const nextPage = currentPage + 1;
      setCurrentPage(nextPage);
      refetch({
        gameId: filters.gameId || undefined,
        minPrice: filters.minPrice,
        maxPrice: filters.maxPrice,
        minLevel: filters.minLevel,
        maxLevel: filters.maxLevel,
        rank: filters.rank,
        status: (filters.status || 'APPROVED') as 'APPROVED' | 'PENDING',
        sortBy: filters.sortBy || 'createdAt',
        sortDirection: (filters.sortDirection || 'DESC') as 'ASC' | 'DESC',
        page: nextPage,
        limit: 12,
        q: debouncedSearch || undefined, // Use debounced search
      });
    }
  }, [currentPage, filters, accountsLoading, accountsData, refetch, debouncedSearch]);

  // Intersection Observer for infinite scroll
  useEffect(() => {
    if (!accountsData?.accounts) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          loadMoreAccounts();
        }
      },
      { threshold: 0.1, rootMargin: '50px' }
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
  }, [loadMoreAccounts, accountsData]);

  // Determine if this is an initial load (no previous data)
  const isInitialLoad = accountsLoading && !previousData && currentPage === 0;
  const isRefetching = accountsLoading && (previousData || allAccounts.length > 0);
  
  // Use current or previous data for smooth transitions
  const displayData = accountsData || previousData;
  const accountsResponse = displayData?.accounts as AccountsResponse | undefined;
  const hasMorePages = accountsResponse ? currentPage < accountsResponse.totalPages - 1 : false;
  const resultCount = accountsResponse?.totalElements || 0;

  // Only show full loading skeleton on initial load
  if (isInitialLoad && (gamesLoading && !gamesData)) {
    return <LoadingSkeleton type="grid" count={12} />;
  }

  // Error state - check both queries
  if (gamesError || accountsError) {
    const errorMessage = accountsError?.message || gamesError?.message || 'Failed to load marketplace data';
    console.error('[HomePage] GraphQL Error:', accountsError || gamesError);
    return <ErrorMessage message={errorMessage} />;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Progress bar - shown during refetch only */}
      <ProgressBar loading={isRefetching} />
      
      {/* Corner loader - subtle indicator */}
      <CornerLoader loading={isRefetching} position="bottom-right" />
      
      {/* Header with Search */}
      <header className="bg-white shadow-sm border-b sticky top-0 z-10">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-2xl font-bold text-gray-900">Game Account Marketplace</h1>

            {/* Sort and mobile filter toggle */}
            <div className="flex items-center gap-4">
              <SortDropdown />
              <button
                onClick={() => setSidebarOpen(true)}
                className="md:hidden px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                aria-label="Toggle filters"
              >
                Filters
              </button>
            </div>
          </div>

          {/* Search input and result count */}
          <div className="flex items-center gap-4">
            <div className="flex-1 max-w-2xl">
              <div className="relative">
                <input
                  type="text"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  placeholder="Search accounts by title or description..."
                  className="w-full px-4 py-3 pl-12 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-smooth"
                  aria-label="Search accounts"
                />
                <svg
                  className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                  />
                </svg>
                {/* Debouncing indicator */}
                {isDebouncing && (
                  <div className="absolute right-4 top-1/2 transform -translate-y-1/2">
                    <div className="w-4 h-4 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
                  </div>
                )}
              </div>
              {isDebouncing && (
                <p className="text-xs text-gray-500 mt-1 animate-fade-in">
                  Searching...
                </p>
              )}
            </div>
            {resultCount > 0 && (
              <p className="text-sm text-gray-600 whitespace-nowrap">
                {resultCount} {resultCount === 1 ? 'result' : 'results'} found
              </p>
            )}
          </div>

          {/* Active filter chips */}
          <ActiveFilterChips />
        </div>
      </header>

      <main className="container mx-auto px-4 py-8">
        <div className="flex gap-8">
          {/* Sidebar - Desktop: always visible, Mobile: hidden by default */}
          <div className="hidden md:block md:w-80 flex-shrink-0">
            <FilterSidebar />
          </div>

          {/* Mobile sidebar - visible when open */}
          {sidebarOpen && (
            <div className="md:hidden fixed inset-0 z-30">
              <FilterSidebar isOpen={true} onClose={() => setSidebarOpen(false)} />
            </div>
          )}

          {/* Results */}
          <div className="flex-1">
            {/* Show skeleton on initial load with no previous data */}
            {isInitialLoad ? (
              <LoadingSkeleton type="grid" count={12} />
            ) : allAccounts.length === 0 && !accountsLoading ? (
              <div className="text-center py-12 bg-white rounded-lg shadow-md animate-fade-in">
                <svg
                  className="w-16 h-16 mx-auto text-gray-400 mb-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
                <p className="text-gray-500 text-lg mb-2">
                  {debouncedSearch ? `No results found for "${debouncedSearch}"` : 'No accounts found'}
                </p>
                <button
                  onClick={() => {
                    clearFilters();
                    setSearchTerm('');
                  }}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-smooth"
                >
                  Clear All Filters
                </button>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
                  {allAccounts.map((account: Account, index) => (
                    <div 
                      key={account.id} 
                      className="animate-fade-in-up"
                      style={{ animationDelay: `${index * 0.05}s` }}
                    >
                      <AccountCard account={account} />
                    </div>
                  ))}
                </div>

                {/* Loading indicator for pagination */}
                {accountsLoading && currentPage > 0 && (
                  <div className="text-center mt-8 animate-fade-in">
                    <div className="inline-flex items-center gap-2 text-gray-500">
                      <div className="w-4 h-4 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
                      <span>Loading more results...</span>
                    </div>
                  </div>
                )}

                {/* Intersection Observer target */}
                <div ref={observerTarget} className="h-4" aria-hidden="true" />

                {/* No more data message */}
                {!hasMorePages && allAccounts.length > 0 && (
                  <div className="text-center mt-8 text-gray-500 animate-fade-in">
                    No more results to load.
                  </div>
                )}

                {/* Load More button as fallback */}
                {hasMorePages && (
                  <div className="text-center mt-8 animate-fade-in">
                    <button
                      onClick={loadMoreAccounts}
                      disabled={accountsLoading}
                      className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-smooth"
                      aria-label="Load more results"
                    >
                      {accountsLoading ? 'Loading...' : 'Load More Results'}
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default HomePage;
