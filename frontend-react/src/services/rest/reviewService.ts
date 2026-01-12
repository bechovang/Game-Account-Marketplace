/**
 * Review Service API
 * Handles all review-related API calls
 */

import axiosInstance from './axiosInstance';
import type {
  Review,
  CreateReviewRequest,
  UpdateReviewRequest,
  SellerStatsResponse,
} from '../../types/review';

const REVIEWS_BASE_URL = '/api/reviews';

/**
 * Create a new review for a completed transaction
 */
export const createReview = async (
  request: CreateReviewRequest
): Promise<Review> => {
  const response = await axiosInstance.post<Review>(
    REVIEWS_BASE_URL,
    request
  );
  return response.data;
};

/**
 * Get all reviews written by a user
 */
export const getUserReviews = async (userId: number): Promise<Review[]> => {
  const response = await axiosInstance.get<Review[]>(
    `${REVIEWS_BASE_URL}/user/${userId}`
  );
  return response.data;
};

/**
 * Get all reviews received by a seller
 */
export const getSellerReviews = async (sellerId: number): Promise<Review[]> => {
  const response = await axiosInstance.get<Review[]>(
    `${REVIEWS_BASE_URL}/seller/${sellerId}`
  );
  return response.data;
};

/**
 * Get seller's average rating and total review count
 */
export const getSellerStats = async (
  sellerId: number
): Promise<SellerStatsResponse> => {
  const response = await axiosInstance.get<SellerStatsResponse>(
    `${REVIEWS_BASE_URL}/seller/${sellerId}/stats`
  );
  return response.data;
};

/**
 * Get review for a specific transaction
 */
export const getTransactionReview = async (
  transactionId: number
): Promise<Review> => {
  const response = await axiosInstance.get<Review>(
    `${REVIEWS_BASE_URL}/transaction/${transactionId}`
  );
  return response.data;
};

/**
 * Update an existing review
 */
export const updateReview = async (
  reviewId: number,
  request: UpdateReviewRequest
): Promise<Review> => {
  const response = await axiosInstance.put<Review>(
    `${REVIEWS_BASE_URL}/${reviewId}`,
    request
  );
  return response.data;
};

/**
 * Delete a review
 */
export const deleteReview = async (reviewId: number): Promise<void> => {
  await axiosInstance.delete(`${REVIEWS_BASE_URL}/${reviewId}`);
};

export default {
  createReview,
  getUserReviews,
  getSellerReviews,
  getSellerStats,
  getTransactionReview,
  updateReview,
  deleteReview,
};
