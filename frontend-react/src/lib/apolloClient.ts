import { ApolloClient, InMemoryCache, createHttpLink, from, ApolloLink } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';
import { onError } from '@apollo/client/link/error';
import toast from 'react-hot-toast';

// Get backend URL from environment or use default
const GRAPHQL_ENDPOINT = import.meta.env.VITE_GRAPHQL_URL || 'http://localhost:8080/graphql';

/**
 * HTTP Link for GraphQL requests
 */
const httpLink = createHttpLink({
  uri: GRAPHQL_ENDPOINT,
  credentials: 'include', // Send cookies for authentication
});

/**
 * Auth Link - Attaches JWT token to each request
 */
const authLink = setContext((_, { headers }) => {
  // Get JWT token from localStorage
  const token = localStorage.getItem('access_token');

  return {
    headers: {
      ...headers,
      authorization: token ? `Bearer ${token}` : '',
    },
  };
});

/**
 * Error Link - Handles GraphQL errors
 * - 401/403: Clears token and shows auth error
 * - Network errors: Shows toast notification
 */
const errorLink = onError(({ graphQLErrors, networkError, operation, forward }) => {
  // Handle GraphQL errors
  if (graphQLErrors) {
    for (const err of graphQLErrors) {
      const { message, locations, path, extensions } = err;

      console.error(
        `[GraphQL error]: Message: ${message}, Location: ${locations}, Path: ${path}`
      );

      // Handle authentication errors
      if (extensions?.code === 'UNAUTHENTICATED' || extensions?.code === 'FORBIDDEN') {
        // Clear invalid token
        localStorage.removeItem('access_token');

        // Show error toast
        toast.error('Authentication expired. Please log in again.');

        // Redirect to login
        window.location.href = '/login';
        return;
      }

      // Show other GraphQL errors
      toast.error(message || 'GraphQL error occurred');
    }
  }

  // Handle network errors
  if (networkError) {
    console.error(`[Network error]: ${networkError}`);

    // Check if it's a 401 Unauthorized
    if ('statusCode' in networkError && networkError.statusCode === 401) {
      localStorage.removeItem('access_token');
      toast.error('Authentication failed. Please log in again.');
      window.location.href = '/login';
      return;
    }

    // Show generic network error
    toast.error('Network error. Please check your connection.');
  }

  return forward(operation);
});

/**
 * Apollo Client instance
 * Configured with authLink, errorLink, and httpLink
 */
export const apolloClient = new ApolloClient({
  link: from([authLink, errorLink, httpLink]),
  cache: new InMemoryCache({
    typePolicies: {
      Query: {
        fields: {
          // Cache individual accounts by ID
          account: {
            read(_, { args, toReference }) {
              return toReference({
                __typename: 'Account',
                id: args?.id,
              });
            },
          },
          // Cache games by ID
          game: {
            read(_, { args, toReference }) {
              return toReference({
                __typename: 'Game',
                id: args?.id,
              });
            },
          },
          // Accounts query with smart caching
          accounts: {
            keyArgs: ['gameId', 'minPrice', 'maxPrice', 'minLevel', 'maxLevel', 'rank', 'status', 'q', 'sortBy', 'sortDirection'],
            merge(existing, incoming, { args }) {
              // For new search/filter (page 0), replace entirely
              if (!args?.page || args.page === 0) {
                return incoming;
              }
              
              // For pagination (page > 0), append to existing
              if (existing?.content && incoming?.content) {
                return {
                  ...incoming,
                  content: [...existing.content, ...incoming.content]
                };
              }
              
              return incoming;
            }
          },
          // Favorites query with similar caching
          favorites: {
            keyArgs: ['sortBy', 'sortDirection'],
            merge(existing, incoming, { args }) {
              if (!args?.page || args.page === 0) {
                return incoming;
              }
              
              if (existing?.content && incoming?.content) {
                return {
                  ...incoming,
                  content: [...existing.content, ...incoming.content]
                };
              }
              
              return incoming;
            }
          }
        },
      },
    },
  }),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: 'cache-and-network', // Always fetch from network while using cache
      errorPolicy: 'all',
    },
    query: {
      fetchPolicy: 'network-only', // Always fetch from network for direct queries
      errorPolicy: 'all',
    },
    mutation: {
      errorPolicy: 'all',
    },
  },
});

/**
 * Helper function to refetch active queries
 * Useful after mutations to update cache
 */
export const refetchActiveQueries = async () => {
  await apolloClient.refetchQueries({
    include: 'active',
  });
};
