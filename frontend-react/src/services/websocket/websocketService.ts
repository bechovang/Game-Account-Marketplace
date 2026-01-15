// WebSocket service for real-time communication using STOMP over SockJS
import { Client } from '@stomp/stompjs';
import type { IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type {
  ChatMessage,
  Notification,
  AccountUpdate,
  AccountUpdateMessageUnion,
  TypingIndicator,
} from './types';
import { WebSocketConnectionState } from './types';

// Types for subscription details to support re-subscription
type SubscriptionDetails =
  | { type: 'chat'; accountId: number; callback: (message: ChatMessage) => void }
  | { type: 'notifications'; userId: number; callback: (notification: Notification) => void }
  | { type: 'account-updates'; callback: (update: AccountUpdateMessageUnion) => void }
  | { type: 'typing'; userId: number; callback: (indicator: TypingIndicator) => void };

class WebSocketService {
  private client: Client | null = null;
  private connectionState: WebSocketConnectionState = WebSocketConnectionState.DISCONNECTED;
  private subscriptions: Map<string, StompSubscription> = new Map();
  private pendingSubscriptions: SubscriptionDetails[] = [];
  private connectionPromise: Promise<void> | null = null;
  private resolveConnection: (() => void) | null = null;
  private jwtToken: string | null = null;
  private stateChangeCallbacks: Array<(state: WebSocketConnectionState) => void> = [];

  /**
   * Register a callback for connection state changes.
   */
  onStateChange(callback: (state: WebSocketConnectionState) => void): void {
    this.stateChangeCallbacks.push(callback);
  }

  /**
   * Unregister a callback for connection state changes.
   * Used to prevent memory leaks when components unmount.
   */
  offStateChange(callback: (state: WebSocketConnectionState) => void): void {
    this.stateChangeCallbacks = this.stateChangeCallbacks.filter((cb) => cb !== callback);
  }

  /**
   * Notify all registered callbacks of state change.
   */
  private notifyStateChange(state: WebSocketConnectionState): void {
    this.stateChangeCallbacks.forEach((callback) => callback(state));
  }

  /**
   * Connect to WebSocket server with JWT authentication.
   *
   * @param jwtToken JWT token for authentication
   * @returns Promise that resolves when connection is established
   */
  connect(jwtToken: string): Promise<void> {
    // If already connected, return resolved promise
    if (this.client && this.client.connected) {
      console.warn('WebSocket already connected');
      return Promise.resolve();
    }

    // If connecting, return existing promise
    if (this.connectionPromise) {
      return this.connectionPromise;
    }

    this.jwtToken = jwtToken;
    this.setConnectionState(WebSocketConnectionState.CONNECTING);

    // Create new promise for connection
    this.connectionPromise = new Promise((resolve) => {
      this.resolveConnection = resolve;

      this.client = new Client({
        webSocketFactory: () => new SockJS(import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws'),
        connectHeaders: {
          Authorization: `Bearer ${jwtToken}`,
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        debug: (str) => console.log(str),
        onConnect: () => {
          console.log('WebSocket connected');
          this.setConnectionState(WebSocketConnectionState.CONNECTED);
          // Re-establish all pending subscriptions after reconnect
          this.resubscribeAll();
          // Resolve connection promise
          if (this.resolveConnection) {
            this.resolveConnection();
            this.resolveConnection = null;
          }
          this.connectionPromise = null;
        },
        onDisconnect: () => {
          console.log('WebSocket disconnected');
          this.setConnectionState(WebSocketConnectionState.DISCONNECTED);
        },
        onStompError: (frame) => {
          console.error('STOMP error:', frame);
          this.setConnectionState(WebSocketConnectionState.ERROR);
        },
        onWebSocketError: (event) => {
          console.error('WebSocket error:', event);
          this.setConnectionState(WebSocketConnectionState.ERROR);
        },
        onWebSocketClose: (event) => {
          console.warn('WebSocket closed:', event);
          this.setConnectionState(WebSocketConnectionState.DISCONNECTED);
        },
      });

      this.client.activate();
    });

    return this.connectionPromise;
  }

  /**
   * Disconnect from WebSocket server.
   */
  disconnect(): void {
    if (this.client) {
      // Unsubscribe from all subscriptions
      this.subscriptions.forEach((sub) => sub.unsubscribe());
      this.subscriptions.clear();
      // Clear pending subscriptions
      this.pendingSubscriptions = [];

      this.client.deactivate();
      this.client = null;
      this.setConnectionState(WebSocketConnectionState.DISCONNECTED);
      console.log('WebSocket disconnected by client');

      // Reset connection promise
      this.connectionPromise = null;
      this.resolveConnection = null;
    }
  }

  /**
   * Update connection state and notify listeners.
   */
  private setConnectionState(state: WebSocketConnectionState): void {
    this.connectionState = state;
    this.notifyStateChange(state);
  }

  /**
   * Re-subscribe to all pending subscriptions after reconnect.
   */
  private resubscribeAll(): void {
    if (!this.client || !this.client.connected) {
      return;
    }

    console.log(`Re-subscribing to ${this.pendingSubscriptions.length} subscriptions`);

    // Create a copy to avoid modification during iteration
    const subsToRestore = [...this.pendingSubscriptions];

    // Clear pending and re-add each subscription
    this.pendingSubscriptions = [];
    subsToRestore.forEach((subDetails) => {
      switch (subDetails.type) {
        case 'chat':
          this.subscribeToChat(subDetails.accountId, subDetails.callback);
          break;
        case 'notifications':
          this.subscribeToNotifications(subDetails.userId, subDetails.callback);
          break;
        case 'account-updates':
          this.subscribeToAccountUpdates(subDetails.callback);
          break;
        case 'typing':
          this.subscribeToTypingIndicators(subDetails.userId, subDetails.callback);
          break;
      }
    });
  }

  /**
   * Unsubscribe existing subscription for a given key if it exists.
   */
  private unsubscribeIfExists(key: string): void {
    const existing = this.subscriptions.get(key);
    if (existing) {
      existing.unsubscribe();
      this.subscriptions.delete(key);
    }
  }

  /**
   * Subscribe to chat messages for a specific account.
   *
   * @param accountId Account ID
   * @param callback Callback function for incoming messages
   */
  subscribeToChat(accountId: number, callback: (message: ChatMessage) => void): void {
    const key = `chat-${accountId}`;

    // If not connected, store in pending subscriptions
    if (!this.client || !this.client.connected) {
      console.warn(`WebSocket not connected, storing pending subscription for ${key}`);
      // Remove existing pending subscription for this key
      this.pendingSubscriptions = this.pendingSubscriptions.filter((s) => {
        if (s.type === 'chat' && s.accountId === accountId) {
          return false;
        }
        return true;
      });
      this.pendingSubscriptions.push({ type: 'chat', accountId, callback });
      return;
    }

    // Unsubscribe existing subscription for this key (fixes memory leak)
    this.unsubscribeIfExists(key);

    const subscription = this.client.subscribe(`/topic/chat/${accountId}`, (message: IMessage) => {
      const chatMessage: ChatMessage = JSON.parse(message.body);
      callback(chatMessage);
    });

    this.subscriptions.set(key, subscription);

    // Store in pending for re-subscription after reconnect
    const existingPending = this.pendingSubscriptions.find(
      (s) => s.type === 'chat' && s.accountId === accountId
    );
    if (!existingPending) {
      this.pendingSubscriptions.push({ type: 'chat', accountId, callback });
    }
  }

  /**
   * Subscribe to user notifications.
   *
   * @param userId User ID
   * @param callback Callback function for incoming notifications
   */
  subscribeToNotifications(userId: number, callback: (notification: Notification) => void): void {
    const key = `notifications-${userId}`;

    // If not connected, store in pending subscriptions
    if (!this.client || !this.client.connected) {
      console.warn(`WebSocket not connected, storing pending subscription for ${key}`);
      // Remove existing pending subscription for this key
      this.pendingSubscriptions = this.pendingSubscriptions.filter((s) => {
        if (s.type === 'notifications' && s.userId === userId) {
          return false;
        }
        return true;
      });
      this.pendingSubscriptions.push({ type: 'notifications', userId, callback });
      return;
    }

    // Unsubscribe existing subscription for this key (fixes memory leak)
    this.unsubscribeIfExists(key);

    const subscription = this.client.subscribe(`/topic/notifications/${userId}`, (message: IMessage) => {
      const notification: Notification = JSON.parse(message.body);
      callback(notification);
    });

    this.subscriptions.set(key, subscription);

    // Store in pending for re-subscription after reconnect
    const existingPending = this.pendingSubscriptions.find(
      (s) => s.type === 'notifications' && s.userId === userId
    );
    if (!existingPending) {
      this.pendingSubscriptions.push({ type: 'notifications', userId, callback });
    }
  }

  /**
   * Subscribe to account status updates (broadcasts).
   *
   * @param callback Callback function for account updates
   */
  subscribeToAccountUpdates(callback: (update: AccountUpdateMessageUnion) => void): void {
    const key = 'account-updates';

    // If not connected, store in pending subscriptions
    if (!this.client || !this.client.connected) {
      console.warn(`WebSocket not connected, storing pending subscription for ${key}`);
      // Remove existing pending subscription for this key
      this.pendingSubscriptions = this.pendingSubscriptions.filter((s) => s.type !== 'account-updates');
      this.pendingSubscriptions.push({ type: 'account-updates', callback });
      return;
    }

    // Unsubscribe existing subscription for this key (fixes memory leak)
    this.unsubscribeIfExists(key);

    const subscription = this.client.subscribe('/topic/accounts', (message: IMessage) => {
      const backendUpdate: AccountUpdate = JSON.parse(message.body);

      // Transform backend AccountUpdate format to frontend AccountUpdateMessageUnion format
      const frontendMessage: AccountUpdateMessageUnion =
        backendUpdate.eventType === 'new_account_posted' && backendUpdate.account
          ? {
              type: 'new_account_posted',
              accountId: String(backendUpdate.account.id),
              timestamp: new Date().toISOString(),
              accountData: {
                id: String(backendUpdate.account.id),
                title: backendUpdate.account.title,
                price: backendUpdate.account.price,
                game: backendUpdate.account.game.name,
                rank: backendUpdate.account.rank,
                status: backendUpdate.account.status,
                seller: {
                  id: String(backendUpdate.account.seller.id),
                  username: backendUpdate.account.seller.id.toString(), // Backend uses seller ID
                },
                createdAt: backendUpdate.account.createdAt,
              },
            }
          : {
              type: 'account_status_changed',
              accountId: String(backendUpdate.accountId || ''),
              timestamp: new Date().toISOString(),
              oldStatus: backendUpdate.previousStatus || '',
              newStatus: backendUpdate.status || '',
            };

      callback(frontendMessage);
    });

    this.subscriptions.set(key, subscription);

    // Store in pending for re-subscription after reconnect
    const existingPending = this.pendingSubscriptions.find((s) => s.type === 'account-updates');
    if (!existingPending) {
      this.pendingSubscriptions.push({ type: 'account-updates', callback });
    }
  }

  /**
   * Unsubscribe from account status updates.
   * Used to prevent memory leaks when components unmount.
   */
  unsubscribeAccountUpdates(): void {
    const key = 'account-updates';
    this.unsubscribeIfExists(key);
    // Also remove from pending subscriptions
    this.pendingSubscriptions = this.pendingSubscriptions.filter((s) => s.type !== 'account-updates');
  }

  /**
   * Subscribe to typing indicators for current user.
   *
   * @param userId Current user ID
   * @param callback Callback function for typing indicators
   */
  subscribeToTypingIndicators(userId: number, callback: (indicator: TypingIndicator) => void): void {
    const key = `typing-${userId}`;

    // If not connected, store in pending subscriptions
    if (!this.client || !this.client.connected) {
      console.warn(`WebSocket not connected, storing pending subscription for ${key}`);
      // Remove existing pending subscription for this key
      this.pendingSubscriptions = this.pendingSubscriptions.filter((s) => {
        if (s.type === 'typing' && s.userId === userId) {
          return false;
        }
        return true;
      });
      this.pendingSubscriptions.push({ type: 'typing', userId, callback });
      return;
    }

    // Unsubscribe existing subscription for this key (fixes memory leak)
    this.unsubscribeIfExists(key);

    const subscription = this.client.subscribe(`/queue/typing/${userId}`, (message: IMessage) => {
      const indicator: TypingIndicator = JSON.parse(message.body);
      callback(indicator);
    });

    this.subscriptions.set(key, subscription);

    // Store in pending for re-subscription after reconnect
    const existingPending = this.pendingSubscriptions.find(
      (s) => s.type === 'typing' && s.userId === userId
    );
    if (!existingPending) {
      this.pendingSubscriptions.push({ type: 'typing', userId, callback });
    }
  }

  /**
   * Send a chat message.
   *
   * @param accountId Account ID
   * @param receiverId Recipient user ID
   * @param content Message content
   */
  sendMessage(accountId: number, receiverId: number, content: string): void {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    this.client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({
        accountId,
        receiverId,
        content,
      }),
    });
  }

  /**
   * Send typing indicator.
   *
   * @param accountId Account ID
   * @param receiverId Recipient user ID
   * @param isTyping Typing status
   */
  sendTypingIndicator(accountId: number, receiverId: number, isTyping: boolean): void {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    this.client.publish({
      destination: '/app/chat.typing',
      body: JSON.stringify({
        accountId,
        receiverId,
        isTyping,
      }),
    });
  }

  /**
   * Send read receipt for messages from another user.
   *
   * @param accountId Account ID
   * @param otherUserId The other user whose messages to mark as read
   */
  sendReadReceipt(accountId: number, otherUserId: number): void {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    this.client.publish({
      destination: '/app/chat.read',
      body: JSON.stringify({
        accountId,
        receiverId: otherUserId,
      }),
    });
  }

  /**
   * Get current connection state.
   */
  getConnectionState(): WebSocketConnectionState {
    return this.connectionState;
  }

  /**
   * Check if WebSocket is connected.
   */
  isConnected(): boolean {
    return (
      this.connectionState === WebSocketConnectionState.CONNECTED &&
      this.client !== null &&
      this.client.connected
    );
  }
}

// Export singleton instance
export const websocketService = new WebSocketService();
