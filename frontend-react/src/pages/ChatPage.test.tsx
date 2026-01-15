// Test file for ChatPage component
import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { ChatPage } from './ChatPage';
import { apiClient } from '../services/rest/axiosInstance';

// Mock apiClient
vi.mock('../services/rest/axiosInstance', () => ({
  apiClient: vi.fn(),
}));

// Mock child components
vi.mock('../components/chat/ConversationList', () => ({
  ConversationList: ({ conversations, selectedAccountId, onSelectConversation }: any) => (
    <div data-testid="conversation-list">
      <div data-testid="conversation-count">{conversations.length}</div>
      <div data-testid="selected-account">{selectedAccountId || 'none'}</div>
      <button
        onClick={() => onSelectConversation(conversations[0]?.accountId, conversations[0]?.otherUser.id)}
        data-testid="select-first-conversation"
      >
        Select First
      </button>
    </div>
  ),
}));

vi.mock('../components/chat/ChatBox', () => ({
  ChatBox: ({ accountId, otherUserId, otherUserEmail }: any) => (
    <div data-testid="chat-box">
      <div data-testid="chat-account-id">{accountId}</div>
      <div data-testid="chat-other-user">{otherUserEmail}</div>
    </div>
  ),
}));

describe('ChatPage', () => {
  const mockConversations = [
    {
      accountId: 123,
      accountTitle: 'Level 50 Account',
      otherUser: {
        id: 2,
        email: 'seller@example.com',
        avatar: null,
      },
      lastMessage: {
        content: 'Is this account still available?',
        createdAt: '2024-01-14T10:00:00Z',
      },
      unreadCount: 2,
    },
    {
      accountId: 456,
      accountTitle: 'Rank S Account',
      otherUser: {
        id: 3,
        email: 'buyer@example.com',
        avatar: null,
      },
      lastMessage: {
        content: 'I will buy this account',
        createdAt: '2024-01-14T09:00:00Z',
      },
      unreadCount: 0,
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    // Mock window.location.reload
    Object.defineProperty(window, 'location', {
      writable: true,
      value: { reload: vi.fn() },
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('shows loading state on initial render', () => {
    (apiClient as any).mockImplementation(() => new Promise(() => {})); // Never resolves

    render(<ChatPage />);

    expect(screen.getByText('Loading conversations...')).toBeInTheDocument();
    expect(screen.queryByTestId('conversation-list')).not.toBeInTheDocument();
  });

  it('renders conversation list after successful fetch', async () => {
    (apiClient as any).mockResolvedValue(mockConversations);

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByTestId('conversation-list')).toBeInTheDocument();
    });

    expect(screen.getByTestId('conversation-count')).toHaveTextContent('2');
  });

  it('automatically selects first conversation on load', async () => {
    (apiClient as any).mockResolvedValue(mockConversations);

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByTestId('conversation-list')).toBeInTheDocument();
    });

    expect(screen.getByTestId('selected-account')).toHaveTextContent('123');
    expect(screen.getByTestId('chat-box')).toBeInTheDocument();
  });

  it('renders ChatBox with correct props when conversation selected', async () => {
    (apiClient as any).mockResolvedValue(mockConversations);

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByTestId('chat-box')).toBeInTheDocument();
    });

    expect(screen.getByTestId('chat-account-id')).toHaveTextContent('123');
    expect(screen.getByTestId('chat-other-user')).toHaveTextContent('seller@example.com');
  });

  it('handles conversation selection', async () => {
    (apiClient as any).mockResolvedValue(mockConversations);

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByTestId('conversation-list')).toBeInTheDocument();
    });

    const selectButton = screen.getByTestId('select-first-conversation');
    fireEvent.click(selectButton);

    // Verify the selection was handled
    expect(screen.getByTestId('selected-account')).toHaveTextContent('123');
  });

  it('shows error state when API call fails', async () => {
    const mockError = new Error('Network error');
    (apiClient as any).mockRejectedValue(mockError);

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByText('Failed to Load Conversations')).toBeInTheDocument();
    });

    expect(screen.getByText('Network error')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Try Again' })).toBeInTheDocument();
  });

  it('reloads page when retry button is clicked', async () => {
    const mockError = new Error('Failed to fetch');
    (apiClient as any).mockRejectedValue(mockError);

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Try Again' })).toBeInTheDocument();
    });

    const retryButton = screen.getByRole('button', { name: 'Try Again' });
    fireEvent.click(retryButton);

    expect(window.location.reload).toHaveBeenCalledTimes(1);
  });

  it('shows loading spinner during fetch', () => {
    (apiClient as any).mockImplementation(() => new Promise(() => {}));

    render(<ChatPage />);

    const spinner = document.querySelector('.animate-spin');
    expect(spinner).toBeInTheDocument();
    expect(screen.getByText('Loading conversations...')).toBeInTheDocument();
  });

  it('fetches conversations only once on mount', async () => {
    (apiClient as any).mockResolvedValue(mockConversations);

    render(<ChatPage />);

    await waitFor(() => {
      expect(apiClient).toHaveBeenCalledTimes(1);
    });
  });

  it('handles empty conversations list', async () => {
    (apiClient as any).mockResolvedValue([]);

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByTestId('conversation-list')).toBeInTheDocument();
    });

    expect(screen.getByTestId('conversation-count')).toHaveTextContent('0');
    expect(screen.queryByTestId('chat-box')).not.toBeInTheDocument();
  });

  it('does not select conversation when list is empty', async () => {
    (apiClient as any).mockResolvedValue([]);

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByTestId('conversation-list')).toBeInTheDocument();
    });

    expect(screen.getByTestId('selected-account')).toHaveTextContent('none');
  });

  it('displays error message for different error types', async () => {
    (apiClient as any).mockRejectedValue('Unauthorized');

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByText('Failed to Load Conversations')).toBeInTheDocument();
    });

    expect(screen.getByText('Unauthorized')).toBeInTheDocument();
  });

  it('renders with correct layout structure', async () => {
    (apiClient as any).mockResolvedValue(mockConversations);

    const { container } = render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByTestId('conversation-list')).toBeInTheDocument();
    });

    const mainContainer = container.querySelector('.container');
    expect(mainContainer).toBeInTheDocument();

    const flexContainer = container.querySelector('.flex');
    expect(flexContainer).toBeInTheDocument();
  });

  it('handles conversation with null lastMessage', async () => {
    const conversationsWithoutLastMessage = [
      {
        ...mockConversations[0],
        lastMessage: undefined,
      },
    ];

    (apiClient as any).mockResolvedValue(conversationsWithoutLastMessage);

    render(<ChatPage />);

    await waitFor(() => {
      expect(screen.getByTestId('conversation-list')).toBeInTheDocument();
    });

    // Should still render without crashing
    expect(screen.getByTestId('conversation-count')).toHaveTextContent('1');
  });

  it('shows AlertCircle icon in error state', async () => {
    (apiClient as any).mockRejectedValue(new Error('Test error'));

    render(<ChatPage />);

    await waitFor(() => {
      const alertIcon = document.querySelector('.lucide-alert-circle');
      expect(alertIcon).toBeInTheDocument();
    });
  });
});

// Import fireEvent for tests that need it
import { fireEvent } from '@testing-library/react';
