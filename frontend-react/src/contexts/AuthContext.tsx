import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { apiClient } from '../services/rest/axiosInstance';
import { websocketService } from '../services/websocket/websocketService';

interface User {
  id: number;
  email: string;
  fullName: string;
  avatar?: string;
  role: 'BUYER' | 'SELLER' | 'ADMIN';
}

interface LoginResponse {
  token: string;
  userId: number;
  email: string;
  fullName: string;
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

  // Fetch user profile from backend using stored token
  const fetchUserProfile = async (authToken: string): Promise<User | null> => {
    try {
      const response = await apiClient.get<User>('/api/auth/me');
      return response;
    } catch (error) {
      console.error('Failed to fetch user profile:', error);
      // If token is invalid, clear it
      localStorage.removeItem('access_token');
      return null;
    }
  };

  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('access_token');
      if (storedToken) {
        setToken(storedToken);
        // Fetch user profile using token
        const userProfile = await fetchUserProfile(storedToken);
        if (userProfile) {
          setUser(userProfile);
          // Connect to WebSocket with stored token
          websocketService.connect(storedToken);
        } else {
          // Token was invalid, clear it
          setToken(null);
        }
      }
      setIsLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (email: string, password: string) => {
    setIsLoading(true);
    try {
      const response = await apiClient.post<LoginResponse>('/api/auth/login', { email, password });
      const userData: User = {
        id: response.userId,
        email: response.email,
        fullName: response.fullName,
        role: response.role,
      };

      setToken(response.token);
      setUser(userData);
      localStorage.setItem('access_token', response.token);
      // Connect to WebSocket
      websocketService.connect(response.token);
      window.location.href = '/'; // Redirect to home
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    // Disconnect from WebSocket
    websocketService.disconnect();
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
