import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useDeleteAccount } from '../hooks/use-graphql';
import type { AccountStatus } from '../types/graphql';
import DeleteAccountModal from '../components/modals/DeleteAccountModal';
import { apiClient } from '../services/rest/axiosInstance';

/**
 * Loading skeleton for account cards
 */
const AccountCardSkeleton = () => (
  <div className="bg-white rounded-lg shadow-md overflow-hidden animate-pulse">
    <div className="h-48 bg-gray-200"></div>
    <div className="p-4 space-y-3">
      <div className="h-4 bg-gray-200 rounded w-3/4"></div>
      <div className="h-3 bg-gray-200 rounded w-1/2"></div>
      <div className="flex justify-between items-center">
        <div className="h-5 bg-gray-200 rounded w-1/4"></div>
        <div className="h-8 bg-gray-200 rounded w-1/3"></div>
      </div>
    </div>
  </div>
);

/**
 * Account Card Component
 */
interface AccountCardProps {
  account: any;
  onEdit: (id: string) => void;
  onDelete: (id: string) => void;
}

const AccountCard = ({ account, onEdit, onDelete }: AccountCardProps) => {
  const getStatusColor = (status: string) => {
    switch (status) {
      case AccountStatus.APPROVED:
        return 'bg-green-100 text-green-800';
      case AccountStatus.PENDING:
        return 'bg-yellow-100 text-yellow-800';
      case AccountStatus.REJECTED:
        return 'bg-red-100 text-red-800';
      case AccountStatus.SOLD:
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
      {/* Images */}
      {account.images && account.images.length > 0 ? (
        <div className="h-48 overflow-hidden">
          <img
            src={account.images[0]}
            alt={account.title}
            className="w-full h-full object-cover"
          />
        </div>
      ) : (
        <div className="h-48 bg-gray-200 flex items-center justify-center">
          <svg className="w-16 h-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
        </div>
      )}

      {/* Content */}
      <div className="p-4">
        {/* Status Badge */}
        <div className="mb-2">
          <span className={`inline-flex px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(account.status)}`}>
            {account.status}
          </span>
        </div>

        {/* Title */}
        <h3 className="text-lg font-semibold text-gray-900 mb-1 truncate">{account.title}</h3>

        {/* Game */}
        <p className="text-sm text-gray-600 mb-2">{account.game?.name}</p>

        {/* Details */}
        {(account.level || account.rank) && (
          <div className="flex items-center space-x-2 text-sm text-gray-500 mb-2">
            {account.level && <span>Level {account.level}</span>}
            {account.level && account.rank && <span>•</span>}
            {account.rank && <span>{account.rank}</span>}
          </div>
        )}

        {/* Price */}
        <p className="text-2xl font-bold text-gray-900 mb-4">${account.price.toFixed(2)}</p>

        {/* Stats */}
        <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
          <span>Views: {account.viewsCount || 0}</span>
          {account.isFeatured && (
            <span className="flex items-center text-yellow-600">
              <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
              Featured
            </span>
          )}
        </div>

        {/* Action Buttons */}
        <div className="flex space-x-2">
          <button
            onClick={() => onEdit(account.id)}
            className="flex-1 px-3 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
          >
            Edit
          </button>
          <button
            onClick={() => onDelete(account.id)}
            className="flex-1 px-3 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors text-sm font-medium"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
};

/**
 * MyListingsPage - Grid view of seller's account listings
 * Displays all accounts with status badges, edit/delete buttons
 * Protected route - requires SELLER or ADMIN role
 */
const MyListingsPage = () => {
  const navigate = useNavigate();
  
  // State for seller's own accounts
  const [accounts, setAccounts] = useState<any[]>([]);
  const [pagination, setPagination] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [accountToDelete, setAccountToDelete] = useState<string | null>(null);
  const { deleteAccount, loading: deleting } = useDeleteAccount();

  // Fetch seller's own accounts from REST endpoint
  const fetchMyListings = async () => {
    try {
      setLoading(true);
      const response = await apiClient.get('/api/accounts/seller/my-accounts');
      
      // Response is paginated
      if (response.content) {
        setAccounts(response.content);
        setPagination({
          totalElements: response.totalElements,
          totalPages: response.totalPages,
          currentPage: response.number,
          pageSize: response.size,
        });
      }
    } catch (error) {
      console.error('Failed to fetch my listings:', error);
      toast.error('Failed to load your listings');
      setAccounts([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMyListings();
  }, []);

  /**
   * Handle edit button click
   */
  const handleEdit = (id: string) => {
    navigate(`/seller/edit/${id}`);
  };

  /**
   * Handle delete button click
   */
  const handleDeleteClick = (id: string) => {
    setAccountToDelete(id);
    setDeleteModalOpen(true);
  };

  /**
   * Confirm delete action
   */
  const handleDeleteConfirm = async () => {
    if (!accountToDelete) return;

    try {
      const success = await deleteAccount({ variables: { id: accountToDelete } });

      if (success) {
        toast.success('Account listing deleted successfully');
        setDeleteModalOpen(false);
        setAccountToDelete(null);
        fetchMyListings(); // Refresh the list
      }
    } catch (error) {
      console.error('Failed to delete account:', error);
      toast.error('Failed to delete account listing');
    }
  };

  /**
   * Close delete modal
   */
  const handleDeleteCancel = () => {
    setDeleteModalOpen(false);
    setAccountToDelete(null);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">My Listings</h1>
          <p className="text-gray-600 mt-1">Manage your game account listings</p>
        </div>
        <button
          onClick={() => navigate('/seller/create')}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
        >
          Create Listing
        </button>
      </div>

      {/* Loading Skeleton */}
      {loading && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {Array.from({ length: 8 }).map((_, i) => (
            <AccountCardSkeleton key={i} />
          ))}
        </div>
      )}

      {/* Empty State */}
      {!loading && accounts.length === 0 && (
        <div className="text-center py-12">
          <svg className="mx-auto h-48 w-48 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
          </svg>
          <h3 className="mt-4 text-lg font-medium text-gray-900">No listings yet</h3>
          <p className="mt-2 text-gray-500">Get started by creating your first account listing</p>
          <button
            onClick={() => navigate('/seller/create')}
            className="mt-6 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
          >
            Create Your First Listing
          </button>
        </div>
      )}

      {/* Account Grid */}
      {!loading && accounts.length > 0 && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {accounts.map((account) => (
              <AccountCard
                key={account.id}
                account={account}
                onEdit={handleEdit}
                onDelete={handleDeleteClick}
              />
            ))}
          </div>

          {/* Pagination Info */}
          {pagination && (
            <div className="mt-6 text-center text-sm text-gray-600">
              Showing {accounts.length} of {pagination.totalElements} listings
              {pagination.totalPages > 1 && (
                <span className="ml-2">
                  (Page {pagination.currentPage + 1} of {pagination.totalPages})
                </span>
              )}
            </div>
          )}
        </>
      )}

      {/* Delete Confirmation Modal */}
      <DeleteAccountModal
        isOpen={deleteModalOpen}
        isLoading={deleting}
        onConfirm={handleDeleteConfirm}
        onCancel={handleDeleteCancel}
      />
    </div>
  );
};

export default MyListingsPage;
