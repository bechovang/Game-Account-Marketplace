// Chat page container component
import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { apiClient } from '../services/rest/axiosInstance';
import { ChatBox } from '../components/chat/ChatBox';
import { ConversationList } from '../components/chat/ConversationList';
import type { Conversation } from '../components/chat/ConversationList';
import { Button } from '../components/ui/button';
import { AlertCircle } from 'lucide-react';

interface AccountDetails {
  sellerId: number;
  sellerEmail: string;
  sellerFullName: string;
}

export function ChatPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [selectedAccountId, setSelectedAccountId] = useState<number | null>(null);
  const [selectedConversation, setSelectedConversation] = useState<Conversation | null>(null);
  const [accountDetails, setAccountDetails] = useState<AccountDetails | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch conversations on mount
  useEffect(() => {
    const fetchConversations = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const response = await apiClient.get<Conversation[]>('/api/chat/conversations');
        setConversations(response);
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Failed to load conversations';
        setError(errorMessage);
        console.error('Failed to fetch conversations:', err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchConversations();
  }, []);

  // Handle selection based on URL and loaded conversations
  useEffect(() => {
    if (conversations.length === 0) return;

    const accountIdParam = searchParams.get('accountId');
    const accountIdToSelect = accountIdParam ? parseInt(accountIdParam) : null;

    const selectConversation = async () => {
      if (accountIdToSelect) {
        // Find conversation for this account
        const conversation = conversations.find((c) => c.accountId === accountIdToSelect);
        if (conversation) {
          setSelectedAccountId(accountIdToSelect);
          setSelectedConversation(conversation);
          setAccountDetails(null);
        } else {
          // No conversation exists yet for this account
          // Fetch account details to get seller info
          setSelectedAccountId(accountIdToSelect);
          setSelectedConversation(null);
          try {
            const accountResponse = await apiClient.get<AccountDetails>(`/api/accounts/${accountIdToSelect}/seller-info`);
            setAccountDetails(accountResponse);
          } catch (accountErr) {
            console.error('Failed to fetch account details:', accountErr);
            setError('Could not load account details');
          }
        }
      } else if (!selectedAccountId) {
        // Select first conversation if no specific account requested
        setSelectedAccountId(conversations[0].accountId);
        setSelectedConversation(conversations[0]);
        setAccountDetails(null);
      }
    };

    selectConversation();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [conversations, searchParams]);

  const handleSelectConversation = (accountId: number, otherUserId: number) => {
    setSelectedAccountId(accountId);
    const conversation = conversations.find((c) => c.accountId === accountId);
    setSelectedConversation(conversation || null);
    // Update URL so refresh and direct access work
    setSearchParams({ accountId: accountId.toString() });
  };

  const handleRetry = () => {
    // Refetch conversations
    window.location.reload();
  };

  if (isLoading) {
    return (
      <div className="container mx-auto p-4">
        <div className="flex items-center justify-center h-[calc(100vh-200px)]">
          <div className="text-center">
            <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-primary border-r-transparent"></div>
            <p className="mt-4 text-muted-foreground">Loading conversations...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto p-4">
        <div className="flex items-center justify-center h-[calc(100vh-200px)]">
          <div className="text-center">
            <AlertCircle className="h-12 w-12 text-destructive mx-auto mb-4" />
            <h2 className="text-lg font-semibold mb-2">Failed to Load Conversations</h2>
            <p className="text-muted-foreground mb-4">{error}</p>
            <Button onClick={handleRetry}>Try Again</Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-4">
      <div className="flex flex-col md:flex-row gap-4 h-[calc(100vh-200px)]">
        <ConversationList
          conversations={conversations}
          selectedAccountId={selectedAccountId}
          onSelectConversation={handleSelectConversation}
        />
        {selectedConversation && (
          <ChatBox
            accountId={selectedConversation.accountId}
            otherUserId={selectedConversation.otherUser.id}
            otherUserEmail={selectedConversation.otherUser.email}
          />
        )}
        {accountDetails && selectedAccountId && (
          <ChatBox
            accountId={selectedAccountId}
            otherUserId={accountDetails.sellerId}
            otherUserEmail={accountDetails.sellerEmail}
          />
        )}
        {!selectedConversation && !accountDetails && selectedAccountId && (
          <div className="flex-1 flex items-center justify-center bg-white border rounded-lg">
            <div className="text-center text-gray-400">
              <p>Loading chat...</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
