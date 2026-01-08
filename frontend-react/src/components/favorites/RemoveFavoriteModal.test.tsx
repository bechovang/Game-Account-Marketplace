import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import RemoveFavoriteModal from '../RemoveFavoriteModal';

describe('RemoveFavoriteModal', () => {
  const mockProps = {
    isOpen: true,
    isLoading: false,
    onConfirm: jest.fn(),
    onCancel: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders confirmation modal when open', () => {
    render(<RemoveFavoriteModal {...mockProps} />);

    expect(screen.getByText('Remove from Favorites')).toBeInTheDocument();
    expect(screen.getByText(/Are you sure you want to remove/)).toBeInTheDocument();
  });

  it('does not render modal when closed', () => {
    render(<RemoveFavoriteModal {...mockProps} isOpen={false} />);

    expect(screen.queryByText('Remove from Favorites')).not.toBeInTheDocument();
  });

  it('calls onConfirm when Confirm button is clicked', () => {
    render(<RemoveFavoriteModal {...mockProps} />);

    const confirmButton = screen.getByText('Remove');
    fireEvent.click(confirmButton);

    expect(mockProps.onConfirm).toHaveBeenCalledTimes(1);
  });

  it('calls onCancel when Cancel button is clicked', () => {
    render(<RemoveFavoriteModal {...mockProps} />);

    const cancelButton = screen.getByText('Cancel');
    fireEvent.click(cancelButton);

    expect(mockProps.onCancel).toHaveBeenCalledTimes(1);
  });

  it('disables buttons while loading', () => {
    render(<RemoveFavoriteModal {...mockProps} isLoading={true} />);

    const confirmButton = screen.getByText('Removing...');
    const cancelButton = screen.getByText('Cancel');

    expect(confirmButton).toBeDisabled();
    expect(cancelButton).toBeDisabled();
  });

  it('shows "Remove" text when not loading', () => {
    render(<RemoveFavoriteModal {...mockProps} isLoading={false} />);

    expect(screen.getByText('Remove')).toBeInTheDocument();
  });

  it('shows "Removing..." text when loading', () => {
    render(<RemoveFavoriteModal {...mockProps} isLoading={true} />);

    expect(screen.getByText('Removing...')).toBeInTheDocument();
  });

  it('has warning icon with proper styling', () => {
    render(<RemoveFavoriteModal {...mockProps} />);

    const warningIcon = document.querySelector('.bg-red-100');
    expect(warningIcon).toBeInTheDocument();
  });

  it('has close button behavior via backdrop click', () => {
    render(<RemoveFavoriteModal {...mockProps} />);

    const backdrop = document.querySelector('.bg-black.bg-opacity-25');
    if (backdrop) {
      fireEvent.click(backdrop);
      // The modal should close (onCancel is expected to be called)
      expect(mockProps.onCancel).toHaveBeenCalled();
    }
  });
});
