import React from 'react';

interface LoadingSkeletonProps {
  type?: 'card' | 'list' | 'detail';
}

const LoadingSkeleton: React.FC<LoadingSkeletonProps> = ({ type = 'detail' }) => {
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
