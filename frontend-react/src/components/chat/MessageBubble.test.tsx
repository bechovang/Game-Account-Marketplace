// Test file for MessageBubble component
import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MessageBubble } from './MessageBubble';

// Mock date-fns
vi.mock('date-fns', () => ({
  formatDistanceToNow: vi.fn(() => '5 minutes ago'),
}));

describe('MessageBubble', () => {
  const mockMessage = {
    id: 'msg-123',
    accountId: 1,
    senderId: 1,
    senderEmail: 'sender@example.com',
    receiverId: 2,
    content: 'Hello, this is a test message',
    isRead: false,
    createdAt: '2024-01-14T10:00:00Z',
  };

  it('renders received message with avatar and sender name', () => {
    render(<MessageBubble message={mockMessage} isOwnMessage={false} />);

    expect(screen.getByText('sender@example.com')).toBeInTheDocument();
    expect(screen.getByText('Hello, this is a test message')).toBeInTheDocument();
    expect(screen.getByText('5 minutes ago')).toBeInTheDocument();
  });

  it('renders sent message without avatar or sender name', () => {
    render(<MessageBubble message={mockMessage} isOwnMessage={true} />);

    expect(screen.queryByText('sender@example.com')).not.toBeInTheDocument();
    expect(screen.getByText('Hello, this is a test message')).toBeInTheDocument();
  });

  it('applies correct styling for own messages', () => {
    const { container } = render(<MessageBubble message={mockMessage} isOwnMessage={true} />);
    const messageContainer = container.querySelector('.flex-row-reverse');
    expect(messageContainer).toBeInTheDocument();
  });

  it('applies correct styling for received messages', () => {
    const { container } = render(<MessageBubble message={mockMessage} isOwnMessage={false} />);
    const messageContainer = container.querySelector('.flex-row');
    expect(messageContainer).toBeInTheDocument();
  });
});
