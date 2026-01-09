import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useQuery } from '@apollo/client';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { GET_ACCOUNTS, GET_GAMES } from '../services/graphql/queries';
import { useFilters, type AccountFilters } from '../hooks/useFilters';
import AccountCard from '../components/account/AccountCard';
import FilterSidebar from '../components/search/FilterSidebar';
import SortDropdown from '../components/search/SortDropdown';
import ActiveFilterChips from '../components/search/ActiveFilterChips';
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
  const { user } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const { filters, setFilter, clearFilters } = useFilters();
  const [searchInput, setSearchInput] = useState(filters.q || '');
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

  // Accounts query with all filters
  const { data: accountsData, loading: accountsLoading, error: accountsError, refetch } = useQuery(GET_ACCOUNTS, {
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
      q: filters.q || undefined,
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

  // Update search input when URL query changes
  useEffect(() => {
    setSearchInput(filters.q || '');
  }, [filters.q]);

  // Handle search submit (only on Enter or form submit)
  const handleSearchSubmit = useCallback((e?: React.FormEvent) => {
    e?.preventDefault();
    if (searchInput.trim()) {
      setFilter('q', searchInput.trim());
    } else {
      setFilter('q', undefined);
    }
  }, [searchInput, setFilter]);

  // Reset accounts when filters change
  useEffect(() => {
    setCurrentPage(0);
    setAllAccounts([]);
    isFetchingRef.current = false;
  }, [filters.gameId, filters.minPrice, filters.maxPrice, filters.minLevel, filters.maxLevel, filters.rank, filters.status, filters.sortBy, filters.sortDirection, filters.q]);

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
        q: filters.q || undefined,
      });
    }
  }, [currentPage, filters, accountsLoading, accountsData, refetch]);

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

  // Loading state
  if ((gamesLoading && !gamesData) || (accountsLoading && !accountsData && currentPage === 0)) {
    return <LoadingSkeleton />;
  }

  // Error state - check both queries
  if (gamesError || accountsError) {
    const errorMessage = accountsError?.message || gamesError?.message || 'Failed to load marketplace data';
    console.error('[HomePage] GraphQL Error:', accountsError || gamesError);
    return <ErrorMessage message={errorMessage} />;
  }

  const accountsResponse = accountsData?.accounts as AccountsResponse | undefined;
  const hasMorePages = accountsResponse ? currentPage < accountsResponse.totalPages - 1 : false;
  const resultCount = accountsResponse?.totalElements || 0;

  return (
    <div className="min-h-screen bg-gray-50">
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
            <form onSubmit={handleSearchSubmit} className="flex-1 max-w-2xl">
              <div className="relative">
                <input
                  type="text"
                  value={searchInput}
                  onChange={(e) => setSearchInput(e.target.value)}
                  placeholder="Search accounts by title or description..."
                  className="w-full px-4 py-3 pl-12 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
              </div>
            </form>
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
            {allAccounts.length === 0 && !accountsLoading ? (
              <div className="text-center py-12 bg-white rounded-lg shadow-md">
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
                  {filters.q ? `No results found for "${filters.q}"` : 'No accounts found'}
                </p>
                <button
                  onClick={() => clearFilters()}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  Clear All Filters
                </button>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
                  {allAccounts.map((account: Account) => (
                    <AccountCard key={account.id} account={account} />
                  ))}
                </div>

                {/* Loading indicator */}
                {accountsLoading && currentPage > 0 && (
                  <div className="text-center mt-8 text-gray-500">
                    Loading more results...
                  </div>
                )}

                {/* Intersection Observer target */}
                <div ref={observerTarget} className="h-4" aria-hidden="true" />

                {/* No more data message */}
                {!hasMorePages && allAccounts.length > 0 && (
                  <div className="text-center mt-8 text-gray-500">
                    No more results to load.
                  </div>
                )}

                {/* Load More button as fallback */}
                {hasMorePages && (
                  <div className="text-center mt-8">
                    <button
                      onClick={loadMoreAccounts}
                      disabled={accountsLoading}
                      className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
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
