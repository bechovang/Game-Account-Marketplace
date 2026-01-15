// Custom hook for real-time notification functionality using WebSocket
import { useEffect, useRef, useState } from 'react';
import { websocketService } from '../services/websocket/websocketService';
import type { Notification as WsNotification } from '../services/websocket/types';
import { WebSocketConnectionState } from '../services/websocket/types';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'sonner';
import { getNotificationIcon } from '../utils/notificationIcons';

/**
 * Hook for handling real-time notifications via WebSocket.
 * Automatically subscribes to user's notification topic and shows toast notifications.
 *
 * Note: This hook should be used once at the app level (e.g., in App.tsx)
 * to ensure all notifications are captured.
 */
export function useNotifications() {
  const { user } = useAuth();
  const [isConnected, setIsConnected] = useState(false);
  const isSubscribedRef = useRef(false);

  useEffect(() => {
    if (!user) return;

    const handleNotification = (wsNotification: WsNotification) => {
      console.log('Received notification:', wsNotification);

      // Dispatch custom event for NotificationContext to listen to
      // This allows separation of concerns - WebSocket vs UI state
      const event = new CustomEvent('ws-notification', {
        detail: wsNotification,
      });
      window.dispatchEvent(event);

      // Show toast notification using sonner with proper styling and 5 second auto-hide
      const iconConfig = getNotificationIcon(wsNotification.type as any);
      toast(wsNotification.message, {
        title: wsNotification.title,
        description: wsNotification.message,
        duration: 5000,
        icon: iconConfig.icon,
        className: getToastClass(wsNotification.type),
      });
    };

    // Subscribe when connected
    const setupSubscription = () => {
      if (websocketService.isConnected() && !isSubscribedRef.current) {
        websocketService.subscribeToNotifications(user.id, handleNotification);
        isSubscribedRef.current = true;
        setIsConnected(true);
        console.log(`Subscribed to notifications for user ${user.id}`);
      }
    };

    setupSubscription();

    // Handle connection state changes
    const handleStateChange = (state: WebSocketConnectionState) => {
      if (state === WebSocketConnectionState.CONNECTED) {
        setupSubscription();
      } else if (state === WebSocketConnectionState.DISCONNECTED) {
        isSubscribedRef.current = false;
        setIsConnected(false);
      }
    };

    websocketService.onStateChange(handleStateChange);

    // Cleanup
    return () => {
      isSubscribedRef.current = false;
    };
  }, [user]);

  return {
    isConnected,
  };
}

/**
 * Get toast CSS class based on notification type for color styling
 */
function getToastClass(type: string): string {
  switch (type) {
    case 'ACCOUNT_APPROVED':
      return 'notification-toast-success';
    case 'ACCOUNT_REJECTED':
      return 'notification-toast-error';
    case 'ACCOUNT_SOLD':
    case 'NEW_TRANSACTION':
      return 'notification-toast-info';
    case 'PAYMENT_RECEIVED':
      return 'notification-toast-info';
    default:
      return 'notification-toast-default';
  }
}
