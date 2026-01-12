/**
 * PurchasePage Component
 * Shows transaction status and credentials after payment
 */

import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { CheckCircle2, Clock, XCircle, Eye, EyeOff, Home, Star } from 'lucide-react';
import { useTransaction } from '../../hooks/useTransaction';
import { TransactionStatus } from '../../types/transaction';
import { STORAGE_KEYS } from '../../utils/constants';
import LoadingSkeleton from '../common/LoadingSkeleton';
import ReviewModal from '../review/ReviewModal';
import { useAuth } from '../../contexts/AuthContext';

const PurchasePage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const transactionId = searchParams.get('transactionId');
  const [showCredentials, setShowCredentials] = useState(false);
  const [isReviewModalOpen, setIsReviewModalOpen] = useState(false);

  // If no transactionId, try to get from sessionStorage
  const effectiveTransactionId = transactionId || sessionStorage.getItem(STORAGE_KEYS.PENDING_TRANSACTION_ID);

  const { transaction, credentials, loading, credentialsLoading, error, viewCredentials, silentRefetch } = useTransaction(
    effectiveTransactionId ? parseInt(effectiveTransactionId) : 0
  );

  useEffect(() => {
    // Clear sessionStorage on component mount
    if (transactionId) {
      sessionStorage.removeItem(STORAGE_KEYS.PENDING_TRANSACTION_ID);
    }
  }, [transactionId]);

  // Poll transaction status when PENDING (uses silent refetch to avoid UI flash)
  useEffect(() => {
    if (!transaction || transaction.status !== TransactionStatus.PENDING) {
      return;
    }

    // Poll every 2 seconds, max 5 minutes (150 attempts)
    const pollInterval = setInterval(() => {
      silentRefetch();
    }, 2000);

    // Stop polling after 5 minutes
    const timeoutId = setTimeout(() => {
      clearInterval(pollInterval);
    }, 5 * 60 * 1000);

    // Cleanup on unmount or status change
    return () => {
      clearInterval(pollInterval);
      clearTimeout(timeoutId);
    };
  }, [transaction, silentRefetch]);

  if (!effectiveTransactionId) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6 text-center max-w-md mx-auto">
          <XCircle className="w-16 h-16 text-yellow-600 mx-auto mb-4" />
          <h2 className="text-xl font-bold mb-2">No Transaction Found</h2>
          <p className="text-gray-600 mb-4">
            Unable to locate transaction information. Please check your transaction history.
          </p>
          <button
            onClick={() => navigate('/transactions')}
            className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
          >
            View Transaction History
          </button>
        </div>
      </div>
    );
  }

  if (loading) {
    return <LoadingSkeleton type="detail" />;
  }

  if (error || !transaction) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center max-w-md mx-auto">
          <XCircle className="w-16 h-16 text-red-600 mx-auto mb-4" />
          <h2 className="text-xl font-bold mb-2">Transaction Error</h2>
          <p className="text-gray-600 mb-4">{error || 'Failed to load transaction details'}</p>
          <button
            onClick={() => navigate('/transactions')}
            className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
          >
            View Transaction History
          </button>
        </div>
      </div>
    );
  }

  const isCompleted = transaction.status === TransactionStatus.COMPLETED;
  const isPending = transaction.status === TransactionStatus.PENDING;
  const isCancelled = transaction.status === TransactionStatus.CANCELLED;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-2xl mx-auto">
        {/* Status Banner */}
        <div
          className={`rounded-lg p-6 mb-6 ${
            isCompleted
              ? 'bg-green-50 border border-green-200'
              : isPending
              ? 'bg-yellow-50 border border-yellow-200'
              : 'bg-red-50 border border-red-200'
          }`}
        >
          <div className="flex items-center gap-4">
            {isCompleted ? (
              <CheckCircle2 className="w-12 h-12 text-green-600 flex-shrink-0" />
            ) : isPending ? (
              <Clock className="w-12 h-12 text-yellow-600 flex-shrink-0 animate-pulse" />
            ) : (
              <XCircle className="w-12 h-12 text-red-600 flex-shrink-0" />
            )}
            <div>
              <h2
                className={`text-2xl font-bold ${
                  isCompleted ? 'text-green-800' : isPending ? 'text-yellow-800' : 'text-red-800'
                }`}
              >
                {isCompleted
                  ? 'Purchase Completed!'
                  : isPending
                  ? 'Payment Pending'
                  : 'Payment Cancelled'}
              </h2>
              <p className={isCompleted ? 'text-green-600' : isPending ? 'text-yellow-600' : 'text-red-600'}>
                {isCompleted
                  ? 'You have successfully purchased this game account.'
                  : isPending
                  ? 'Your payment is being processed. This page will update automatically.'
                  : 'The payment was cancelled or failed.'}
              </p>
            </div>
          </div>
        </div>

        {/* Transaction Details */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h3 className="text-xl font-semibold mb-4">Transaction Details</h3>
          <div className="space-y-3">
            <div className="flex justify-between">
              <span className="text-gray-600">Transaction ID:</span>
              <span className="font-medium">#{transaction.id}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Order Code:</span>
              <span className="font-medium">{transaction.orderCode}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Account:</span>
              <span className="font-medium">{transaction.accountTitle}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Amount Paid:</span>
              <span className="font-bold text-green-600">${transaction.amount.toFixed(2)}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Date:</span>
              <span className="font-medium">
                {new Date(transaction.createdAt).toLocaleString()}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Status:</span>
              <span
                className={`px-3 py-1 rounded-full text-sm font-medium ${
                  isCompleted
                    ? 'bg-green-100 text-green-800'
                    : isPending
                    ? 'bg-yellow-100 text-yellow-800'
                    : 'bg-red-100 text-red-800'
                }`}
              >
                {transaction.status}
              </span>
            </div>
          </div>
        </div>

        {/* Credentials Section (Only if completed) */}
        {isCompleted && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <h3 className="text-xl font-semibold mb-4">Account Credentials</h3>

            {credentials ? (
              <div className="space-y-4">
                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                  <p className="text-sm text-gray-600 mb-2">
                    Your game account credentials are ready. Keep them secure!
                  </p>
                  <div className="space-y-3">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Username
                      </label>
                      <div className="flex items-center gap-2">
                        <code className="flex-1 bg-white px-3 py-2 rounded border font-mono">
                          {showCredentials ? credentials.username : '••••••••'}
                        </code>
                        <button
                          onClick={() => setShowCredentials(!showCredentials)}
                          className="p-2 text-gray-500 hover:text-gray-700"
                          type="button"
                        >
                          {showCredentials ? <EyeOff size={20} /> : <Eye size={20} />}
                        </button>
                      </div>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Password
                      </label>
                      <div className="flex items-center gap-2">
                        <code className="flex-1 bg-white px-3 py-2 rounded border font-mono">
                          {showCredentials ? credentials.password : '••••••••'}
                        </code>
                        <button
                          onClick={() => setShowCredentials(!showCredentials)}
                          className="p-2 text-gray-500 hover:text-gray-700"
                          type="button"
                        >
                          {showCredentials ? <EyeOff size={20} /> : <Eye size={20} />}
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-center">
                <p className="text-gray-600 mb-4">
                  Click the button below to retrieve your account credentials.
                </p>
                <button
                  onClick={viewCredentials}
                  disabled={credentialsLoading}
                  className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
                >
                  {credentialsLoading ? 'Loading...' : 'View Credentials'}
                </button>
              </div>
            )}
          </div>
        )}

        {/* Action Buttons */}
        <div className="flex gap-3">
          <button
            onClick={() => navigate('/')}
            className="flex-1 bg-gray-200 text-gray-800 px-6 py-3 rounded-lg hover:bg-gray-300 flex items-center justify-center gap-2"
          >
            <Home size={20} />
            Back to Home
          </button>
          {/* Leave Review Button - Only show if transaction is completed */}
          {isCompleted && user && (
            <button
              onClick={() => setIsReviewModalOpen(true)}
              className="flex-1 bg-yellow-500 text-white px-6 py-3 rounded-lg hover:bg-yellow-600 flex items-center justify-center gap-2"
            >
              <Star size={20} />
              Leave Review
            </button>
          )}
          <button
            onClick={() => navigate('/transactions')}
            className="flex-1 bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700"
          >
            View Transaction History
          </button>
        </div>

        {/* Review Modal */}
        {effectiveTransactionId && (
          <ReviewModal
            isOpen={isReviewModalOpen}
            onClose={() => setIsReviewModalOpen(false)}
            transactionId={parseInt(effectiveTransactionId)}
            onSuccess={() => {
              // Refresh data after review is submitted
              if (effectiveTransactionId) {
                viewCredentials();
              }
            }}
          />
        )}

        {/* Support Info */}
        {isPending && (
          <div className="mt-6 bg-blue-50 border border-blue-200 rounded-lg p-4 text-center">
            <p className="text-sm text-gray-600">
              This page will automatically refresh when your payment is processed.
              If you have completed the payment but the status is still pending after a few minutes,
              please contact support.
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default PurchasePage;
