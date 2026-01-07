import { gql } from '@apollo/client';

// ==================== Account Queries ====================

/**
 * GET_ACCOUNTS - Query accounts with filters and pagination
 * @param gameId - Filter by game ID (optional)
 * @param minPrice - Minimum price filter (optional)
 * @param maxPrice - Maximum price filter (optional)
 * @param status - Filter by account status (optional)
 * @param page - Page number, 0-indexed (optional, default 0)
 * @param limit - Results per page (optional, default 20, max 100)
 */
export const GET_ACCOUNTS = gql`
  query GetAccounts(
    $gameId: ID
    $minPrice: Float
    $maxPrice: Float
    $status: AccountStatus
    $page: Int
    $limit: Int
  ) {
    accounts(
      gameId: $gameId
      minPrice: $minPrice
      maxPrice: $maxPrice
      status: $status
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
    $page: Int
    $limit: Int
  ) {
    accounts(
      gameId: $gameId
      minPrice: $minPrice
      maxPrice: $maxPrice
      status: $status
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
