// Notification types and interfaces for the notification system
import type { Notification as WsNotification } from '../services/websocket/types';

export enum NotificationType {
  ACCOUNT_APPROVED = 'ACCOUNT_APPROVED',
  ACCOUNT_REJECTED = 'ACCOUNT_REJECTED',
  ACCOUNT_SOLD = 'ACCOUNT_SOLD',
  NEW_TRANSACTION = 'NEW_TRANSACTION',
  PAYMENT_RECEIVED = 'PAYMENT_RECEIVED',
}

export interface Notification {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  data?: Record<string, unknown>;
  createdAt: string;
  isRead: boolean;
}

export interface NotificationContextType {
  notifications: Notification[];
  unreadCount: number;
  addNotification: (notification: WsNotification) => void;
  markAsRead: (id: string) => void;
  markAllAsRead: () => void;
  clearAll: () => void;
}
