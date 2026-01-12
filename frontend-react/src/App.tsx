import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ApolloProvider } from '@apollo/client';
import { Toaster } from '@/components/ui/sonner';
import { apolloClient } from './lib/apolloClient';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';
import SellerRoute from './components/common/SellerRoute';
import AppHeader from './components/layout/AppHeader';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import FavoritesPage from './pages/FavoritesPage';
import ProfilePage from './pages/ProfilePage';
import CreateListingPage from './pages/CreateListingPage';
import EditListingPage from './pages/EditListingPage';
import MyListingsPage from './pages/MyListingsPage';
import AccountDetailPage from './pages/account/AccountDetailPage';
import PurchasePage from './components/purchase/PurchasePage';
import TransactionHistoryPage from './components/purchase/TransactionHistoryPage';

function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-background">
      <AppHeader />
      <main className="min-h-[calc(100vh-4rem)]">
        {children}
      </main>
    </div>
  );
}

function App() {
  console.log('🚀 App component rendering...');
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
                  <Layout>
                    <HomePage />
                  </Layout>
                </ProtectedRoute>
              }
            />

            {/* Favorites Page */}
            <Route
              path="/favorites"
              element={
                <ProtectedRoute>
                  <Layout>
                    <FavoritesPage />
                  </Layout>
                </ProtectedRoute>
              }
            />

            {/* Profile Page */}
            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <Layout>
                    <ProfilePage />
                  </Layout>
                </ProtectedRoute>
              }
            />

            {/* Seller Routes - Requires SELLER or ADMIN Role */}
            <Route
              path="/seller/create"
              element={
                <SellerRoute>
                  <Layout>
                    <CreateListingPage />
                  </Layout>
                </SellerRoute>
              }
            />
            <Route
              path="/seller/edit/:id"
              element={
                <SellerRoute>
                  <Layout>
                    <EditListingPage />
                  </Layout>
                </SellerRoute>
              }
            />
            <Route
              path="/seller/my-listings"
              element={
                <SellerRoute>
                  <Layout>
                    <MyListingsPage />
                  </Layout>
                </SellerRoute>
              }
            />

            {/* Account Detail Page */}
            <Route
              path="/accounts/:accountId"
              element={
                <ProtectedRoute>
                  <Layout>
                    <AccountDetailPage />
                  </Layout>
                </ProtectedRoute>
              }
            />

            {/* Purchase Success Page */}
            <Route
              path="/payment/success"
              element={
                <ProtectedRoute>
                  <Layout>
                    <PurchasePage />
                  </Layout>
                </ProtectedRoute>
              }
            />

            {/* Transaction History Page */}
            <Route
              path="/transactions"
              element={
                <ProtectedRoute>
                  <Layout>
                    <TransactionHistoryPage />
                  </Layout>
                </ProtectedRoute>
              }
            />

            {/* Catch All - Redirect to Home */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>

          {/* Toast Notifications */}
          <Toaster richColors position="top-right" />
        </BrowserRouter>
      </AuthProvider>
    </ApolloProvider>
  );
}

export default App;
