/**
 * Review Types
 * TypeScript types for review system
 */

/**
 * Basic user info in review context
 */
export interface UserBasicInfo {
  id: number;
  email: string;
  fullName: string;
  avatar?: string;
  rating: number;
  totalReviews: number;
}

/**
 * Review entity
 */
export interface Review {
  id: number;
  reviewer: UserBasicInfo;
  targetUser: UserBasicInfo;
  rating: number;
  comment: string;
  createdAt: string;
}

/**
 * Request to create a review
 */
export interface CreateReviewRequest {
  transactionId: number;
  rating: number;
  comment: string;
}

/**
 * Request to update a review
 */
export interface UpdateReviewRequest {
  rating: number;
  comment: string;
}

/**
 * Response with seller statistics
 */
export interface SellerStatsResponse {
  averageRating: number;
  totalReviews: number;
}

/**
 * Review API response (same as Review entity)
 */
export interface ReviewResponse {
  id: number;
  reviewer: UserBasicInfo;
  targetUser: UserBasicInfo;
  rating: number;
  comment: string;
  createdAt: string;
}
