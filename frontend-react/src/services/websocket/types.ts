// WebSocket type definitions for real-time communication
import type { AccountUpdateMessageUnion } from '../../types/accountUpdates';

export type { AccountUpdateMessageUnion as AccountUpdateMessage };

export interface ChatMessage {
  id: string;
  accountId: number;
  senderId: number;
  senderEmail: string;
  receiverId: number;
  content: string;
  isRead: boolean;
  createdAt: string;
}

export interface Notification {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  data: Record<string, unknown>;
  createdAt: string;
  isRead: boolean;
}

export type NotificationType =
  | 'ACCOUNT_APPROVED'
  | 'ACCOUNT_REJECTED'
  | 'ACCOUNT_SOLD'
  | 'NEW_TRANSACTION'
  | 'PAYMENT_RECEIVED';

export interface AccountUpdate {
  eventType: 'new_account_posted' | 'account_status_changed';
  account?: AccountData;
  accountId?: number;
  status?: string;
  previousStatus?: string;
}

export interface AccountData {
  id: number;
  title: string;
  price: number;
  level: number;
  rank: string;
  game: { id: number; name: string };
  seller: { id: number };
  status: string;
  createdAt: string;
}

export interface TypingIndicator {
  accountId: number;
  senderId: number;
  senderEmail: string;
  isTyping: boolean;
}

export enum WebSocketConnectionState {
  CONNECTING = 'CONNECTING',
  CONNECTED = 'CONNECTED',
  DISCONNECTED = 'DISCONNECTED',
  ERROR = 'ERROR',
}
