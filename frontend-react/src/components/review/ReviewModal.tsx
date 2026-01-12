/**
 * ReviewModal Component
 * Modal for submitting product reviews with star rating
 */

import React, { useState } from 'react';
import { X } from 'lucide-react';
import StarRating from './StarRating';
import reviewService from '../../services/rest/reviewService';
import toast from 'react-hot-toast';

interface ReviewModalProps {
  isOpen: boolean;
  onClose: () => void;
  transactionId: number;
  onSuccess?: () => void;
}

const ReviewModal: React.FC<ReviewModalProps> = ({
  isOpen,
  onClose,
  transactionId,
  onSuccess,
}) => {
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [commentError, setCommentError] = useState('');

  // Reset form when modal opens/closes
  React.useEffect(() => {
    if (!isOpen) {
      setRating(0);
      setComment('');
      setCommentError('');
    }
  }, [isOpen]);

  const validateComment = (): boolean => {
    if (!comment.trim()) {
      setCommentError('Please write a review');
      return false;
    }
    if (comment.trim().length < 10) {
      setCommentError('Review must be at least 10 characters');
      return false;
    }
    if (comment.length > 1000) {
      setCommentError('Review must not exceed 1000 characters');
      return false;
    }
    setCommentError('');
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate rating
    if (rating === 0) {
      toast.error('Please select a rating');
      return;
    }

    // Validate comment
    if (!validateComment()) {
      return;
    }

    setIsLoading(true);
    try {
      await reviewService.createReview({
        transactionId,
        rating,
        comment: comment.trim(),
      });
      toast.success('Review submitted successfully!');
      setRating(0);
      setComment('');
      setCommentError('');
      onSuccess?.();
      onClose();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || 'Failed to submit review';
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCommentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const value = e.target.value;
    if (value.length <= 1000) {
      setComment(value);
      if (commentError) validateComment();
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/50 backdrop-blur-sm"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="relative bg-white rounded-lg shadow-xl max-w-md w-full">
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600"
          disabled={isLoading}
        >
          <X size={24} />
        </button>

        <div className="p-6">
          <h2 className="text-xl font-bold mb-4">Leave a Review</h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Star Rating */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Your Rating <span className="text-red-500">*</span>
              </label>
              <StarRating
                rating={rating}
                onRatingChange={setRating}
                interactive={true}
                size="lg"
              />
            </div>

            {/* Comment */}
            <div>
              <label htmlFor="comment" className="block text-sm font-medium text-gray-700 mb-2">
                Your Review <span className="text-red-500">*</span>
              </label>
              <textarea
                id="comment"
                value={comment}
                onChange={handleCommentChange}
                rows={4}
                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  commentError ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="Share your experience with this seller..."
                disabled={isLoading}
              />
              <div className="flex justify-between items-center mt-1">
                {commentError && (
                  <p className="text-sm text-red-600">{commentError}</p>
                )}
                <p className={`text-sm ${comment.length > 1000 ? 'text-red-600' : 'text-gray-500'} ${!commentError ? 'ml-auto' : ''}`}>
                  {comment.length}/1000
                </p>
              </div>
            </div>

            {/* Actions */}
            <div className="flex justify-end gap-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                disabled={isLoading}
                className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isLoading}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center gap-2"
              >
                {isLoading ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    Submitting...
                  </>
                ) : (
                  'Submit Review'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ReviewModal;
