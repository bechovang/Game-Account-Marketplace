// Main chat container component for real-time messaging
import React, { useEffect, useRef, useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Send } from 'lucide-react';
import { useChat } from '../../hooks/useChat';
import { MessageBubble } from './MessageBubble';
import { TypingIndicator } from './TypingIndicator';
import { useAuth } from '../../contexts/AuthContext';

interface ChatBoxProps {
  accountId: number;
  otherUserId: number;
  otherUserEmail: string;
}

export function ChatBox({ accountId, otherUserId, otherUserEmail }: ChatBoxProps) {
  const { user } = useAuth();
  const [messageInput, setMessageInput] = useState('');
  const scrollRef = useRef<HTMLDivElement>(null);
  const {
    messages,
    typingUsers,
    sendMessage,
    sendTypingIndicator,
    markAsRead,
    isConnected,
  } = useChat(accountId, otherUserId);

  // Auto-scroll to bottom when new message arrives
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  // Mark messages as read when chat box mounts
  useEffect(() => {
    if (user && otherUserId) {
      markAsRead(otherUserId);
    }
  }, [user, otherUserId, markAsRead]);

  const handleSendMessage = () => {
    if (messageInput.trim()) {
      sendMessage(messageInput);
      setMessageInput('');
      sendTypingIndicator(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setMessageInput(e.target.value);
    sendTypingIndicator(true);
  };

  // Filter typing users to only show the other user
  const otherUserTyping = typingUsers.has(otherUserId);

  return (
    <div className="flex flex-col h-full bg-white border rounded-lg">
      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 border-b">
        <h3 className="font-semibold">{otherUserEmail}</h3>
        {!isConnected && (
          <span className="text-xs text-red-500">Disconnected</span>
        )}
      </div>

      {/* Messages */}
      <div ref={scrollRef} className="flex-1 overflow-y-auto p-4">
        {messages.length === 0 ? (
          <div className="flex items-center justify-center h-full text-gray-400">
            No messages yet. Start the conversation!
          </div>
        ) : (
          messages.map((message) => (
            <MessageBubble
              key={message.id}
              message={message}
              isOwnMessage={message.senderId === user?.id}
            />
          ))
        )}
        {otherUserTyping && <TypingIndicator users={typingUsers} />}
      </div>

      {/* Input */}
      <div className="flex gap-2 p-4 border-t">
        <Input
          value={messageInput}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          placeholder="Type a message..."
          disabled={!isConnected}
          className="flex-1"
        />
        <Button onClick={handleSendMessage} disabled={!isConnected || !messageInput.trim()}>
          <Send className="h-4 w-4" />
        </Button>
      </div>
    </div>
  );
}
