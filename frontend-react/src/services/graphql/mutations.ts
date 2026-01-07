import { gql } from '@apollo/client';

// ==================== Account Mutations ====================

/**
 * CREATE_ACCOUNT - Create a new account listing
 * @param input - CreateAccountInput with gameId, title, description, level, rank, price, images
 * Requires authentication (SELLER or ADMIN role)
 */
export const CREATE_ACCOUNT = gql`
  mutation CreateAccount($input: CreateAccountInput!) {
    createAccount(input: $input) {
      id
      title
      description
      level
      rank
      price
      status
      images
      createdAt
      updatedAt
      seller {
        id
        fullName
        email
      }
      game {
        id
        name
        slug
      }
    }
  }
`;

/**
 * UPDATE_ACCOUNT - Update an existing account listing
 * @param id - Account ID
 * @param input - UpdateAccountInput with title, description, level, rank, price, images
 * Requires authentication and ownership (or admin)
 */
export const UPDATE_ACCOUNT = gql`
  mutation UpdateAccount($id: ID!, $input: UpdateAccountInput!) {
    updateAccount(id: $id, input: $input) {
      id
      title
      description
      level
      rank
      price
      status
      images
      createdAt
      updatedAt
      seller {
        id
        fullName
        email
      }
      game {
        id
        name
        slug
      }
    }
  }
`;

/**
 * DELETE_ACCOUNT - Delete an account listing
 * @param id - Account ID
 * Requires authentication and ownership (or admin)
 * Returns true if successful
 */
export const DELETE_ACCOUNT = gql`
  mutation DeleteAccount($id: ID!) {
    deleteAccount(id: $id)
  }
`;

// ==================== Admin Mutations ====================

/**
 * APPROVE_ACCOUNT - Approve a pending account listing
 * @param id - Account ID
 * Requires ADMIN role
 */
export const APPROVE_ACCOUNT = gql`
  mutation ApproveAccount($id: ID!) {
    approveAccount(id: $id) {
      id
      title
      status
      updatedAt
      seller {
        id
        fullName
        email
      }
    }
  }
`;

/**
 * REJECT_ACCOUNT - Reject a pending account listing
 * @param id - Account ID
 * @param reason - Reason for rejection
 * Requires ADMIN role
 */
export const REJECT_ACCOUNT = gql`
  mutation RejectAccount($id: ID!, $reason: String) {
    rejectAccount(id: $id, reason: $reason) {
      id
      title
      status
      updatedAt
      seller {
        id
        fullName
        email
      }
    }
  }
`;

// ==================== Combined Mutations ====================

/**
 * CREATE_ACCOUNT_WITH_REFETCH - Create account and refetch accounts list
 * Useful for creating an account and immediately updating the list view
 */
export const CREATE_ACCOUNT_WITH_REFETCH = gql`
  mutation CreateAccountWithRefetch($input: CreateAccountInput!) {
    createAccount(input: $input) {
      id
      title
      description
      level
      rank
      price
      status
      images
      createdAt
      updatedAt
      seller {
        id
        fullName
        email
      }
      game {
        id
        name
        slug
      }
    }
  }
`;

/**
 * UPDATE_ACCOUNT_WITH_REFETCH - Update account and refetch accounts list
 * Useful for updating an account and immediately updating the list view
 */
export const UPDATE_ACCOUNT_WITH_REFETCH = gql`
  mutation UpdateAccountWithRefetch($id: ID!, $input: UpdateAccountInput!) {
    updateAccount(id: $id, input: $input) {
      id
      title
      description
      level
      rank
      price
      status
      images
      createdAt
      updatedAt
      seller {
        id
        fullName
        email
      }
      game {
        id
        name
        slug
      }
    }
  }
`;
