/**
 * PurchaseModal Component
 * Modal for purchasing game accounts with PayOS payment
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { X } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { usePurchaseTransaction } from '../../hooks/usePurchaseTransaction';
import type { Account } from '../../types/graphql';
import toast from 'react-hot-toast';

interface PurchaseModalProps {
  isOpen: boolean;
  onClose: () => void;
  account: Account | null;
}

const PurchaseModal: React.FC<PurchaseModalProps> = ({ isOpen, onClose, account }) => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { purchase, loading } = usePurchaseTransaction();

  // Don't render if not open
  if (!isOpen) return null;

  // Check if user is authenticated
  if (!user) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" onClick={onClose} />
        <div className="relative bg-white rounded-lg shadow-xl max-w-md w-full mx-4 p-6">
          <h2 className="text-xl font-bold mb-4">Login Required</h2>
          <p className="text-gray-600 mb-4">Please login to purchase this account.</p>
          <div className="flex gap-3">
            <button
              onClick={() => navigate('/login')}
              className="flex-1 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            >
              Go to Login
            </button>
            <button
              onClick={onClose}
              className="flex-1 bg-gray-200 text-gray-800 px-4 py-2 rounded hover:bg-gray-300"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Check if user is the seller
  if (user && account && user.id === parseInt(account.seller.id)) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" onClick={onClose} />
        <div className="relative bg-white rounded-lg shadow-xl max-w-md w-full mx-4 p-6">
          <h2 className="text-xl font-bold mb-4">Cannot Purchase</h2>
          <p className="text-gray-600 mb-4">You cannot purchase your own account.</p>
          <button
            onClick={onClose}
            className="w-full bg-gray-200 text-gray-800 px-4 py-2 rounded hover:bg-gray-300"
          >
            Close
          </button>
        </div>
      </div>
    );
  }

  // Check if account is available
  if (account && account.status !== 'APPROVED') {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" onClick={onClose} />
        <div className="relative bg-white rounded-lg shadow-xl max-w-md w-full mx-4 p-6">
          <h2 className="text-xl font-bold mb-4">Account Unavailable</h2>
          <p className="text-gray-600 mb-4">This account is currently not available for purchase.</p>
          <button
            onClick={onClose}
            className="w-full bg-gray-200 text-gray-800 px-4 py-2 rounded hover:bg-gray-300"
          >
            Close
          </button>
        </div>
      </div>
    );
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!account) return;

    try {
      await purchase({
        accountId: parseInt(account.id),
      });
      // Redirect will happen automatically in the hook
    } catch (error) {
      // Error is handled in the hook
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/50 backdrop-blur-sm"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="relative bg-white rounded-lg shadow-xl max-w-md w-full mx-4 p-6">
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600"
          disabled={loading}
        >
          <X size={24} />
        </button>

        {/* Header */}
        <h2 className="text-2xl font-bold mb-4">Purchase Account</h2>

        {/* Account Details */}
        {account && (
          <div className="bg-gray-50 rounded-lg p-4 mb-4">
            <h3 className="font-semibold text-lg mb-2">{account.title}</h3>
            <div className="flex justify-between items-center">
              <span className="text-gray-600">
                Level: {account.level || 'N/A'} | Rank: {account.rank || 'N/A'}
              </span>
              <span className="text-2xl font-bold text-green-600">
                ${account.price.toFixed(2)}
              </span>
            </div>
            <p className="text-sm text-gray-500 mt-2">
              Seller: {account.seller.fullName || account.seller.email}
            </p>
          </div>
        )}

        {/* Payment Method */}
        <div className="mb-4">
          <h4 className="font-semibold mb-2">Payment Method</h4>
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 flex items-center gap-3">
            <div className="w-12 h-12 bg-blue-600 rounded flex items-center justify-center text-white font-bold">
              QR
            </div>
            <div>
              <p className="font-medium">PayOS Bank Transfer</p>
              <p className="text-sm text-gray-600">Scan QR code to pay</p>
            </div>
          </div>
        </div>

        {/* Info Message */}
        <div className="bg-green-50 border border-green-200 rounded-lg p-3 mb-4">
          <p className="text-sm text-green-700">
            After payment completion, you will receive the game account credentials (username & password).
          </p>
        </div>

        {/* Action Buttons */}
        <form onSubmit={handleSubmit}>
          <div className="flex gap-3">
            <button
              type="submit"
              disabled={loading}
              className="flex-1 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Processing...
                </>
              ) : (
                'Confirm Purchase'
              )}
            </button>
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300 disabled:bg-gray-100 disabled:cursor-not-allowed"
            >
              Cancel
            </button>
          </div>
        </form>

        {/* Security Note */}
        <p className="text-xs text-gray-500 mt-4 text-center">
          Your payment is secured by PayOS. Account credentials will be revealed after successful payment.
        </p>
      </div>
    </div>
  );
};

export default PurchaseModal;
