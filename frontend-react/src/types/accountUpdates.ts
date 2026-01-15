// Account update message types for real-time WebSocket updates
export type AccountUpdateMessageType = 'new_account_posted' | 'account_status_changed';

export interface AccountUpdateMessage {
  type: AccountUpdateMessageType;
  accountId: string;
  timestamp: string;
}

export interface NewAccountPostedMessage extends AccountUpdateMessage {
  type: 'new_account_posted';
  accountData: {
    id: string;
    title: string;
    price: number;
    game: string;
    rank: string;
    status: string;
    seller: {
      id: string;
      username: string;
    };
    createdAt: string;
  };
}

export interface AccountStatusChangedMessage extends AccountUpdateMessage {
  type: 'account_status_changed';
  oldStatus: string;
  newStatus: string;
}

export type AccountUpdateMessageUnion = NewAccountPostedMessage | AccountStatusChangedMessage;
