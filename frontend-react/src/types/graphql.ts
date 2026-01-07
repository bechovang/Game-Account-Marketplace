/**
 * GraphQL type definitions for Game Account Marketplace
 * Generated from backend GraphQL schema
 */

// ==================== Enums ====================

export enum AccountStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  SOLD = 'SOLD',
}

export enum Role {
  BUYER = 'BUYER',
  SELLER = 'SELLER',
  ADMIN = 'ADMIN',
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  BANNED = 'BANNED',
  SUSPENDED = 'SUSPENDED',
}

// ==================== Type Interfaces ====================

export interface User {
  id: string;
  email: string;
  fullName?: string;
  avatar?: string;
  role: Role;
  status: UserStatus;
  rating: number;
  totalReviews: number;
  balance: number;
  createdAt: string;
  updatedAt: string;
}

export interface Game {
  id: string;
  name: string;
  slug: string;
  description?: string;
  iconUrl?: string;
  accountCount: number;
  createdAt: string;
}

export interface Account {
  id: string;
  seller: User;
  game: Game;
  title: string;
  description?: string;
  level?: number;
  rank?: string;
  price: number;
  status: AccountStatus;
  viewsCount: number;
  isFeatured: boolean;
  images: string[];
  createdAt: string;
  updatedAt: string;
}

export interface PaginatedAccounts {
  content: Account[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

// ==================== Input Types ====================

export interface CreateAccountInput {
  gameId: string;
  title: string;
  description?: string;
  level?: number;
  rank?: string;
  price: number;
  images: string[];
}

export interface UpdateAccountInput {
  title: string;
  description?: string;
  level?: number;
  rank?: string;
  price: number;
  images: string[];
}

// ==================== Query Variable Types ====================

export interface AccountsQueryVariables {
  gameId?: string;
  minPrice?: number;
  maxPrice?: number;
  status?: AccountStatus;
  page?: number;
  limit?: number;
}

export interface AccountQueryVariables {
  id: string;
}

export interface GameQueryVariables {
  id: string;
}

export interface GameBySlugQueryVariables {
  slug: string;
}

// ==================== Mutation Variable Types ====================

export interface CreateAccountMutationVariables {
  input: CreateAccountInput;
}

export interface UpdateAccountMutationVariables {
  id: string;
  input: UpdateAccountInput;
}

export interface DeleteAccountMutationVariables {
  id: string;
}

export interface ApproveAccountMutationVariables {
  id: string;
}

export interface RejectAccountMutationVariables {
  id: string;
  reason: string;
}
