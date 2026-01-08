import React, { useCallback, useState } from 'react';
import { useMutation } from '@apollo/client';
import { REMOVE_FROM_FAVORITES } from '../../services/graphql/mutations';
import RemoveFavoriteModal from './RemoveFavoriteModal';

interface RemoveFavoriteButtonProps {
  accountId: string;
  onRemove?: () => void;
}

/**
 * RemoveFavoriteButton - Trash icon button that shows confirmation modal before removing
 * Follows AC #2: Remove action requires confirmation modal before executing
 */
const RemoveFavoriteButton: React.FC<RemoveFavoriteButtonProps> = ({ accountId, onRemove }) => {
  const [showError, setShowError] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [removeFromFavorites, { loading }] = useMutation(REMOVE_FROM_FAVORITES, {
    optimisticResponse: (variables) => ({
      removeFromFavorites: true,
    }),
    update: (cache, { data }) => {
      if (data?.removeFromFavorites) {
        // Remove from favorites list
        cache.evict({
          id: cache.identify({
            __typename: 'Account',
            id: variables.accountId,
          }),
          fieldName: 'isFavorited',
        });

        // Evict from cached favorites list
        cache.modify({
          fields: {
            favorites: (existing, { readField }) => {
              return {
                ...existing,
                content: existing?.content?.filter((account: any) => {
                  const accountRef = cache.identify({
                    __typename: 'Account',
                    id: account.id || readField('id', account),
                  });
                  return accountRef !== cache.identify({
                    __typename: 'Account',
                    id: variables.accountId,
                  });
                }),
              };
            },
          },
        });
      }
    },
    onError: (error) => {
      // Rollback happens automatically
      setShowError(true);
      setTimeout(() => setShowError(false), 3000);
    },
    onCompleted: (data) => {
      if (data?.removeFromFavorites && onRemove) {
        onRemove();
      }
    },
  });

  const handleRemoveClick = useCallback(() => {
    setIsModalOpen(true);
  }, []);

  const handleConfirmRemove = useCallback(() => {
    removeFromFavorites({
      variables: { accountId },
    });
    setIsModalOpen(false);
  }, [accountId, removeFromFavorites]);

  const handleCancelRemove = useCallback(() => {
    setIsModalOpen(false);
  }, []);

  return (
    <>
      <button
        onClick={(e) => {
          e.stopPropagation();
          handleRemoveClick();
        }}
        disabled={loading}
        className="p-2 bg-white rounded-full shadow-md hover:bg-red-50 transition-colors"
        aria-label="Remove from favorites"
      >
        {loading ? (
          <svg
            className="w-5 h-5 text-gray-400 animate-spin"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
            />
          </svg>
        ) : (
          <svg
            className="w-5 h-5 text-red-500"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
            />
          </svg>
        )}
      </button>

      {/* Error toast */}
      {showError && (
        <div className="fixed bottom-4 right-4 bg-red-500 text-white px-4 py-2 rounded-lg shadow-lg z-50">
          Failed to remove from favorites. Please try again.
        </div>
      )}

      {/* Confirmation Modal - AC #2 */}
      <RemoveFavoriteModal
        isOpen={isModalOpen}
        isLoading={loading}
        onConfirm={handleConfirmRemove}
        onCancel={handleCancelRemove}
      />
    </>
  );
};

export default React.memo(RemoveFavoriteButton);
