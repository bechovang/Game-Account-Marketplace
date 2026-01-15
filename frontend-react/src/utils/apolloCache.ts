// Apollo Client cache manipulation utilities for real-time updates
import { ApolloClient } from '@apollo/client';
import type { GameAccount } from '../types/graphql';

/**
 * Update account in Apollo Client cache using cache.modify
 * This updates existing cached data without refetching
 */
export function updateAccountInCache(
  client: ApolloClient<any>,
  accountId: string,
  updates: Partial<GameAccount>
): void {
  client.cache.modify({
    fields: {
      gameAccounts(existingAccounts = { data: [] }, { readField }) {
        return {
          data: existingAccounts.data.map((account: any) => {
            if (readField('id', account) === accountId) {
              return { ...account, ...updates };
            }
            return account;
          }),
        };
      },
    },
  });
}

/**
 * Add new account to Apollo Client cache
 * This prepends the new account to the cached list
 */
export function addAccountToCache(
  client: ApolloClient<any>,
  newAccount: GameAccount
): void {
  client.cache.modify({
    fields: {
      gameAccounts(existingAccounts = { data: [] }) {
        return { data: [newAccount, ...existingAccounts.data] };
      },
    },
  });
}

/**
 * Remove account from Apollo Client cache
 * This evicts the account from cache completely
 */
export function removeAccountFromCache(
  client: ApolloClient<any>,
  accountId: string
): void {
  client.evict({
    id: client.identify({ id: accountId, __typename: 'GameAccount' }),
  });
  client.gc();
}

/**
 * Refetch all active queries related to accounts
 * Use this after cache updates to ensure consistency
 */
export function refetchAccountQueries(client: ApolloClient<any>): void {
  client.refetchQueries({
    include: ['active'],
  });
}
