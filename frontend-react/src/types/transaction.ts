export enum TransactionStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export interface Transaction {
  id: number;
  amount: number;
  status: TransactionStatus;
  createdAt: string;
  completedAt?: string;
  orderCode: string;
  accountId: number;
  accountTitle: string;
  accountPrice: number;
  buyerId: number;
  buyerName: string;
  sellerId: number;
  sellerName: string;
}

export interface PurchaseRequest {
  accountId: number;
}

export interface PurchaseResponse {
  transactionId: number;
  checkoutUrl: string;
  amount: number;
  orderCode: string;
}

export interface CredentialsResponse {
  username: string;
  password: string;
}

export interface TransactionFilters {
  status?: TransactionStatus;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}





