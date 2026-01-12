/**
 * TransactionHistoryPage Component
 * Shows user's transaction history with filters and pagination
 */

import React, { useState } from 'react';
import { Filter, ChevronLeft, ChevronRight } from 'lucide-react';
import { useTransactions } from '../../hooks/useTransactions';
import { TransactionStatus } from '../../types/transaction';
import type { Transaction, TransactionFilters } from '../../types/transaction';
import LoadingSkeleton from '../common/LoadingSkeleton';

const TransactionHistoryPage: React.FC = () => {
  const [filters, setFilters] = useState<TransactionFilters>({
    status: undefined,
    page: 0,
    size: 10,
  });
  const [showFilters, setShowFilters] = useState(false);

  const { transactions, loading, error, totalPages, currentPage, refetch, setFilters: updateFilters } =
    useTransactions(filters);

  const handleStatusFilter = (status: TransactionStatus | undefined) => {
    updateFilters({ ...filters, status, page: 0 });
  };

  const handlePageChange = (newPage: number) => {
    updateFilters({ ...filters, page: newPage });
  };

  const getStatusBadgeClass = (status: TransactionStatus): string => {
    switch (status) {
      case TransactionStatus.COMPLETED:
        return 'bg-green-100 text-green-800';
      case TransactionStatus.PENDING:
        return 'bg-yellow-100 text-yellow-800';
      case TransactionStatus.CANCELLED:
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const TransactionRow: React.FC<{ transaction: Transaction }> = ({ transaction }) => (
    <tr className="border-b hover:bg-gray-50">
      <td className="px-4 py-3">#{transaction.id}</td>
      <td className="px-4 py-3">
        <div>
          <div className="font-medium">{transaction.accountTitle}</div>
          <div className="text-sm text-gray-500">Order: {transaction.orderCode}</div>
        </div>
      </td>
      <td className="px-4 py-3 font-semibold text-green-600">
        ${transaction.amount.toFixed(2)}
      </td>
      <td className="px-4 py-3">
        <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusBadgeClass(transaction.status)}`}>
          {transaction.status}
        </span>
      </td>
      <td className="px-4 py-3 text-sm text-gray-600">
        {new Date(transaction.createdAt).toLocaleDateString()}
      </td>
      <td className="px-4 py-3 text-sm">
        <div>Buyer: {transaction.buyerName}</div>
        <div className="text-gray-500">Seller: {transaction.sellerName}</div>
      </td>
      <td className="px-4 py-3">
        <a
          href={`/payment/success?transactionId=${transaction.id}`}
          className="text-blue-600 hover:underline text-sm"
        >
          View Details
        </a>
      </td>
    </tr>
  );

  if (loading && transactions.length === 0) {
    return <LoadingSkeleton type="detail" />;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold">Transaction History</h1>
          <button
            onClick={() => setShowFilters(!showFilters)}
            className="flex items-center gap-2 px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
          >
            <Filter size={20} />
            Filters
          </button>
        </div>

        {/* Filters Panel */}
        {showFilters && (
          <div className="bg-white rounded-lg shadow-md p-4 mb-6">
            <h3 className="font-semibold mb-3">Filter by Status</h3>
            <div className="flex flex-wrap gap-2 mb-4">
              <button
                onClick={() => handleStatusFilter(undefined)}
                className={`px-4 py-2 rounded-lg ${
                  !filters.status
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                All
              </button>
              <button
                onClick={() => handleStatusFilter(TransactionStatus.COMPLETED)}
                className={`px-4 py-2 rounded-lg ${
                  filters.status === TransactionStatus.COMPLETED
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                Completed
              </button>
              <button
                onClick={() => handleStatusFilter(TransactionStatus.PENDING)}
                className={`px-4 py-2 rounded-lg ${
                  filters.status === TransactionStatus.PENDING
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                Pending
              </button>
              <button
                onClick={() => handleStatusFilter(TransactionStatus.CANCELLED)}
                className={`px-4 py-2 rounded-lg ${
                  filters.status === TransactionStatus.CANCELLED
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                Cancelled
              </button>
            </div>
            <h3 className="font-semibold mb-3">Filter by Date Range</h3>
            <div className="flex flex-wrap gap-4">
              <div>
                <label htmlFor="startDate" className="block text-sm text-gray-600 mb-1">
                  From:
                </label>
                <input
                  id="startDate"
                  type="date"
                  value={filters.startDate || ''}
                  onChange={(e) => updateFilters({ ...filters, startDate: e.target.value || undefined, page: 0 })}
                  className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label htmlFor="endDate" className="block text-sm text-gray-600 mb-1">
                  To:
                </label>
                <input
                  id="endDate"
                  type="date"
                  value={filters.endDate || ''}
                  onChange={(e) => updateFilters({ ...filters, endDate: e.target.value || undefined, page: 0 })}
                  className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              {(filters.startDate || filters.endDate) && (
                <button
                  onClick={() => updateFilters({ ...filters, startDate: undefined, endDate: undefined, page: 0 })}
                  className="px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 self-end"
                >
                  Clear Dates
                </button>
              )}
            </div>
          </div>
        )}

        {/* Error State */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
            <p className="text-red-800">{error}</p>
            <button
              onClick={refetch}
              className="mt-2 text-red-600 hover:underline text-sm"
            >
              Retry
            </button>
          </div>
        )}

        {/* Transactions Table */}
        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          {transactions.length === 0 && !loading ? (
            <div className="p-8 text-center">
              <p className="text-gray-500">No transactions found.</p>
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        ID
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Account
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Amount
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Status
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Date
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Parties
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {transactions.map((transaction) => (
                      <TransactionRow key={transaction.id} transaction={transaction} />
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex items-center justify-between px-4 py-3 bg-gray-50 border-t">
                  <div className="text-sm text-gray-600">
                    Page {currentPage + 1} of {totalPages}
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => handlePageChange(currentPage - 1)}
                      disabled={currentPage === 0}
                      className="px-3 py-1 rounded border disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
                    >
                      <ChevronLeft size={20} />
                    </button>
                    <button
                      onClick={() => handlePageChange(currentPage + 1)}
                      disabled={currentPage >= totalPages - 1}
                      className="px-3 py-1 rounded border disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
                    >
                      <ChevronRight size={20} />
                    </button>
                  </div>
                </div>
              )}
            </>
          )}
        </div>

        {/* Summary Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mt-6">
          <div className="bg-white rounded-lg shadow-md p-4">
            <div className="text-sm text-gray-600">Total Transactions</div>
            <div className="text-2xl font-bold">{transactions.length}</div>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4">
            <div className="text-sm text-gray-600">Completed</div>
            <div className="text-2xl font-bold text-green-600">
              {transactions.filter((t) => t.status === TransactionStatus.COMPLETED).length}
            </div>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4">
            <div className="text-sm text-gray-600">Pending</div>
            <div className="text-2xl font-bold text-yellow-600">
              {transactions.filter((t) => t.status === TransactionStatus.PENDING).length}
            </div>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4">
            <div className="text-sm text-gray-600">Total Spent</div>
            <div className="text-2xl font-bold text-blue-600">
              $
              {transactions
                .filter((t) => t.status === TransactionStatus.COMPLETED)
                .reduce((sum, t) => sum + t.amount, 0)
                .toFixed(2)}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TransactionHistoryPage;
