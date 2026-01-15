// Component for displaying a single chat message bubble
import React from 'react';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import { formatDistanceToNow } from 'date-fns';
import type { ChatMessage } from '../../services/websocket/types';

interface MessageBubbleProps {
  message: ChatMessage;
  isOwnMessage: boolean;
  senderAvatar?: string;
}

export function MessageBubble({ message, isOwnMessage, senderAvatar }: MessageBubbleProps) {
  return (
    <div className={`flex gap-3 mb-4 ${isOwnMessage ? 'flex-row-reverse' : 'flex-row'}`}>
      {/* Avatar for received messages */}
      {!isOwnMessage && (
        <Avatar className="h-8 w-8">
          <AvatarImage src={senderAvatar} alt={message.senderEmail} />
          <AvatarFallback>
            {message.senderEmail.charAt(0).toUpperCase()}
          </AvatarFallback>
        </Avatar>
      )}

      <div className={`flex flex-col ${isOwnMessage ? 'items-end' : 'items-start'} max-w-[70%]`}>
        {/* Sender name for received messages */}
        {!isOwnMessage && (
          <span className="text-xs text-gray-500 mb-1">
            {message.senderEmail}
          </span>
        )}

        {/* Message bubble */}
        <div
          className={`px-4 py-2 rounded-2xl ${
            isOwnMessage
              ? 'bg-blue-500 text-white rounded-br-sm'
              : 'bg-gray-200 text-gray-900 rounded-bl-sm'
          }`}
        >
          <p className="text-sm break-words">{message.content}</p>
        </div>

        {/* Timestamp */}
        <span className="text-xs text-gray-400 mt-1">
          {formatDistanceToNow(new Date(message.createdAt), { addSuffix: true })}
        </span>
      </div>
    </div>
  );
}
