import React, { useState, useEffect, useRef, useCallback, useMemo } from 'react';
import { useQuery } from '@apollo/client';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { GET_ACCOUNTS, GET_GAMES } from '../services/graphql/queries';
import { useFilters, AccountFilters } from '../hooks/useFilters';
import AccountCard from '../components/account/AccountCard';
import FilterSidebar from '../components/search/FilterSidebar';
import SortDropdown from '../components/search/SortDropdown';
import ActiveFilterChips from '../components/search/ActiveFilterChips';
import LoadingSkeleton from '../components/common/LoadingSkeleton';
import ErrorMessage from '../components/common/ErrorMessage';
import { debounce } from 'lodash';

interface SearchPageProps {}

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

const SearchPage: React.FC<SearchPageProps> = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { filters, hasActiveFilters, setFilter } = useFilters();
  const [currentPage, setCurrentPage] = useState(0);
  const [allAccounts, setAllAccounts] = useState<Account[]>([]);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [searchInput, setSearchInput] = useState(filters.q || '');
  const observerTarget = useRef<HTMLDivElement>(null);
  const isFetchingRef = useRef(false);

  // Derive all filter values from URL params (single source of truth)
  const searchQuery = useMemo(() => searchParams.get('q') || '', [searchParams]);
  const minPrice = useMemo(() => searchParams.get('minPrice'), [searchParams]);
  const maxPrice = useMemo(() => searchParams.get('maxPrice'), [searchParams]);
  const minLevel = useMemo(() => searchParams.get('minLevel'), [searchParams]);
  const maxLevel = useMemo(() => searchParams.get('maxLevel'), [searchParams]);
  const rank = useMemo(() => searchParams.get('rank'), [searchParams]);
  const status = useMemo(() => searchParams.get('status') || 'APPROVED', [searchParams]);
  const sortBy = useMemo(() => searchParams.get('sortBy') || 'createdAt', [searchParams]);
  const sortDirection = useMemo(() => searchParams.get('sortDirection') || 'DESC', [searchParams]);

  // Games query
  const { data: gamesData } = useQuery(GET_GAMES, {
    fetchPolicy: 'cache-first'
  });

  // Update search input when URL query changes
  useEffect(() => {
    setSearchInput(searchQuery);
  }, [searchQuery]);

  // Debounced search handler
  const debouncedSearch = useMemo(
    () => debounce((value: string) => {
      setFilter('q', value || undefined);
    }, 300),
    [setFilter]
  );

  // Handle search input change
  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchInput(value);
    debouncedSearch(value);
  };

  // Cleanup debounce on unmount
  useEffect(() => {
    return () => {
      debouncedSearch.cancel();
    };
  }, [debouncedSearch]);

  // Reset accounts when filters or search query change
  useEffect(() => {
    setCurrentPage(0);
    setAllAccounts([]);
    isFetchingRef.current = false;
  }, [searchQuery, minPrice, maxPrice, minLevel, maxLevel, rank, sortBy, sortDirection]);

  // Accounts query with filters
  const { data: accountsData, loading, error, refetch } = useQuery(GET_ACCOUNTS, {
    variables: {
      gameId: filters.gameId || null,
      minPrice: minPrice ? parseFloat(minPrice) : undefined,
      maxPrice: maxPrice ? parseFloat(maxPrice) : undefined,
      minLevel: minLevel ? parseInt(minLevel) : undefined,
      maxLevel: maxLevel ? parseInt(maxLevel) : undefined,
      rank: rank || undefined,
      status: status as 'APPROVED' | 'PENDING',
      sortBy: sortBy || 'createdAt',
      sortDirection: (sortDirection as 'ASC' | 'DESC') || 'DESC',
      page: currentPage,
      limit: 12,
      q: searchQuery || undefined,
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

  // Memoized load more function
  const loadMoreAccounts = useCallback(() => {
    if (isFetchingRef.current || loading) {
      return;
    }

    const newAccountsResponse = accountsData?.accounts as AccountsResponse | undefined;
    if (newAccountsResponse && currentPage < newAccountsResponse.totalPages - 1) {
      isFetchingRef.current = true;
      setCurrentPage(prev => prev + 1);
      refetch({
        gameId: filters.gameId || null,
        minPrice: minPrice ? parseFloat(minPrice) : undefined,
        maxPrice: maxPrice ? parseFloat(maxPrice) : undefined,
        minLevel: minLevel ? parseInt(minLevel) : undefined,
        maxLevel: maxLevel ? parseInt(maxLevel) : undefined,
        rank: rank || undefined,
        status: status as 'APPROVED' | 'PENDING',
        sortBy: sortBy || 'createdAt',
        sortDirection: (sortDirection as 'ASC' | 'DESC') || 'DESC',
        page: currentPage + 1,
        limit: 12,
        q: searchQuery || undefined,
      });
    }
  }, [currentPage, filters, loading, accountsData, refetch, searchQuery, status, sortBy, sortDirection, minPrice, maxPrice, minLevel, maxLevel, rank]);

  // Intersection Observer for infinite scroll
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

  // Toggle sidebar (for mobile)
  const toggleSidebar = useCallback(() => {
    setSidebarOpen(prev => !prev);
  }, []);

  if (loading && currentPage === 0) {
    return <LoadingSkeleton type="grid" />;
  }

  if (error) {
    return <ErrorMessage message="Failed to load search results" />;
  }

  const accountsResponse = accountsData?.accounts as AccountsResponse | undefined;
  const hasMorePages = accountsResponse ? currentPage < accountsResponse.totalPages - 1 : false;
  const resultCount = accountsResponse?.totalElements || 0;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b sticky top-0 z-20">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-4">
              <button
                onClick={() => navigate('/')}
                className="text-blue-600 hover:text-blue-700"
                aria-label="Back to home"
              >
                ← Back
              </button>
              <h1 className="text-2xl font-bold text-gray-900">
                {searchQuery ? `Search: "${searchQuery}"` : 'Browse All'}
              </h1>
            </div>

            {/* Sort and mobile filter toggle */}
            <div className="flex items-center gap-4">
              <SortDropdown />
              <button
                onClick={toggleSidebar}
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
                  value={searchInput}
                  onChange={handleSearchChange}
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
              <FilterSidebar isOpen={true} onClose={toggleSidebar} />
            </div>
          )}

          {/* Results */}
          <div className="flex-1">
            {allAccounts.length === 0 && !loading ? (
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
                  {searchQuery ? `No results found for "${searchQuery}"` : 'No accounts found'}
                </p>
                {hasActiveFilters && (
                  <p className="text-sm text-gray-400 mb-4">Try adjusting your filters</p>
                )}
                <button
                  onClick={() => navigate('/')}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  Browse All Listings
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
                {loading && currentPage > 0 && (
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
                      disabled={loading}
                      className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
                      aria-label="Load more results"
                    >
                      {loading ? 'Loading...' : 'Load More Results'}
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

export default SearchPage;
