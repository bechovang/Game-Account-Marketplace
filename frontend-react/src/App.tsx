import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ApolloProvider } from '@apollo/client';
import { Toaster } from 'react-hot-toast';
import { apolloClient } from './lib/apolloClient';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';
import SellerRoute from './components/common/SellerRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import SearchPage from './pages/SearchPage';
import FavoritesPage from './pages/FavoritesPage';
import ProfilePage from './pages/ProfilePage';
import CreateListingPage from './pages/CreateListingPage';
import EditListingPage from './pages/EditListingPage';
import MyListingsPage from './pages/MyListingsPage';
import AccountDetailPage from './pages/account/AccountDetailPage';

function App() {
  return (
    <ApolloProvider client={apolloClient}>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Protected Routes - Requires Authentication */}
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <HomePage />
                </ProtectedRoute>
              }
            />

            {/* Search Page */}
            <Route
              path="/search"
              element={
                <ProtectedRoute>
                  <SearchPage />
                </ProtectedRoute>
              }
            />

            {/* Favorites Page */}
            <Route
              path="/favorites"
              element={
                <ProtectedRoute>
                  <FavoritesPage />
                </ProtectedRoute>
              }
            />

            {/* Profile Page */}
            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              }
            />

            {/* Seller Routes - Requires SELLER or ADMIN Role */}
            <Route
              path="/seller/create"
              element={
                <SellerRoute>
                  <CreateListingPage />
                </SellerRoute>
              }
            />
            <Route
              path="/seller/edit/:id"
              element={
                <SellerRoute>
                  <EditListingPage />
                </SellerRoute>
              }
            />
            <Route
              path="/seller/my-listings"
              element={
                <SellerRoute>
                  <MyListingsPage />
                </SellerRoute>
              }
            />

            {/* Account Detail Page */}
            <Route
              path="/accounts/:accountId"
              element={
                <ProtectedRoute>
                  <AccountDetailPage />
                </ProtectedRoute>
              }
            />

            {/* Catch All - Redirect to Home */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>

        {/* Toast Notifications */}
        <Toaster position="top-right" toastOptions={{ duration: 3000 }} />
      </AuthProvider>
    </ApolloProvider>
  );
}

export default App;
