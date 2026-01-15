// Test file for ChatBox component
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { ChatBox } from './ChatBox';
import { useAuth } from '../../contexts/AuthContext';

// Mock useAuth
vi.mock('../../contexts/AuthContext', () => ({
  useAuth: vi.fn(),
}));

// Mock useChat hook
vi.mock('../../hooks/useChat', () => ({
  useChat: vi.fn(),
}));

// Mock MessageBubble and TypingIndicator
vi.mock('./MessageBubble', () => ({
  MessageBubble: ({ message, isOwnMessage }: { message: any; isOwnMessage: boolean }) => (
    <div data-testid={`message-${message.id}`} data-own={isOwnMessage}>
      {message.content}
    </div>
  ),
}));

vi.mock('./TypingIndicator', () => ({
  TypingIndicator: ({ users }: { users: Map<number, string> }) => (
    <div data-testid="typing-indicator">{users.size} users typing</div>
  ),
}));

import { useChat } from '../../hooks/useChat';

describe('ChatBox', () => {
  const mockUser = {
    id: 1,
    email: 'currentuser@example.com',
    fullName: 'Current User',
    role: 'BUYER' as const,
    avatar: null,
  };

  const mockUseChatReturn = {
    messages: [
      {
        id: 'msg-1',
        accountId: 123,
        senderId: 2,
        senderEmail: 'other@example.com',
        receiverId: 1,
        content: 'Hello from other user',
        isRead: false,
        createdAt: '2024-01-14T10:00:00Z',
      },
      {
        id: 'msg-2',
        accountId: 123,
        senderId: 1,
        senderEmail: 'currentuser@example.com',
        receiverId: 2,
        content: 'Hello from me',
        isRead: false,
        createdAt: '2024-01-14T10:01:00Z',
      },
    ],
    typingUsers: new Map([[2, 'other@example.com']]),
    sendMessage: vi.fn(),
    sendTypingIndicator: vi.fn(),
    markAsRead: vi.fn(),
    isConnected: true,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (useAuth as any).mockReturnValue({ user: mockUser });
    (useChat as any).mockReturnValue(mockUseChatReturn);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders chat header with other user email', () => {
    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    expect(screen.getByText('other@example.com')).toBeInTheDocument();
  });

  it('renders all messages using MessageBubble component', () => {
    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    expect(screen.getByTestId('message-msg-1')).toHaveTextContent('Hello from other user');
    expect(screen.getByTestId('message-msg-2')).toHaveTextContent('Hello from me');
  });

  it('shows empty state when there are no messages', () => {
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      messages: [],
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    expect(screen.getByText('No messages yet. Start the conversation!')).toBeInTheDocument();
  });

  it('shows typing indicator when other user is typing', () => {
    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    expect(screen.getByTestId('typing-indicator')).toBeInTheDocument();
    expect(screen.getByTestId('typing-indicator')).toHaveTextContent('1 users typing');
  });

  it('does not show typing indicator when other user is not typing', () => {
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      typingUsers: new Map(),
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    expect(screen.queryByTestId('typing-indicator')).not.toBeInTheDocument();
  });

  it('shows disconnected status when WebSocket is not connected', () => {
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      isConnected: false,
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    expect(screen.getByText('Disconnected')).toBeInTheDocument();
  });

  it('disables input and send button when disconnected', () => {
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      isConnected: false,
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const input = screen.getByPlaceholderText('Type a message...');
    const sendButton = screen.getByRole('button', { name: '' }); // Send icon button

    expect(input).toBeDisabled();
    expect(sendButton).toBeDisabled();
  });

  it('enables input and send button when connected', () => {
    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const input = screen.getByPlaceholderText('Type a message...');
    const sendButton = screen.getByRole('button', { name: '' });

    expect(input).not.toBeDisabled();
    expect(sendButton).not.toBeDisabled();
  });

  it('sends message when Send button is clicked', async () => {
    const mockSendMessage = vi.fn();
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      sendMessage: mockSendMessage,
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const input = screen.getByPlaceholderText('Type a message...');
    const sendButton = screen.getByRole('button', { name: '' });

    fireEvent.change(input, { target: { value: 'Test message' } });
    fireEvent.click(sendButton);

    expect(mockSendMessage).toHaveBeenCalledWith('Test message');
    expect(input).toHaveValue('');
  });

  it('sends message when Enter key is pressed', () => {
    const mockSendMessage = vi.fn();
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      sendMessage: mockSendMessage,
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const input = screen.getByPlaceholderText('Type a message...');

    fireEvent.change(input, { target: { value: 'Test message' } });
    fireEvent.keyDown(input, { key: 'Enter' });

    expect(mockSendMessage).toHaveBeenCalledWith('Test message');
  });

  it('does not send message when Shift+Enter is pressed', () => {
    const mockSendMessage = vi.fn();
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      sendMessage: mockSendMessage,
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const input = screen.getByPlaceholderText('Type a message...');

    fireEvent.change(input, { target: { value: 'Test message' } });
    fireEvent.keyDown(input, { key: 'Enter', shiftKey: true });

    expect(mockSendMessage).not.toHaveBeenCalled();
  });

  it('does not send empty or whitespace-only messages', () => {
    const mockSendMessage = vi.fn();
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      sendMessage: mockSendMessage,
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const input = screen.getByPlaceholderText('Type a message...');
    const sendButton = screen.getByRole('button', { name: '' });

    // Test empty message
    fireEvent.click(sendButton);
    expect(mockSendMessage).not.toHaveBeenCalled();

    // Test whitespace-only message
    fireEvent.change(input, { target: { value: '   ' } });
    fireEvent.click(sendButton);
    expect(mockSendMessage).not.toHaveBeenCalled();
  });

  it('sends typing indicator when user types', () => {
    const mockSendTypingIndicator = vi.fn();
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      sendTypingIndicator: mockSendTypingIndicator,
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const input = screen.getByPlaceholderText('Type a message...');

    fireEvent.change(input, { target: { value: 'Test' } });

    expect(mockSendTypingIndicator).toHaveBeenCalledWith(true);
  });

  it('sends typing indicator false when message is sent', () => {
    const mockSendTypingIndicator = vi.fn();
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      sendTypingIndicator: mockSendTypingIndicator,
      sendMessage: vi.fn(),
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const input = screen.getByPlaceholderText('Type a message...');
    const sendButton = screen.getByRole('button', { name: '' });

    fireEvent.change(input, { target: { value: 'Test message' } });
    fireEvent.click(sendButton);

    expect(mockSendTypingIndicator).toHaveBeenCalledWith(false);
  });

  it('marks messages as read on mount', () => {
    const mockMarkAsRead = vi.fn();
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      markAsRead: mockMarkAsRead,
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    expect(mockMarkAsRead).toHaveBeenCalledWith(2);
  });

  it('does not mark as read when user is not available', () => {
    const mockMarkAsRead = vi.fn();
    (useAuth as any).mockReturnValue({ user: null });
    (useChat as any).mockReturnValue({
      ...mockUseChatReturn,
      markAsRead: mockMarkAsRead,
    });

    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    expect(mockMarkAsRead).not.toHaveBeenCalled();
  });

  it('passes correct isOwnMessage prop based on senderId', () => {
    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const msg1 = screen.getByTestId('message-msg-1');
    const msg2 = screen.getByTestId('message-msg-2');

    // Message from other user (senderId: 2) should not be own message
    expect(msg1).toHaveAttribute('data-own', 'false');

    // Message from current user (senderId: 1) should be own message
    expect(msg2).toHaveAttribute('data-own', 'true');
  });

  it('disables send button when input is empty', () => {
    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const sendButton = screen.getByRole('button', { name: '' });

    expect(sendButton).toBeDisabled();
  });

  it('enables send button when input has text', () => {
    render(<ChatBox accountId={123} otherUserId={2} otherUserEmail="other@example.com" />);

    const input = screen.getByPlaceholderText('Type a message...');
    const sendButton = screen.getByRole('button', { name: '' });

    fireEvent.change(input, { target: { value: 'Test' } });

    expect(sendButton).not.toBeDisabled();
  });
});
