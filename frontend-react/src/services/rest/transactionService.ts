/**
 * Transaction Service API
 * Handles all transaction-related API calls
 */

import axiosInstance from './axiosInstance';
import type {
  Transaction,
  PurchaseRequest,
  PurchaseResponse,
  CredentialsResponse,
  TransactionFilters,
} from '../../types/transaction';

const TRANSACTIONS_BASE_URL = '/api/transactions';

/**
 * Purchase a game account
 * Creates a transaction and returns the payment URL
 */
export const purchaseAccount = async (
  request: PurchaseRequest
): Promise<PurchaseResponse> => {
  const response = await axiosInstance.post<PurchaseResponse>(
    `${TRANSACTIONS_BASE_URL}/purchase`,
    request
  );
  return response.data;
};

/**
 * Get current user's transactions (as buyer or seller)
 */
export const getMyTransactions = async (
  filters?: TransactionFilters
): Promise<Transaction[]> => {
  const params = new URLSearchParams();

  if (filters?.status) {
    params.append('status', filters.status);
  }
  if (filters?.startDate) {
    params.append('startDate', filters.startDate);
  }
  if (filters?.endDate) {
    params.append('endDate', filters.endDate);
  }
  if (filters?.page) {
    params.append('page', filters.page.toString());
  }
  if (filters?.size) {
    params.append('size', filters.size.toString());
  }

  const queryString = params.toString();
  const url = queryString
    ? `${TRANSACTIONS_BASE_URL}?${queryString}`
    : TRANSACTIONS_BASE_URL;

  const response = await axiosInstance.get<Transaction[]>(url);
  return response.data;
};

/**
 * Get transaction by ID
 */
export const getTransaction = async (id: number): Promise<Transaction> => {
  const response = await axiosInstance.get<Transaction>(
    `${TRANSACTIONS_BASE_URL}/${id}`
  );
  return response.data;
};

/**
 * Complete a transaction and retrieve credentials
 */
export const completeTransaction = async (
  id: number
): Promise<CredentialsResponse> => {
  const response = await axiosInstance.put<CredentialsResponse>(
    `${TRANSACTIONS_BASE_URL}/${id}/complete`
  );
  return response.data;
};

/**
 * Cancel a transaction
 */
export const cancelTransaction = async (id: number): Promise<void> => {
  await axiosInstance.put(`${TRANSACTIONS_BASE_URL}/${id}/cancel`);
};

export default {
  purchaseAccount,
  getMyTransactions,
  getTransaction,
  completeTransaction,
  cancelTransaction,
};
