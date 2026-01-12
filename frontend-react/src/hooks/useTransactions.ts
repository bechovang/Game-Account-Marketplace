/**
 * useTransactions Hook
 * Handles fetching user's transactions with filters and pagination
 */

import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { getMyTransactions } from '../services/rest/transactionService';
import type { Transaction, TransactionFilters, TransactionStatus } from '../types/transaction';

interface UseTransactionsResult {
  transactions: Transaction[];
  loading: boolean;
  error: string | null;
  totalPages: number;
  currentPage: number;
  refetch: () => Promise<void>;
  setFilters: (filters: TransactionFilters) => void;
}

export const useTransactions = (initialFilters?: TransactionFilters): UseTransactionsResult => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFiltersState] = useState<TransactionFilters>(initialFilters || {});
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);

  const fetchTransactions = async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await getMyTransactions(filters);
      setTransactions(data);

      // Calculate pagination (assuming 10 items per page based on API)
      setTotalPages(Math.ceil(data.length / 10));
      setCurrentPage(filters.page || 0);
    } catch (err: any) {
      const errorMessage =
        err.response?.data?.message || err.message || 'Failed to load transactions';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const setFilters = (newFilters: TransactionFilters) => {
    setFiltersState({ ...filters, ...newFilters });
  };

  useEffect(() => {
    fetchTransactions();
  }, [filters]);

  return {
    transactions,
    loading,
    error,
    totalPages,
    currentPage,
    refetch: fetchTransactions,
    setFilters,
  };
};
