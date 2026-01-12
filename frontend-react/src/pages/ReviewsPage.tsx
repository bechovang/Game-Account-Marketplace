/**
 * ReviewsPage Component
 * Page for viewing all reviews for a seller with stats and review submission
 */

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { User, Star as StarIcon, ArrowLeft } from 'lucide-react';
import StarRating from '../components/review/StarRating';
import ReviewList from '../components/review/ReviewList';
import ReviewModal from '../components/review/ReviewModal';
import reviewService from '../services/rest/reviewService';
import type { Review, SellerStatsResponse } from '../types/review';
import toast from 'react-hot-toast';

const ReviewsPage: React.FC = () => {
  const { sellerId } = useParams<{ sellerId: string }>();
  const navigate = useNavigate();

  const [reviews, setReviews] = useState<Review[]>([]);
  const [stats, setStats] = useState<SellerStatsResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isReviewModalOpen, setIsReviewModalOpen] = useState(false);
  const [sellerName, setSellerName] = useState<string>('');

  useEffect(() => {
    if (sellerId) {
      loadSellerData(parseInt(sellerId));
    }
  }, [sellerId]);

  const loadSellerData = async (id: number) => {
    setIsLoading(true);
    try {
      const [reviewsData, statsData] = await Promise.all([
        reviewService.getSellerReviews(id),
        reviewService.getSellerStats(id),
      ]);

      setReviews(reviewsData);
      setStats(statsData);

      // Extract seller name from first review if available
      if (reviewsData.length > 0 && reviewsData[0].targetUser) {
        setSellerName(
          reviewsData[0].targetUser.fullName || reviewsData[0].targetUser.email
        );
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to load reviews');
    } finally {
      setIsLoading(false);
    }
  };

  const handleReviewSubmitted = () => {
    if (sellerId) {
      loadSellerData(parseInt(sellerId));
    }
  };

  const formatRating = (rating: number): string => {
    return rating.toFixed(1);
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-12 h-12 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">Loading reviews...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Back Button */}
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-6"
        >
          <ArrowLeft size={20} />
          <span>Back</span>
        </button>

        {/* Seller Info Section */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <div className="flex items-center gap-4 mb-4">
            {/* Avatar */}
            <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center">
              <User size={32} className="text-blue-600" />
            </div>

            {/* Seller Name and Rating */}
            <div className="flex-1">
              <h1 className="text-2xl font-bold text-gray-900 mb-1">
                {sellerName || `Seller #${sellerId}`}
              </h1>
              <div className="flex items-center gap-3">
                <div className="flex items-center gap-1">
                  <StarIcon size={20} className="text-yellow-400 fill-yellow-400" />
                  <span className="text-lg font-semibold text-gray-900">
                    {stats ? formatRating(stats.averageRating) : '0.0'}
                  </span>
                </div>
                <span className="text-gray-500">
                  ({stats?.totalReviews || 0} review{stats?.totalReviews !== 1 ? 's' : ''})
                </span>
              </div>
            </div>

            {/* Large Star Rating Display */}
            <div className="text-center">
              <StarRating rating={stats?.averageRating || 0} size="lg" />
              <p className="text-sm text-gray-500 mt-1">Average Rating</p>
            </div>
          </div>

          {/* Write Review Button */}
          <div className="border-t pt-4">
            <button
              onClick={() => setIsReviewModalOpen(true)}
              className="w-full sm:w-auto px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Write a Review
            </button>
          </div>
        </div>

        {/* Reviews Section */}
        <div>
          <h2 className="text-xl font-bold text-gray-900 mb-4">
            Reviews ({stats?.totalReviews || 0})
          </h2>
          <ReviewList reviews={reviews} isLoading={isLoading} />
        </div>
      </div>

      {/* Review Modal */}
      {/* Note: This would need a valid transactionId in a real implementation */}
      {/* For now, the modal is disabled until a valid transaction context is provided */}
      {isReviewModalOpen && (
        <ReviewModal
          isOpen={isReviewModalOpen}
          onClose={() => setIsReviewModalOpen(false)}
          transactionId={0} // This would come from actual transaction data
          onSuccess={handleReviewSubmitted}
        />
      )}
    </div>
  );
};

export default ReviewsPage;
