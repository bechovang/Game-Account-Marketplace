# Story 1.8: Frontend Authentication Pages & Context

Status: review

## Story

As a developer,
I want to create login/register pages with AuthContext and protected routes,
so that users can authenticate and access protected pages.

## Acceptance Criteria

1. **Given** the REST endpoints from Story 1.7
**When** I implement frontend authentication
**Then** AuthContext provides user, token, login, logout, isAuthenticated, isLoading state
**Then** AuthContext stores JWT token in localStorage under 'access_token' key
**And** login() function calls POST /api/auth/login with email/password
**And** login() function stores token and user data on success
**And** login() function redirects to home page on success
**And** login() function displays error message on failure
**And** logout() function removes token and redirects to login page
**And** useAuth hook provides access to AuthContext
**And** LoginPage component has email and password input fields
**And** LoginPage validates email format and password length (min 6 chars)
**And** LoginPage displays loading state during API call
**And** RegisterPage component has email, password, fullName fields
**And** RegisterPage validates all inputs and shows validation errors
**And** ProtectedRoute component checks isAuthenticated before rendering children
**And** ProtectedRoute redirects to /login if not authenticated
**And** React Router is configured with routes: /login, /register, / (protected)
**And** App wraps children with ApolloProvider and AuthProvider
**And** Axios interceptor attaches JWT token to all requests

## Tasks / Subtasks

- [x] Create AuthContext (AC: #, #, #, #, #, #, #, #)
  - [x] Create AuthContext with state (user, token, isAuthenticated, isLoading)
  - [x] Implement login() function with API call
  - [x] Implement logout() function (clear token, redirect)
  - [x] Implement token persistence in localStorage
  - [x] Create useAuth hook
- [x] Create Axios instance with interceptor (AC: #)
  - [x] Create axiosInstance in services/rest/
  - [x] Add request interceptor to attach JWT token
  - [x] Add response interceptor for error handling
  - [x] Handle 401 errors (auto-logout)
- [x] Create ProtectedRoute component (AC: #, #, #)
  - [x] Check isAuthenticated from useAuth
  - [x] Redirect to /login if not authenticated
  - [x] Render children if authenticated
  - [x] Show loading state
- [x] Create LoginPage component (AC: #, #, #, #, #)
  - [x] Email input with validation
  - [x] Password input (min 6 chars)
  - [x] Submit button with loading state
  - [x] Error message display
  - [x] Link to register page
- [x] Create RegisterPage component (AC: #, #, #)
  - [x] Email, password, fullName inputs
  - [x] Validation for all fields
  - [x] Submit button with loading state
  - [x] Error message display
  - [x] Link to login page
- [x] Configure React Router (AC: #, #, #)
  - [x] Create BrowserRouter with routes
  - [x] Public: /login, /register
  - [x] Protected: / wrapped in ProtectedRoute
- [x] Update App.tsx (AC: #, #)
  - [x] Wrap with ApolloProvider
  - [x] Wrap with AuthProvider
  - [x] Add RouterProvider
  - [x] Axios interceptor attaches JWT token to all requests

## Dev Notes

**Important:** This story completes the authentication flow. Frontend now fully communicates with backend REST API.

### AuthContext Template

```typescript
// src/contexts/AuthContext.tsx
import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { apiClient } from '../services/rest/axiosInstance';

interface User {
  id: number;
  email: string;
  fullName: string;
  avatar?: string;
  role: 'BUYER' | 'SELLER' | 'ADMIN';
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Load token from localStorage on mount
    const storedToken = localStorage.getItem('access_token');
    if (storedToken) {
      setToken(storedToken);
      // TODO: Fetch user profile using token
    }
    setIsLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    setIsLoading(true);
    try {
      const response = await apiClient.post('/api/auth/login', { email, password });
      setToken(response.token);
      setUser({
        id: response.userId,
        email: response.email,
        role: response.role,
      });
      localStorage.setItem('access_token', response.token);
      window.location.href = '/'; // Redirect to home
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('access_token');
    window.location.href = '/login';
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token,
        isLoading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
```

### Axios Instance with JWT Interceptor [Source: ARCHITECTURE.md#4.2.3]

```typescript
// src/services/rest/axiosInstance.ts
import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - attach JWT token
axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('access_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - handle 401 errors
axiosInstance.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('access_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;

// Typed API client
export const apiClient = {
  post: <T>(url: string, data: object) =>
    axiosInstance.post<T>(url, data).then((res) => res.data),
};
```

### ProtectedRoute Template

```typescript
// src/components/common/ProtectedRoute.tsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
```

### LoginPage Template

```typescript
// src/pages/LoginPage.tsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login, isLoading } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      await login(email, password);
    } catch (err: any) {
      setError('Invalid email or password');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded shadow-md w-96">
        <h1 className="text-2xl font-bold mb-6">Login</h1>

        {error && (
          <div className="bg-red-100 text-red-700 p-3 rounded mb-4">
            {error}
          </div>
        )}

        <div className="mb-4">
          <label className="block mb-2">Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full p-2 border rounded"
            required
          />
        </div>

        <div className="mb-6">
          <label className="block mb-2">Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-2 border rounded"
            minLength={6}
            required
          />
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600"
        >
          {isLoading ? 'Logging in...' : 'Login'}
        </button>

        <p className="mt-4 text-center">
          Don't have an account? <Link to="/register" className="text-blue-500">Register</Link>
        </p>
      </form>
    </div>
  );
};

export default LoginPage;
```

### RegisterPage Template

```typescript
// src/pages/RegisterPage.tsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const RegisterPage: React.FC = () => {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login, isLoading } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      // Register then login
      await apiClient.post('/api/auth/register', { email, password, fullName });
      await login(email, password);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded shadow-md w-96">
        <h1 className="text-2xl font-bold mb-6">Register</h1>

        {error && (
          <div className="bg-red-100 text-red-700 p-3 rounded mb-4">
            {error}
          </div>
        )}

        <div className="mb-4">
          <label className="block mb-2">Full Name</label>
          <input
            type="text"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            className="w-full p-2 border rounded"
            required
            minLength={2}
          />
        </div>

        <div className="mb-4">
          <label className="block mb-2">Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full p-2 border rounded"
            required
          />
        </div>

        <div className="mb-6">
          <label className="block mb-2">Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-2 border rounded"
            minLength={6}
            required
          />
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600"
        >
          {isLoading ? 'Creating account...' : 'Register'}
        </button>

        <p className="mt-4 text-center">
          Already have an account? <Link to="/login" className="text-blue-500">Login</Link>
        </p>
      </form>
    </div>
  );
};

export default RegisterPage;
```

### App.tsx Router Configuration

```typescript
// src/App.tsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ApolloProvider } from '@apollo/client';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage'; // Create this or use placeholder

function App() {
  return (
    <ApolloProvider client={apolloClient}>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <HomePage />
                </ProtectedRoute>
              }
            />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ApolloProvider>
  );
}

export default App;
```

### Common Pitfalls to Avoid

🚨 **CRITICAL WARNINGS:**
1. **Token not attached to requests** - Axios interceptor MUST add Authorization header
2. **401 not handled** - Must auto-logout on 401, otherwise infinite redirect loops
3. **isLoading not checked** - Will flash login page before auth check completes
4. **localStorage key mismatch** - MUST use 'access_token' consistently
5. **Missing CORS headers** - Browser will block requests without proper CORS
6. **Form submission not prevented** - Must call e.preventDefault() to avoid page reload
7. **Not validating password length** - Minimum 6 chars per requirements
8. **useAuth context undefined** - Must wrap App with AuthProvider

### Testing Standards

```bash
cd frontend-react
npm run dev

# Test in browser:
# 1. Visit http://localhost:3000 - should redirect to /login
# 2. Try to access / directly - should redirect to /login
# 3. Register new account - should redirect to / after successful registration
# 4. Logout - should redirect to /login and clear token
# 5. Check localStorage - token should be present after login
```

### Requirements Traceability

**FR1:** Register UI ✅ RegisterPage component
**FR2:** Login UI ✅ LoginPage component
**FR3:** Logout ✅ logout() function
**NFR4:** Page load < 2s ✅ Vite provides fast loading
**NFR33:** Responsive ✅ Tailwind CSS mobile-first design

### Dependencies

Story 1.3 (Frontend Setup) - Required for Vite + React structure
Story 1.7 (REST API) - Required for API endpoints

### References

- Architecture.md Section 4.2.3: Axios Instance with JWT
- Story 1.3: Frontend structure
- Story 1.7: REST API endpoints

---

## Dev Agent Record

### Agent Model Used
Claude Opus 4.5

### Completion Notes List
Story 1.8 completed successfully on 2026-01-07.

**Completed Tasks:**
1. Created AuthContext with useState for user, token, isAuthenticated, isLoading
2. Implemented login() function calling POST /api/auth/login with JWT token storage
3. Implemented logout() function clearing token and redirecting to /login
4. Token persisted in localStorage under 'access_token' key
5. Created useAuth hook for accessing AuthContext
6. Created axiosInstance with JWT interceptor in services/rest/
7. Request interceptor attaches JWT token to all requests
8. Response interceptor handles 401 errors with auto-logout
9. Created ProtectedRoute component checking isAuthenticated
10. Created LoginPage with email/password validation (min 6 chars)
11. Created RegisterPage with email/password/fullName validation
12. Configured React Router with public routes (/login, /register) and protected route (/)
13. Created Apollo Client setup with JWT auth link
14. Updated App.tsx with ApolloProvider, AuthProvider, and BrowserRouter
15. Build successful - verified with `npm run build`

**Notes:**
- Axios 1.6.5 type compatibility issue resolved by removing explicit type annotations
- All components use Tailwind CSS for styling
- Protected routes redirect to /login if not authenticated
- 401 errors automatically clear token and redirect to login

**All acceptance criteria met.**

### File List
- `frontend-react/src/contexts/AuthContext.tsx` (CREATE)
- `frontend-react/src/services/rest/axiosInstance.ts` (CREATE)
- `frontend-react/src/components/common/ProtectedRoute.tsx` (CREATE)
- `frontend-react/src/pages/LoginPage.tsx` (CREATE)
- `frontend-react/src/pages/RegisterPage.tsx` (CREATE)
- `frontend-react/src/pages/HomePage.tsx` (CREATE)
- `frontend-react/src/lib/apolloClient.ts` (CREATE)
- `frontend-react/src/App.tsx` (MODIFY - added Router, ApolloProvider, AuthProvider)
