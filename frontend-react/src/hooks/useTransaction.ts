/**
 * useTransaction Hook
 * Handles fetching transaction details and viewing credentials
 */

import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import {
  getTransaction,
  completeTransaction,
} from '../services/rest/transactionService';
import type { Transaction, CredentialsResponse } from '../types/transaction';

interface UseTransactionResult {
  transaction: Transaction | null;
  credentials: CredentialsResponse | null;
  loading: boolean;
  credentialsLoading: boolean;
  error: string | null;
  viewCredentials: () => Promise<void>;
  refetch: () => Promise<void>;
  silentRefetch: () => Promise<void>;
}

export const useTransaction = (transactionId: number): UseTransactionResult => {
  const [transaction, setTransaction] = useState<Transaction | null>(null);
  const [credentials, setCredentials] = useState<CredentialsResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [credentialsLoading, setCredentialsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchTransaction = useCallback(async (silent = false) => {
    if (!silent) {
      setLoading(true);
    }
    setError(null);

    try {
      const data = await getTransaction(transactionId);
      setTransaction(data);
    } catch (err: any) {
      const errorMessage =
        err.response?.data?.message || err.message || 'Failed to load transaction';
      setError(errorMessage);
      if (!silent) {
        toast.error(errorMessage);
      }

      // If 404, redirect to home
      if (err.response?.status === 404) {
        navigate('/');
      }
    } finally {
      if (!silent) {
        setLoading(false);
      }
    }
  }, [transactionId, navigate]);

  const viewCredentials = async () => {
    setCredentialsLoading(true);
    setError(null);

    try {
      const creds = await completeTransaction(transactionId);
      setCredentials(creds);
      toast.success('Credentials retrieved successfully');
    } catch (err: any) {
      const errorMessage =
        err.response?.data?.message || err.message || 'Failed to retrieve credentials';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setCredentialsLoading(false);
    }
  };

  useEffect(() => {
    fetchTransaction();
  }, [fetchTransaction]);

  // Create silent refetch wrapper
  const silentRefetch = useCallback(async () => {
    await fetchTransaction(true);
  }, [fetchTransaction]);

  return {
    transaction,
    credentials,
    loading,
    credentialsLoading,
    error,
    viewCredentials,
    refetch: () => fetchTransaction(false),
    silentRefetch,
  };
};
