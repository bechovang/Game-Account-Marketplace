// React Context for global notification state management
import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import type { Notification, NotificationContextType } from '../types/notifications';
import { NotificationType } from '../types/notifications';
import type { Notification as WsNotification } from '../services/websocket/types';

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

const STORAGE_KEY = 'notifications';
const MAX_NOTIFICATIONS = 50;

export function NotificationProvider({ children }: { children: React.ReactNode }) {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  // Load from localStorage on mount
  useEffect(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      try {
        const parsed = JSON.parse(stored);
        setNotifications(parsed);
      } catch (e) {
        console.error('Failed to parse notifications from localStorage:', e);
      }
    }
  }, []);

  // Define callbacks first (before useEffect that depends on them)
  const addNotification = useCallback((wsNotification: WsNotification) => {
    const notification: Notification = {
      id: wsNotification.id,
      type: wsNotification.type as NotificationType,
      title: wsNotification.title,
      message: wsNotification.message,
      data: wsNotification.data,
      createdAt: wsNotification.createdAt,
      isRead: wsNotification.isRead,
    };

    setNotifications((prev) => {
      // Avoid duplicates
      if (prev.some((n) => n.id === notification.id)) {
        return prev;
      }
      // Add new notification and keep only MAX_NOTIFICATIONS
      const updated = [notification, ...prev].slice(0, MAX_NOTIFICATIONS);
      return updated;
    });
  }, []);

  const markAsRead = useCallback((id: string) => {
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
    );
  }, []);

  const markAllAsRead = useCallback(() => {
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
  }, []);

  const clearAll = useCallback(() => {
    setNotifications([]);
  }, []);

  // Listen for WebSocket notifications dispatched by useNotifications hook
  // This must come AFTER addNotification is defined
  useEffect(() => {
    const handleWsNotification = (event: CustomEvent<WsNotification>) => {
      const wsNotification = event.detail;
      addNotification(wsNotification);
    };

    // Type assertion for CustomEvent
    window.addEventListener('ws-notification', handleWsNotification as EventListener);

    return () => {
      window.removeEventListener('ws-notification', handleWsNotification as EventListener);
    };
  }, [addNotification]);

  // Save to localStorage when notifications change
  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(notifications));
  }, [notifications]);

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  return (
    <NotificationContext.Provider
      value={{
        notifications,
        unreadCount,
        addNotification,
        markAsRead,
        markAllAsRead,
        clearAll,
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
}

export function useNotification() {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within NotificationProvider');
  }
  return context;
}
