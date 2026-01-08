import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { apiClient } from '../services/rest/axiosInstance';
import toast from 'react-hot-toast';

interface User {
  id: number;
  email: string;
  fullName: string;
  avatar?: string;
  role: 'BUYER' | 'SELLER' | 'ADMIN';
}

const ProfilePage: React.FC = () => {
  const { user, token } = useAuth();
  const [isUpdating, setIsUpdating] = useState(false);

  const handleBecomeSeller = async () => {
    if (!token) {
      toast.error('You must be logged in');
      return;
    }

    setIsUpdating(true);
    try {
      const response = await apiClient.post<User>('/api/users/become-seller');
      toast.success('You are now a SELLER! You can create account listings.');

      // Reload page to update user context
      setTimeout(() => window.location.reload(), 1000);
    } catch (error: any) {
      console.error('Failed to become seller:', error);
      toast.error(error?.response?.data?.message || 'Failed to become seller');
    } finally {
      setIsUpdating(false);
    }
  };

  if (!user) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold text-gray-900">Please log in</h2>
        </div>
      </div>
    );
  }

  const isBuyer = user.role === 'BUYER';
  const isSeller = user.role === 'SELLER' || user.role === 'ADMIN';

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="container mx-auto px-4 max-w-2xl">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h1 className="text-2xl font-bold text-gray-900">My Profile</h1>
        </div>

        {/* User Info Card */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center space-x-4 mb-6">
            {user.avatar ? (
              <img
                src={user.avatar}
                alt={user.fullName}
                className="w-16 h-16 rounded-full object-cover"
              />
            ) : (
              <div className="w-16 h-16 rounded-full bg-blue-500 flex items-center justify-center text-white text-xl font-semibold">
                {user.fullName.charAt(0).toUpperCase()}
              </div>
            )}
            <div>
              <h2 className="text-xl font-semibold text-gray-900">{user.fullName}</h2>
              <p className="text-gray-600">{user.email}</p>
            </div>
          </div>

          <div className="border-t pt-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-500">Role</p>
                <p className="font-medium text-gray-900">
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    user.role === 'ADMIN'
                      ? 'bg-purple-100 text-purple-800'
                      : user.role === 'SELLER'
                      ? 'bg-green-100 text-green-800'
                      : 'bg-blue-100 text-blue-800'
                  }`}>
                    {user.role}
                  </span>
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-500">User ID</p>
                <p className="font-medium text-gray-900">#{user.id}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Role Upgrade Card */}
        {isBuyer && (
          <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg shadow-md p-6 text-white">
            <div className="flex items-start justify-between">
              <div>
                <h3 className="text-lg font-semibold mb-2">Become a Seller</h3>
                <p className="text-blue-100 mb-4">
                  Upgrade your account to start selling game accounts. As a seller, you can:
                </p>
                <ul className="text-blue-100 text-sm space-y-1 mb-4">
                  <li className="flex items-center">
                    <span className="mr-2">✓</span>
                    Create unlimited account listings
                  </li>
                  <li className="flex items-center">
                    <span className="mr-2">✓</span>
                    Manage your inventory
                  </li>
                  <li className="flex items-center">
                    <span className="mr-2">✓</span>
                    Track views and favorites
                  </li>
                </ul>
              </div>
              <button
                onClick={handleBecomeSeller}
                disabled={isUpdating}
                className="px-6 py-3 bg-white text-blue-600 rounded-lg font-semibold hover:bg-blue-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors whitespace-nowrap"
              >
                {isUpdating ? 'Processing...' : 'Become Seller'}
              </button>
            </div>
          </div>
        )}

        {/* Seller Status */}
        {isSeller && (
          <div className="bg-green-50 border border-green-200 rounded-lg p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <svg className="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div className="ml-3">
                <h3 className="text-lg font-medium text-green-900">
                  You are a {user.role}!
                </h3>
                <p className="text-green-700">
                  {user.role === 'SELLER' && 'You can create and manage account listings.'}
                  {user.role === 'ADMIN' && 'You have full access to all features.'}
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Quick Links */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Links</h3>
          <div className="space-y-2">
            {isSeller && (
              <>
                <a
                  href="/seller/create"
                  className="block px-4 py-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                >
                  Create New Listing
                </a>
                <a
                  href="/seller/my-listings"
                  className="block px-4 py-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                >
                  My Listings
                </a>
              </>
            )}
            <a
              href="/favorites"
              className="block px-4 py-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
            >
              My Favorites
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
