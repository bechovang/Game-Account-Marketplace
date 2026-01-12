/**
 * useReviews Hook
 * Custom hook for review-related operations and state management
 */

import { useState, useCallback } from 'react';
import toast from 'react-hot-toast';
import reviewService from '../services/rest/reviewService';
import type { Review, SellerStatsResponse, CreateReviewRequest, UpdateReviewRequest } from '../types/review';

interface UseReviewsResult {
  reviews: Review[];
  stats: SellerStatsResponse | null;
  isLoading: boolean;
  error: string | null;
  fetchSellerReviews: (sellerId: number) => Promise<void>;
  fetchSellerStats: (sellerId: number) => Promise<void>;
  createReview: (request: CreateReviewRequest) => Promise<void>;
  updateReview: (reviewId: number, request: UpdateReviewRequest) => Promise<void>;
  deleteReview: (reviewId: number) => Promise<void>;
  clearError: () => void;
}

export const useReviews = (): UseReviewsResult => {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [stats, setStats] = useState<SellerStatsResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  const fetchSellerReviews = useCallback(async (sellerId: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await reviewService.getSellerReviews(sellerId);
      setReviews(data);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to fetch reviews';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const fetchSellerStats = useCallback(async (sellerId: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await reviewService.getSellerStats(sellerId);
      setStats(data);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to fetch seller stats';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const createReview = useCallback(async (request: CreateReviewRequest) => {
    setIsLoading(true);
    setError(null);
    try {
      const newReview = await reviewService.createReview(request);
      setReviews((prev) => [newReview, ...prev]);
      toast.success('Review submitted successfully!');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to submit review';
      setError(errorMessage);
      toast.error(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const updateReview = useCallback(async (reviewId: number, request: UpdateReviewRequest) => {
    setIsLoading(true);
    setError(null);
    try {
      const updatedReview = await reviewService.updateReview(reviewId, request);
      setReviews((prev) =>
        prev.map((review) => (review.id === reviewId ? updatedReview : review))
      );
      toast.success('Review updated successfully!');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to update review';
      setError(errorMessage);
      toast.error(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const deleteReview = useCallback(async (reviewId: number) => {
    setIsLoading(true);
    setError(null);
    try {
      await reviewService.deleteReview(reviewId);
      setReviews((prev) => prev.filter((review) => review.id !== reviewId));
      toast.success('Review deleted successfully!');
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to delete review';
      setError(errorMessage);
      toast.error(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    reviews,
    stats,
    isLoading,
    error,
    fetchSellerReviews,
    fetchSellerStats,
    createReview,
    updateReview,
    deleteReview,
    clearError,
  };
};

export default useReviews;
