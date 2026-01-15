// Test file for ConversationList component
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { ConversationList, Conversation } from './ConversationList';

// Mock date-fns
vi.mock('date-fns', () => ({
  formatDistanceToNow: vi.fn(() => '5 minutes ago'),
}));

describe('ConversationList', () => {
  const mockConversations: Conversation[] = [
    {
      accountId: 1,
      accountTitle: 'Level 100 Warrior Account',
      otherUser: {
        id: 2,
        email: 'seller@example.com',
      },
      lastMessage: {
        content: 'Is this account still available?',
        createdAt: '2024-01-14T10:00:00Z',
      },
      unreadCount: 2,
    },
    {
      accountId: 2,
      accountTitle: 'Rare Skins Account',
      otherUser: {
        id: 3,
        email: 'buyer@example.com',
      },
      lastMessage: {
        content: 'Thanks for the purchase!',
        createdAt: '2024-01-14T09:00:00Z',
      },
      unreadCount: 0,
    },
  ];

  it('renders conversation list with conversations', () => {
    render(
      <ConversationList
        conversations={mockConversations}
        selectedAccountId={null}
        onSelectConversation={vi.fn()}
      />
    );

    expect(screen.getByText('Messages')).toBeInTheDocument();
    expect(screen.getByText('seller@example.com')).toBeInTheDocument();
    expect(screen.getByText('buyer@example.com')).toBeInTheDocument();
  });

  it('renders empty state when no conversations', () => {
    render(
      <ConversationList
        conversations={[]}
        selectedAccountId={null}
        onSelectConversation={vi.fn()}
      />
    );

    expect(screen.getByText('No conversations yet')).toBeInTheDocument();
  });

  it('displays unread count badge', () => {
    render(
      <ConversationList
        conversations={mockConversations}
        selectedAccountId={null}
        onSelectConversation={vi.fn()}
      />
    );

    expect(screen.getByText('2')).toBeInTheDocument();
  });

  it('highlights selected conversation', () => {
    render(
      <ConversationList
        conversations={mockConversations}
        selectedAccountId={1}
        onSelectConversation={vi.fn()}
      />
    );

    const selectedConversation = screen.getByText('seller@example.com').closest('.bg-blue-50');
    expect(selectedConversation).toBeInTheDocument();
  });

  it('calls onSelectConversation when conversation is clicked', () => {
    const onSelectMock = vi.fn();
    render(
      <ConversationList
        conversations={mockConversations}
        selectedAccountId={null}
        onSelectConversation={onSelectMock}
      />
    );

    fireEvent.click(screen.getByText('seller@example.com'));
    expect(onSelectMock).toHaveBeenCalledWith(1, 2);
  });

  it('truncates long message previews', () => {
    const longMessageConversation: Conversation[] = [
      {
        accountId: 1,
        accountTitle: 'Test Account',
        otherUser: { id: 2, email: 'test@example.com' },
        lastMessage: {
          content: 'This is a very long message that should be truncated because it exceeds fifty characters limit',
          createdAt: '2024-01-14T10:00:00Z',
        },
        unreadCount: 0,
      },
    ];

    render(
      <ConversationList
        conversations={longMessageConversation}
        selectedAccountId={null}
        onSelectConversation={vi.fn()}
      />
    );

    expect(screen.getByText(/This is a very long message that should be truncate...\/)).toBeInTheDocument();
  });
});
