/**
 * ReviewCard Component
 * Displays a single review with star rating and commenter info
 */

import React from 'react';
import { User } from 'lucide-react';
import StarRating from './StarRating';
import type { Review } from '../../types/review';

interface ReviewCardProps {
  review: Review;
}

const ReviewCard: React.FC<ReviewCardProps> = ({ review }) => {
  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));

    if (diffInDays === 0) return 'Today';
    if (diffInDays === 1) return 'Yesterday';
    if (diffInDays < 7) return `${diffInDays} days ago`;
    if (diffInDays < 30) return `${Math.floor(diffInDays / 7)} weeks ago`;
    if (diffInDays < 365) return `${Math.floor(diffInDays / 30)} months ago`;
    return `${Math.floor(diffInDays / 365)} years ago`;
  };

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow">
      {/* Header: User info and rating */}
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center gap-3">
          {/* Avatar */}
          {review.reviewer.avatar ? (
            <img
              src={review.reviewer.avatar}
              alt={review.reviewer.fullName}
              className="w-10 h-10 rounded-full object-cover"
            />
          ) : (
            <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center">
              <User size={20} className="text-gray-500" />
            </div>
          )}

          {/* User info */}
          <div>
            <p className="font-medium text-gray-900">
              {review.reviewer.fullName || review.reviewer.email}
            </p>
            <p className="text-sm text-gray-500">{formatDate(review.createdAt)}</p>
          </div>
        </div>

        {/* Star Rating (read-only) */}
        <StarRating rating={review.rating} size="sm" />
      </div>

      {/* Comment */}
      <p className="text-gray-700 text-sm leading-relaxed">{review.comment}</p>
    </div>
  );
};

export default ReviewCard;
