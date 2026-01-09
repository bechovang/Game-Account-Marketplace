import { gql } from '@apollo/client';

// ==================== Favorites Queries ====================

/**
 * GET_FAVORITES - Get current user's favorite accounts
 * @param page - Page number, 0-indexed (optional, default 0)
 * @param limit - Results per page (optional, default 20)
 * @param sortBy - Sort field (optional, default 'createdAt')
 * @param sortDirection - Sort direction (optional, default 'DESC' for newest first)
 * Requires authentication
 *
 * AC #1: FavoritesPage sorts by createdAt descending (newest first)
 */
export const GET_FAVORITES = gql`
  query GetFavorites($page: Int, $limit: Int, $sortBy: String, $sortDirection: String) {
    favorites(page: $page, limit: $limit, sortBy: $sortBy, sortDirection: $sortDirection) {
      content {
        id
        title
        description
        level
        rank
        price
        status
        viewsCount
        isFeatured
        isFavorited
        images
        createdAt
        updatedAt
        seller {
          id
          fullName
          avatar
          rating
          totalReviews
        }
        game {
          id
          name
          slug
          iconUrl
        }
      }
      totalElements
      totalPages
      currentPage
      pageSize
    }
  }
`;

// ==================== Account Queries ====================

/**
 * GET_ACCOUNTS - Query accounts with filters, sorting, and pagination
 * @param gameId - Filter by game ID (optional)
 * @param minPrice - Minimum price filter (optional)
 * @param maxPrice - Maximum price filter (optional)
 * @param status - Filter by account status (optional)
 * @param isFeatured - Filter to show only featured accounts (optional)
 * @param sortBy - Sort field: price, level, createdAt (optional, default createdAt)
 * @param sortDirection - Sort direction: ASC, DESC (optional, default DESC)
 * @param page - Page number, 0-indexed (optional, default 0)
 * @param limit - Results per page (optional, default 20, max 100)
 */
export const GET_ACCOUNTS = gql`
  query GetAccounts(
    $gameId: ID
    $minPrice: Float
    $maxPrice: Float
    $minLevel: Int
    $maxLevel: Int
    $rank: String
    $status: AccountStatus
    $isFeatured: Boolean
    $sortBy: String
    $sortDirection: String
    $page: Int
    $limit: Int
    $q: String
  ) {
    accounts(
      gameId: $gameId
      minPrice: $minPrice
      maxPrice: $maxPrice
      minLevel: $minLevel
      maxLevel: $maxLevel
      rank: $rank
      status: $status
      isFeatured: $isFeatured
      sortBy: $sortBy
      sortDirection: $sortDirection
      page: $page
      limit: $limit
      q: $q
    ) {
      content {
        id
        title
        description
        level
        rank
        price
        status
        viewsCount
        isFeatured
        images
        createdAt
        updatedAt
        seller {
          id
          fullName
          avatar
          rating
          totalReviews
        }
        game {
          id
          name
          slug
          iconUrl
        }
      }
      totalElements
      totalPages
      currentPage
      pageSize
    }
  }
`;

/**
 * GET_ACCOUNTS_CONNECTION - Query accounts with cursor-based pagination (Relay specification)
 * @param filters - AccountFiltersInput with gameId, minPrice, maxPrice, status, isFeatured
 * @param sort - AccountSortInput with field and direction
 * @param after - Cursor for forward pagination
 * @param before - Cursor for backward pagination
 * @param first - Number of items after cursor (max 50)
 * @param last - Number of items before cursor (max 50)
 */
export const GET_ACCOUNTS_CONNECTION = gql`
  query GetAccountsConnection(
    $filters: AccountFiltersInput
    $sort: AccountSortInput
    $after: String
    $before: String
    $first: Int
    $last: Int
  ) {
    accountsConnection(
      filters: $filters
      sort: $sort
      after: $after
      before: $before
      first: $first
      last: $last
    ) {
      edges {
        node {
          id
          title
          description
          level
          rank
          price
          status
          viewsCount
          isFeatured
          isFavorited
          images
          createdAt
          updatedAt
          seller {
            id
            fullName
            avatar
            rating
            totalReviews
          }
          game {
            id
            name
            slug
            iconUrl
          }
        }
        cursor
      }
      pageInfo {
        hasNextPage
        hasPreviousPage
        startCursor
        endCursor
      }
      totalCount
    }
  }
`;

/**
 * GET_ACCOUNT - Get a single account by ID
 * @param id - Account ID
 */
export const GET_ACCOUNT = gql`
  query GetAccount($id: ID!) {
    account(id: $id) {
      id
      title
      description
      level
      rank
      price
      status
      viewsCount
      isFeatured
      isFavorited
      images
      createdAt
      updatedAt
      seller {
        id
        fullName
        avatar
        rating
        totalReviews
        email
        role
      }
      game {
        id
        name
        slug
        description
        iconUrl
      }
    }
  }
`;

// ==================== Game Queries ====================

/**
 * GET_GAMES - Get all games
 */
export const GET_GAMES = gql`
  query GetGames {
    games {
      id
      name
      slug
      description
      iconUrl
      accountCount
      createdAt
    }
  }
`;

/**
 * GET_GAME - Get a single game by ID
 * @param id - Game ID
 */
export const GET_GAME = gql`
  query GetGame($id: ID!) {
    game(id: $id) {
      id
      name
      slug
      description
      iconUrl
      accountCount
      createdAt
    }
  }
`;

/**
 * GET_GAME_BY_SLUG - Get a game by slug
 * @param slug - Game slug
 */
export const GET_GAME_BY_SLUG = gql`
  query GetGameBySlug($slug: String!) {
    gameBySlug(slug: $slug) {
      id
      name
      slug
      description
      iconUrl
      accountCount
      createdAt
    }
  }
`;

// ==================== Combined Queries ====================

/**
 * GET_MARKETPLACE_DATA - Get accounts and games in one query
 * Useful for marketplace homepage
 */
export const GET_MARKETPLACE_DATA = gql`
  query GetMarketplaceData(
    $gameId: ID
    $minPrice: Float
    $maxPrice: Float
    $status: AccountStatus
    $isFeatured: Boolean
    $sortBy: String
    $sortDirection: String
    $page: Int
    $limit: Int
  ) {
    accounts(
      gameId: $gameId
      minPrice: $minPrice
      maxPrice: $maxPrice
      status: $status
      isFeatured: $isFeatured
      sortBy: $sortBy
      sortDirection: $sortDirection
      page: $page
      limit: $limit
    ) {
      content {
        id
        title
        description
        level
        rank
        price
        status
        viewsCount
        isFeatured
        images
        createdAt
        seller {
          id
          fullName
          avatar
          rating
        }
        game {
          id
          name
          slug
          iconUrl
        }
      }
      totalElements
      totalPages
      currentPage
      pageSize
    }
    games {
      id
      name
      slug
      iconUrl
      accountCount
    }
  }
`;
