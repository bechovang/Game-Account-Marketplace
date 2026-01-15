// Component for displaying list of chat conversations
import React from 'react';
import { Badge } from '../ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import { formatDistanceToNow } from 'date-fns';

export interface Conversation {
  accountId: number;
  accountTitle: string;
  otherUser: {
    id: number;
    email: string;
    avatar?: string;
  };
  lastMessage?: {
    content: string;
    createdAt: string;
  };
  unreadCount: number;
}

interface ConversationListProps {
  conversations: Conversation[];
  selectedAccountId: number | null;
  onSelectConversation: (accountId: number, otherUserId: number) => void;
}

export function ConversationList({
  conversations,
  selectedAccountId,
  onSelectConversation,
}: ConversationListProps) {
  return (
    <div className="w-full md:w-80 bg-white border rounded-lg">
      <div className="px-4 py-3 border-b">
        <h2 className="font-semibold">Messages</h2>
      </div>
      <div className="h-[calc(100vh-200px)] overflow-y-auto">
        {conversations.length === 0 ? (
          <div className="flex items-center justify-center h-48 text-gray-400">
            No conversations yet
          </div>
        ) : (
          <div className="divide-y">
            {conversations.map((conversation) => (
              <div
                key={conversation.accountId}
                onClick={() => onSelectConversation(conversation.accountId, conversation.otherUser.id)}
                className={`flex items-center gap-3 p-4 cursor-pointer hover:bg-gray-50 transition-colors ${
                  selectedAccountId === conversation.accountId ? 'bg-blue-50' : ''
                }`}
              >
                <Avatar>
                  <AvatarImage src={conversation.otherUser.avatar} />
                  <AvatarFallback>
                    {conversation.otherUser.email.charAt(0).toUpperCase()}
                  </AvatarFallback>
                </Avatar>

                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="font-medium truncate">{conversation.otherUser.email}</span>
                    {conversation.lastMessage && (
                      <span className="text-xs text-gray-400">
                        {formatDistanceToNow(new Date(conversation.lastMessage.createdAt), { addSuffix: true })}
                      </span>
                    )}
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-500 truncate">
                      {conversation.lastMessage
                        ? conversation.lastMessage.content.slice(0, 50) +
                          (conversation.lastMessage.content.length > 50 ? '...' : '')
                        : conversation.accountTitle}
                    </span>
                    {conversation.unreadCount > 0 && (
                      <Badge variant="destructive" className="text-xs">
                        {conversation.unreadCount}
                      </Badge>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
