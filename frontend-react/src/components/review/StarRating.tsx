/**
 * StarRating Component
 * Interactive star rating component with hover states
 */

import React, { useState } from 'react';
import { Star } from 'lucide-react';

interface StarRatingProps {
  rating: number;
  onRatingChange?: (rating: number) => void;
  interactive?: boolean;
  size?: 'sm' | 'md' | 'lg';
}

const sizeClasses = {
  sm: 'w-4 h-4',
  md: 'w-6 h-6',
  lg: 'w-8 h-8',
};

const StarRating: React.FC<StarRatingProps> = ({
  rating,
  onRatingChange,
  interactive = false,
  size = 'md',
}) => {
  const [hoverRating, setHoverRating] = useState(0);

  const handleClick = (starNumber: number) => {
    if (interactive && onRatingChange) {
      onRatingChange(starNumber);
    }
  };

  const handleMouseEnter = (starNumber: number) => {
    if (interactive) {
      setHoverRating(starNumber);
    }
  };

  const handleMouseLeave = () => {
    if (interactive) {
      setHoverRating(0);
    }
  };

  const displayRating = hoverRating || rating;

  return (
    <div className="flex items-center gap-1">
      {[1, 2, 3, 4, 5].map((star) => {
        const isFilled = star <= displayRating;

        return (
          <button
            key={star}
            type="button"
            onClick={() => handleClick(star)}
            onMouseEnter={() => handleMouseEnter(star)}
            onMouseLeave={handleMouseLeave}
            disabled={!interactive}
            className={`
              ${sizeClasses[size]}
              ${isFilled ? 'text-yellow-400 fill-yellow-400' : 'text-gray-300'}
              ${interactive ? 'hover:scale-110 transition-transform cursor-pointer' : 'cursor-default'}
              ${interactive ? 'focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:ring-offset-2 rounded' : ''}
            `}
            aria-label={`${star} star${star === 1 ? '' : 's'}`}
            title={`${star} star${star === 1 ? '' : 's'}`}
          >
            <Star
              className={sizeClasses[size]}
              fill={isFilled ? 'currentColor' : 'none'}
            />
          </button>
        );
      })}
    </div>
  );
};

export default StarRating;
