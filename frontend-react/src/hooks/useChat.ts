// Custom hook for real-time chat functionality using WebSocket
import { useState, useEffect, useCallback, useRef } from 'react';
import { websocketService } from '../services/websocket/websocketService';
import type { ChatMessage, TypingIndicator } from '../services/websocket/types';
import { useAuth } from '../contexts/AuthContext';
import { apiClient } from '../services/rest/axiosInstance';

interface UseChatReturn {
  messages: ChatMessage[];
  typingUsers: Map<number, string>; // senderId -> senderEmail
  sendMessage: (content: string) => void;
  sendTypingIndicator: (isTyping: boolean) => void;
  markAsRead: (senderId: number) => void;
  isConnected: boolean;
}

export function useChat(accountId: number, otherUserId: number): UseChatReturn {
  const { user } = useAuth();
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [typingUsers, setTypingUsers] = useState<Map<number, string>>(new Map());
  const typingTimeoutRef = useRef<Map<number, NodeJS.Timeout>>(new Map());
  const [loadedHistory, setLoadedHistory] = useState(false);

  // Load message history on mount
  useEffect(() => {
    // Reset loaded history when accountId changes
    setLoadedHistory(false);
    setMessages([]);

    const loadMessageHistory = async () => {
      try {
        const response = await apiClient.get<ChatMessage[]>(`/api/chat/messages?accountId=${accountId}`);
        setMessages(response);
        setLoadedHistory(true);
      } catch (error) {
        console.error('Failed to load message history:', error);
        setLoadedHistory(true); // Still mark as loaded to avoid infinite retries
      }
    };

    loadMessageHistory();
  }, [accountId]);

  // Subscribe to chat messages
  useEffect(() => {
    const handleChatMessage = (message: ChatMessage) => {
      setMessages((prev) => {
        // Avoid duplicate messages
        if (prev.some((m) => m.id === message.id)) {
          return prev;
        }
        return [...prev, message];
      });
    };

    // Subscribe when connected
    const setupSubscription = () => {
      if (websocketService.isConnected()) {
        websocketService.subscribeToChat(accountId, handleChatMessage);
      }
    };

    setupSubscription();

    // Also set up subscription when connection state changes
    const handleStateChange = () => {
      if (websocketService.isConnected()) {
        setupSubscription();
      }
    };

    websocketService.onStateChange(handleStateChange);

    return () => {
      // Note: Subscription cleanup handled by websocketService on disconnect
    };
  }, [accountId]);

  // Subscribe to typing indicators
  useEffect(() => {
    if (!user) return;

    const handleTypingIndicator = (indicator: TypingIndicator) => {
      // Only show typing for the other user
      if (indicator.senderId !== otherUserId) return;

      if (indicator.isTyping) {
        setTypingUsers((prev) => new Map(prev).set(indicator.senderId, indicator.senderEmail));

        // Clear existing timeout for this user
        const existingTimeout = typingTimeoutRef.current.get(indicator.senderId);
        if (existingTimeout) {
          clearTimeout(existingTimeout);
        }

        // Set new timeout to remove typing indicator after 3 seconds
        const timeout = setTimeout(() => {
          setTypingUsers((prev) => {
            const next = new Map(prev);
            next.delete(indicator.senderId);
            return next;
          });
        }, 3000);

        typingTimeoutRef.current.set(indicator.senderId, timeout);
      } else {
        setTypingUsers((prev) => {
          const next = new Map(prev);
          next.delete(indicator.senderId);
          return next;
        });
      }
    };

    if (websocketService.isConnected()) {
      websocketService.subscribeToTypingIndicators(user.id, handleTypingIndicator);
    }

    return () => {
      // Clean up all typing timeouts
      typingTimeoutRef.current.forEach((timeout) => clearTimeout(timeout));
      typingTimeoutRef.current.clear();
    };
  }, [user, otherUserId]);

  const sendMessage = useCallback((content: string) => {
    if (!user || content.trim() === '') return;
    websocketService.sendMessage(accountId, otherUserId, content.trim());
  }, [accountId, otherUserId, user]);

  const sendTypingIndicator = useCallback((isTyping: boolean) => {
    if (!user) return;
    websocketService.sendTypingIndicator(accountId, otherUserId, isTyping);
  }, [accountId, otherUserId, user]);

  const markAsRead = useCallback((senderId: number) => {
    websocketService.sendReadReceipt(accountId, senderId);
  }, [accountId]);

  return {
    messages,
    typingUsers,
    sendMessage,
    sendTypingIndicator,
    markAsRead,
    isConnected: websocketService.isConnected(),
  };
}
