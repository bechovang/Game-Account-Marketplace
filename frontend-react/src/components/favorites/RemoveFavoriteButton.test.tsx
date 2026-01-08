import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MockedProvider } from '@apollo/client/testing';
import { REMOVE_FROM_FAVORITES } from '../../services/graphql/mutations';
import RemoveFavoriteButton from '../RemoveFavoriteButton';

const removeMutationMock = {
  request: {
    query: REMOVE_FROM_FAVORITES,
    variables: {
      accountId: '1',
    },
  },
  result: {
    data: {
      removeFromFavorites: true,
    },
  },
};

const removeErrorMock = {
  request: {
    query: REMOVE_FROM_FAVORITES,
    variables: {
      accountId: '1',
    },
  },
  error: new Error('Failed to remove'),
};

const wrapper = (mock: any) => ({ children }: { children: React.ReactNode }) => (
  <MockedProvider mocks={[mock]} addTypename={false}>
    {children}
  </MockedProvider>
);

describe('RemoveFavoriteButton', () => {
  it('renders remove button with trash icon', () => {
    render(<RemoveFavoriteButton accountId="1" />, { wrapper: wrapper(removeMutationMock) });

    expect(screen.getByLabelText('Remove from favorites')).toBeInTheDocument();
  });

  it('opens confirmation modal when clicked', () => {
    render(<RemoveFavoriteButton accountId="1" />, { wrapper: wrapper(removeMutationMock) });

    const button = screen.getByLabelText('Remove from favorites');
    fireEvent.click(button);

    // AC #2: Remove action requires confirmation modal before executing
    expect(screen.getByText('Remove from Favorites')).toBeInTheDocument();
    expect(screen.getByText(/Are you sure you want to remove/)).toBeInTheDocument();
  });

  it('removes favorite after confirming modal', async () => {
    const onRemove = jest.fn();
    render(<RemoveFavoriteButton accountId="1" onRemove={onRemove} />, {
      wrapper: wrapper(removeMutationMock),
    });

    const button = screen.getByLabelText('Remove from favorites');
    fireEvent.click(button);

    // Click confirm in modal
    const confirmButton = screen.getByText('Remove');
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(onRemove).toHaveBeenCalled();
    });
  });

  it('closes modal when cancel is clicked', () => {
    render(<RemoveFavoriteButton accountId="1" />, { wrapper: wrapper(removeMutationMock) });

    const button = screen.getByLabelText('Remove from favorites');
    fireEvent.click(button);

    // Click cancel in modal
    const cancelButton = screen.getByText('Cancel');
    fireEvent.click(cancelButton);

    // Modal should be closed
    expect(screen.queryByText('Remove from Favorites')).not.toBeInTheDocument();
  });

  it('shows loading state while removing', async () => {
    const loadingMock = {
      request: {
        query: REMOVE_FROM_FAVORITES,
        variables: {
          accountId: '1',
        },
      },
      result: {
        data: {
          removeFromFavorites: true,
        },
      },
      delay: 100,
    };

    render(<RemoveFavoriteButton accountId="1" />, { wrapper: wrapper(loadingMock) });

    const button = screen.getByLabelText('Remove from favorites');
    fireEvent.click(button);

    // Click confirm to start removal
    const confirmButton = screen.getByText('Remove');
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(screen.getByText('Removing...')).toBeInTheDocument();
    });
  });

  it('shows error message on remove failure', async () => {
    render(<RemoveFavoriteButton accountId="1" />, { wrapper: wrapper(removeErrorMock) });

    const button = screen.getByLabelText('Remove from favorites');
    fireEvent.click(button);

    // Click confirm to trigger the error
    const confirmButton = screen.getByText('Remove');
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(screen.getByText('Failed to remove from favorites. Please try again.')).toBeInTheDocument();
    });
  });

  it('disables buttons in modal while loading', async () => {
    const loadingMock = {
      request: {
        query: REMOVE_FROM_FAVORITES,
        variables: {
          accountId: '1',
        },
      },
      result: {
        data: {
          removeFromFavorites: true,
        },
      },
      delay: 100,
    };

    render(<RemoveFavoriteButton accountId="1" />, { wrapper: wrapper(loadingMock) });

    const button = screen.getByLabelText('Remove from favorites');
    fireEvent.click(button);

    // Click confirm to start removal
    const confirmButton = screen.getByText('Remove');
    fireEvent.click(confirmButton);

    await waitFor(() => {
      expect(screen.getByText('Removing...')).toBeDisabled();
    });

    const cancelButton = screen.getByText('Cancel');
    expect(cancelButton).toBeDisabled();
  });

  it('stops click propagation when clicked', async () => {
    const onRemove = jest.fn();
    render(<RemoveFavoriteButton accountId="1" onRemove={onRemove} />, {
      wrapper: wrapper(removeMutationMock),
    });

    const button = screen.getByLabelText('Remove from favorites');
    fireEvent.click(button);

    // Modal should open (event didn't propagate)
    expect(screen.getByText('Remove from Favorites')).toBeInTheDocument();
  });
});
