import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import toast from 'react-hot-toast';

interface SellerRouteProps {
  children: React.ReactNode;
}

/**
 * SellerRoute - Protected route that requires SELLER or ADMIN role
 * Redirects to login if not authenticated
 * Redirects to home with error message if not authorized
 */
const SellerRoute: React.FC<SellerRouteProps> = ({ children }) => {
  const { isAuthenticated, isLoading, user } = useAuth();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Check if user has SELLER or ADMIN role
  if (user?.role !== 'SELLER' && user?.role !== 'ADMIN') {
    toast.error('Access denied. Seller privileges required.');
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

export default SellerRoute;
