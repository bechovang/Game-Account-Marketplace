// Component for displaying typing indicator animation
import React from 'react';

interface TypingIndicatorProps {
  users: Map<number, string>;
}

export function TypingIndicator({ users }: TypingIndicatorProps) {
  if (users.size === 0) return null;

  const emails = Array.from(users.values());
  const text = emails.length === 1
    ? `${emails[0]} is typing...`
    : `${emails[0]} and ${emails.length - 1} other${emails.length > 2 ? 's' : ''} are typing...`;

  return (
    <div className="flex items-center gap-2 px-4 py-2 text-sm text-gray-500">
      <span>{text}</span>
      <div className="flex gap-1">
        <span className="w-2 h-2 bg-gray-400 rounded-full animate-bounce [animation-delay:0ms]" />
        <span className="w-2 h-2 bg-gray-400 rounded-full animate-bounce [animation-delay:150ms]" />
        <span className="w-2 h-2 bg-gray-400 rounded-full animate-bounce [animation-delay:300ms]" />
      </div>
    </div>
  );
}
