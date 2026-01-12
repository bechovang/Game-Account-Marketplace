/**
 * usePurchaseTransaction Hook
 * Handles purchasing a game account
 */

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { purchaseAccount } from '../services/rest/transactionService';
import type { PurchaseRequest } from '../types/transaction';
import { STORAGE_KEYS } from '../utils/constants';

interface UsePurchaseTransactionResult {
  purchase: (request: PurchaseRequest) => Promise<void>;
  loading: boolean;
  error: string | null;
}

export const usePurchaseTransaction = (): UsePurchaseTransactionResult => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const purchase = async (request: PurchaseRequest) => {
    setLoading(true);
    setError(null);

    try {
      const response = await purchaseAccount(request);

      // Store transactionId in sessionStorage for later retrieval
      sessionStorage.setItem(STORAGE_KEYS.PENDING_TRANSACTION_ID, response.transactionId.toString());

      // Redirect to PayOS checkout
      window.location.href = response.checkoutUrl;
    } catch (err: any) {
      const errorMessage =
        err.response?.data?.message || err.message || 'Failed to purchase account';
      setError(errorMessage);
      toast.error(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { purchase, loading, error };
};
