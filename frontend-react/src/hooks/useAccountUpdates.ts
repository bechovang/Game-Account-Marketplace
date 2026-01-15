// Custom hook for real-time account updates using WebSocket
import { useEffect, useCallback, useRef } from 'react';
import { websocketService } from '../services/websocket/websocketService';
import type { AccountUpdateMessageUnion } from '../types/accountUpdates';
import { toast } from 'sonner';
import { useAuth } from '../contexts/AuthContext';
import { WebSocketConnectionState } from '../services/websocket/types';

/**
 * Hook for handling real-time account updates via WebSocket.
 * Automatically subscribes to /topic/accounts and updates Apollo Client cache.
 *
 * Note: This hook should be used once at the app level (e.g., in App.tsx or AccountList)
 * to ensure all account updates are captured.
 */
export function useAccountUpdates() {
  const { user } = useAuth();
  const isSubscribedRef = useRef(false);

  // Stable callback reference for proper cleanup
  const handleAccountUpdate = useCallback((message: AccountUpdateMessageUnion) => {
    console.log('Received account update:', message);

    // Dispatch custom event for components to listen
    // This allows separation of concerns - WebSocket vs UI state
    const event = new CustomEvent('account-update', {
      detail: message,
    });
    window.dispatchEvent(event);

    // Show toast notification for new accounts
    if (message.type === 'new_account_posted') {
      toast.success('New account posted!', {
        description: message.accountData.title,
        duration: 3000,
      });
    }
  }, []);

  // Stable callback for connection state changes
  const handleStateChange = useCallback((state: WebSocketConnectionState) => {
    if (state === WebSocketConnectionState.CONNECTED) {
      if (!isSubscribedRef.current) {
        websocketService.subscribeToAccountUpdates(handleAccountUpdate);
        isSubscribedRef.current = true;
        console.log('Subscribed to account updates');
      }
    } else if (state === WebSocketConnectionState.DISCONNECTED) {
      isSubscribedRef.current = false;
    }
  }, [handleAccountUpdate]);

  useEffect(() => {
    if (!user) return;

    // Subscribe when connected
    if (websocketService.isConnected() && !isSubscribedRef.current) {
      websocketService.subscribeToAccountUpdates(handleAccountUpdate);
      isSubscribedRef.current = true;
      console.log('Subscribed to account updates');
    }

    // Register state change callback
    websocketService.onStateChange(handleStateChange);

    // Cleanup: Remove state change callback and unsubscribe
    return () => {
      websocketService.offStateChange(handleStateChange);
      websocketService.unsubscribeAccountUpdates();
      isSubscribedRef.current = false;
      console.log('Unsubscribed from account updates');
    };
  }, [user, handleAccountUpdate, handleStateChange]);
}
