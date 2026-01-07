import { useQuery, useMutation, useLazyQuery } from '@apollo/client';
import toast from 'react-hot-toast';
import {
  GET_ACCOUNTS,
  GET_ACCOUNT,
  GET_GAMES,
  GET_GAME,
  GET_GAME_BY_SLUG,
  GET_MARKETPLACE_DATA,
} from '../services/graphql/queries';
import {
  CREATE_ACCOUNT,
  UPDATE_ACCOUNT,
  DELETE_ACCOUNT,
  APPROVE_ACCOUNT,
  REJECT_ACCOUNT,
} from '../services/graphql/mutations';
import type {
  AccountsQueryVariables,
  AccountQueryVariables,
  CreateAccountMutationVariables,
  UpdateAccountMutationVariables,
  DeleteAccountMutationVariables,
  ApproveAccountMutationVariables,
  RejectAccountMutationVariables,
} from '../types/graphql';

// ==================== Account Query Hooks ====================

/**
 * Hook for fetching accounts with filters and pagination
 * @param variables - Query variables (gameId, minPrice, maxPrice, status, page, limit)
 */
export const useAccounts = (variables?: AccountsQueryVariables) => {
  const {
    data,
    loading,
    error,
    refetch,
    fetchMore,
  } = useQuery(GET_ACCOUNTS, {
    variables,
    notifyOnNetworkStatusChange: true,
  });

  return {
    accounts: data?.accounts.content || [],
    pagination: data?.accounts
      ? {
          totalElements: data.accounts.totalElements,
          totalPages: data.accounts.totalPages,
          currentPage: data.accounts.currentPage,
          pageSize: data.accounts.pageSize,
        }
      : {
          totalElements: 0,
          totalPages: 0,
          currentPage: 0,
          pageSize: 20,
        },
    loading,
    error,
    refetch,
    fetchMore: (page: number) =>
      fetchMore({
        variables: { ...variables, page },
        updateQuery: (prev, { fetchMoreResult }) => {
          if (!fetchMoreResult) return prev;
          return fetchMoreResult;
        },
      }),
  };
};

/**
 * Lazy hook for fetching accounts (manually triggered)
 * Useful for search/filter functionality
 */
export const useAccountsLazy = () => {
  const [getAccounts, { data, loading, error, refetch }] = useLazyQuery(GET_ACCOUNTS, {
    notifyOnNetworkStatusChange: true,
  });

  return {
    getAccounts,
    accounts: data?.accounts.content || [],
    pagination: data?.accounts
      ? {
          totalElements: data.accounts.totalElements,
          totalPages: data.accounts.totalPages,
          currentPage: data.accounts.currentPage,
          pageSize: data.accounts.pageSize,
        }
      : undefined,
    loading,
    error,
    refetch,
  };
};

/**
 * Hook for fetching a single account by ID
 * @param id - Account ID
 */
export const useAccount = (id: string) => {
  const { data, loading, error, refetch } = useQuery(GET_ACCOUNT, {
    variables: { id },
    skip: !id, // Skip query if id is not provided
  });

  return {
    account: data?.account,
    loading,
    error,
    refetch,
  };
};

// ==================== Game Query Hooks ====================

/**
 * Hook for fetching all games
 */
export const useGames = () => {
  const { data, loading, error } = useQuery(GET_GAMES);

  return {
    games: data?.games || [],
    loading,
    error,
  };
};

/**
 * Hook for fetching a single game by ID
 * @param id - Game ID
 */
export const useGame = (id: string) => {
  const { data, loading, error } = useQuery(GET_GAME, {
    variables: { id },
    skip: !id,
  });

  return {
    game: data?.game,
    loading,
    error,
  };
};

/**
 * Hook for fetching a game by slug
 * @param slug - Game slug
 */
export const useGameBySlug = (slug: string) => {
  const { data, loading, error } = useQuery(GET_GAME_BY_SLUG, {
    variables: { slug },
    skip: !slug,
  });

  return {
    game: data?.gameBySlug,
    loading,
    error,
  };
};

/**
 * Hook for fetching marketplace data (accounts + games)
 * @param variables - Query variables for accounts
 */
export const useMarketplaceData = (variables?: AccountsQueryVariables) => {
  const { data, loading, error, refetch } = useQuery(GET_MARKETPLACE_DATA, {
    variables,
    notifyOnNetworkStatusChange: true,
  });

  return {
    accounts: data?.accounts.content || [],
    games: data?.games || [],
    pagination: data?.accounts
      ? {
          totalElements: data.accounts.totalElements,
          totalPages: data.accounts.totalPages,
          currentPage: data.accounts.currentPage,
          pageSize: data.accounts.pageSize,
        }
      : undefined,
    loading,
    error,
    refetch,
  };
};

// ==================== Account Mutation Hooks ====================

/**
 * Hook for creating a new account listing
 */
export const useCreateAccount = () => {
  const [createAccountMutation, { loading, error }] = useMutation(CREATE_ACCOUNT, {
    // Update cache after successful creation
    update: (cache, { data }) => {
      if (data?.createAccount) {
        toast.success('Account listing created successfully!');
      }
    },
    onError: (err) => {
      console.error('Failed to create account:', err);
      toast.error(err.message || 'Failed to create account listing');
    },
  });

  const createAccount = async (variables: CreateAccountMutationVariables) => {
    const result = await createAccountMutation({ variables });
    return result.data?.createAccount;
  };

  return {
    createAccount,
    loading,
    error,
  };
};

/**
 * Hook for updating an existing account listing
 */
export const useUpdateAccount = () => {
  const [updateAccountMutation, { loading, error }] = useMutation(UPDATE_ACCOUNT, {
    // Update cache after successful update
    update: (cache, { data }) => {
      if (data?.updateAccount) {
        toast.success('Account listing updated successfully!');
      }
    },
    onError: (err) => {
      console.error('Failed to update account:', err);
      toast.error(err.message || 'Failed to update account listing');
    },
  });

  const updateAccount = async (variables: UpdateAccountMutationVariables) => {
    const result = await updateAccountMutation({ variables });
    return result.data?.updateAccount;
  };

  return {
    updateAccount,
    loading,
    error,
  };
};

/**
 * Hook for deleting an account listing
 */
export const useDeleteAccount = () => {
  const [deleteAccountMutation, { loading, error }] = useMutation(DELETE_ACCOUNT, {
    // Update cache after successful deletion
    update: (cache, { data }) => {
      if (data?.deleteAccount) {
        toast.success('Account listing deleted successfully!');
      }
    },
    onError: (err) => {
      console.error('Failed to delete account:', err);
      toast.error(err.message || 'Failed to delete account listing');
    },
  });

  const deleteAccount = async (variables: DeleteAccountMutationVariables) => {
    const result = await deleteAccountMutation({ variables });
    return result.data?.deleteAccount;
  };

  return {
    deleteAccount,
    loading,
    error,
  };
};

// ==================== Admin Mutation Hooks ====================

/**
 * Hook for approving a pending account listing (ADMIN only)
 */
export const useApproveAccount = () => {
  const [approveAccountMutation, { loading, error }] = useMutation(APPROVE_ACCOUNT, {
    update: (cache, { data }) => {
      if (data?.approveAccount) {
        toast.success('Account listing approved successfully!');
      }
    },
    onError: (err) => {
      console.error('Failed to approve account:', err);
      toast.error(err.message || 'Failed to approve account listing');
    },
  });

  const approveAccount = async (variables: ApproveAccountMutationVariables) => {
    const result = await approveAccountMutation({ variables });
    return result.data?.approveAccount;
  };

  return {
    approveAccount,
    loading,
    error,
  };
};

/**
 * Hook for rejecting a pending account listing (ADMIN only)
 */
export const useRejectAccount = () => {
  const [rejectAccountMutation, { loading, error }] = useMutation(REJECT_ACCOUNT, {
    update: (cache, { data }) => {
      if (data?.rejectAccount) {
        toast.success('Account listing rejected successfully!');
      }
    },
    onError: (err) => {
      console.error('Failed to reject account:', err);
      toast.error(err.message || 'Failed to reject account listing');
    },
  });

  const rejectAccount = async (variables: RejectAccountMutationVariables) => {
    const result = await rejectAccountMutation({ variables });
    return result.data?.rejectAccount;
  };

  return {
    rejectAccount,
    loading,
    error,
  };
};
