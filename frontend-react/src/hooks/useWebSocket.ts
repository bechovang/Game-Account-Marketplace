// Custom React hook for WebSocket connection management
import { useEffect, useState, useCallback, useRef } from 'react';
import {
  websocketService,
  WebSocketConnectionState,
} from '../services/websocket/websocketService';

export function useWebSocket() {
  const [connectionState, setConnectionState] = useState<WebSocketConnectionState>(
    websocketService.getConnectionState()
  );
  const isInitialized = useRef(false);

  // Set up reactive state change listener
  useEffect(() => {
    // Skip on first render to avoid unnecessary effect
    if (isInitialized.current) {
      return;
    }
    isInitialized.current = true;

    // Register callback for state changes
    const handleStateChange = (state: WebSocketConnectionState) => {
      setConnectionState(state);
    };

    websocketService.onStateChange(handleStateChange);

    // Cleanup: Note - we don't unregister the callback in this implementation
    // because the service doesn't provide an unregister method.
    // This is acceptable since the callback just updates React state.
  }, []);

  const connect = useCallback((jwtToken: string) => {
    return websocketService.connect(jwtToken);
  }, []);

  const disconnect = useCallback(() => {
    websocketService.disconnect();
  }, []);

  return {
    connectionState,
    isConnected: connectionState === WebSocketConnectionState.CONNECTED,
    connect,
    disconnect,
    websocketService,
  };
}
