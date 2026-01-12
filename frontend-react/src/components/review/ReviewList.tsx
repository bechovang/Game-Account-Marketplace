/**
 * ReviewList Component
 * Displays a list of reviews with pagination and empty state
 */

import React from 'react';
import { MessageSquare, Star } from 'lucide-react';
import ReviewCard from './ReviewCard';
import type { Review } from '../../types/review';

interface ReviewListProps {
  reviews: Review[];
  isLoading?: boolean;
  hasMore?: boolean;
  onLoadMore?: () => void;
}

const ReviewList: React.FC<ReviewListProps> = ({
  reviews,
  isLoading = false,
  hasMore = false,
  onLoadMore,
}) => {
  // Empty state
  if (reviews.length === 0 && !isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-12 px-4">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <MessageSquare size={32} className="text-gray-400" />
        </div>
        <h3 className="text-lg font-medium text-gray-900 mb-2">No Reviews Yet</h3>
        <p className="text-gray-500 text-center max-w-sm">
          Be the first to leave a review for this seller!
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Reviews */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {reviews.map((review) => (
          <ReviewCard key={review.id} review={review} />
        ))}
      </div>

      {/* Loading skeleton */}
      {isLoading && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {[1, 2].map((i) => (
            <div key={i} className="bg-gray-100 rounded-lg p-4 animate-pulse">
              <div className="flex items-center gap-3 mb-3">
                <div className="w-10 h-10 bg-gray-200 rounded-full"></div>
                <div className="flex-1">
                  <div className="h-4 bg-gray-200 rounded w-24 mb-2"></div>
                  <div className="h-3 bg-gray-200 rounded w-16"></div>
                </div>
              </div>
              <div className="space-y-2">
                <div className="h-3 bg-gray-200 rounded w-full"></div>
                <div className="h-3 bg-gray-200 rounded w-5/6"></div>
                <div className="h-3 bg-gray-200 rounded w-4/6"></div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Load More Button */}
      {hasMore && !isLoading && (
        <div className="flex justify-center pt-4">
          <button
            onClick={onLoadMore}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            Load More Reviews
          </button>
        </div>
      )}
    </div>
  );
};

export default ReviewList;
