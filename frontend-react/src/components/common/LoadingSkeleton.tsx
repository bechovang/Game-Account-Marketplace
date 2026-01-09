import React from 'react';

interface LoadingSkeletonProps {
  type?: 'card' | 'list' | 'detail' | 'grid';
  count?: number;
}

const LoadingSkeleton: React.FC<LoadingSkeletonProps> = ({ type = 'detail', count = 6 }) => {
  if (type === 'grid') {
    return (
      <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6" role="status" aria-label="Loading">
        {Array.from({ length: count }).map((_, index) => (
          <div
            key={index}
            className="bg-white rounded-lg shadow-md overflow-hidden animate-pulse-subtle"
            aria-hidden="true"
          >
            {/* Image placeholder with shimmer */}
            <div className="relative w-full h-48 bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-skeleton"></div>

            <div className="p-4">
              {/* Title placeholder */}
              <div className="h-6 bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-skeleton rounded w-3/4 mb-3"></div>

              {/* Game badge + Level */}
              <div className="flex items-center gap-2 mb-3">
                <div className="w-20 h-6 bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-skeleton rounded-full"></div>
                <div className="h-4 bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-skeleton rounded w-16"></div>
              </div>

              {/* Price placeholder */}
              <div className="h-7 bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-skeleton rounded w-24 mb-4"></div>

              {/* Seller info placeholder */}
              <div className="flex items-center justify-between pt-3 border-t">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-skeleton rounded-full"></div>
                  <div className="h-4 bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-skeleton rounded w-20"></div>
                </div>
                <div className="h-4 bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 bg-skeleton rounded w-16"></div>
              </div>
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (type === 'detail') {
    return (
      <div className="container mx-auto px-4 py-8 animate-pulse">
        {/* Header Skeleton */}
        <div className="bg-gray-200 rounded-lg shadow-md p-6 mb-6 h-32"></div>

        {/* Image Gallery Skeleton */}
        <div className="bg-gray-200 rounded-lg shadow-md p-6 mb-6 h-96"></div>

        {/* Description Skeleton */}
        <div className="bg-gray-200 rounded-lg shadow-md p-6 mb-6 h-48"></div>

        {/* Seller Card Skeleton */}
        <div className="bg-gray-200 rounded-lg shadow-md p-6 h-32"></div>
      </div>
    );
  }

  if (type === 'card') {
    return (
      <div className="bg-white rounded-lg shadow-md p-4 animate-pulse">
        <div className="bg-gray-200 h-48 rounded mb-4"></div>
        <div className="bg-gray-200 h-6 rounded mb-2"></div>
        <div className="bg-gray-200 h-4 rounded w-2/3"></div>
      </div>
    );
  }

  // List skeleton
  return (
    <div className="space-y-4 animate-pulse">
      {[1, 2, 3, 4, 5].map((i) => (
        <div key={i} className="bg-gray-200 h-24 rounded"></div>
      ))}
    </div>
  );
};

export default LoadingSkeleton;
