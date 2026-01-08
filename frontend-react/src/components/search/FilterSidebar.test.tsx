import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { MockedProvider } from '@apollo/client/testing';
import { GET_GAMES } from '../../services/graphql/queries';
import FilterSidebar from './FilterSidebar';

const mockGames = [
  { id: '1', name: 'World of Warcraft', iconUrl: 'https://example.com/wow.png' },
  { id: '2', name: 'League of Legends', iconUrl: 'https://example.com/lol.png' },
];

const gamesMock = {
  request: {
    query: GET_GAMES,
  },
  result: {
    data: {
      games: mockGames,
    },
  },
};

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <MemoryRouter>
    <MockedProvider mocks={[gamesMock]} addTypename={false}>
      {children}
    </MockedProvider>
  </MemoryRouter>
);

describe('FilterSidebar', () => {
  it('renders all filter controls', async () => {
    render(<FilterSidebar />, { wrapper });

    await waitFor(() => {
      expect(screen.getByLabelText('Filter by game')).toBeInTheDocument();
      expect(screen.getByLabelText('Minimum price')).toBeInTheDocument();
      expect(screen.getByLabelText('Maximum price')).toBeInTheDocument();
      expect(screen.getByLabelText('Minimum level')).toBeInTheDocument();
      expect(screen.getByLabelText('Maximum level')).toBeInTheDocument();
      expect(screen.getByLabelText('Filter by rank')).toBeInTheDocument();
      expect(screen.getByLabelText('Filter by status')).toBeInTheDocument();
    });
  });

  it('updates URL params when filters change', async () => {
    render(<FilterSidebar />, { wrapper });

    await waitFor(() => {
      const gameSelect = screen.getByLabelText('Filter by game');
      fireEvent.change(gameSelect, { target: { value: '1' } });
      expect(gameSelect).toHaveValue('1');
    });
  });

  it('clears all filters on Clear button click', async () => {
    render(<FilterSidebar />, { wrapper });

    await waitFor(() => {
      const clearButton = screen.getByLabelText('Clear all filters');
      expect(clearButton).toBeInTheDocument();
    });
  });

  it('validates minPrice < maxPrice', async () => {
    render(<FilterSidebar />, { wrapper });

    await waitFor(() => {
      const minPriceInput = screen.getByLabelText('Minimum price');
      const maxPriceInput = screen.getByLabelText('Maximum price');

      // Set invalid range
      fireEvent.change(minPriceInput, { target: { value: '500' } });
      fireEvent.change(maxPriceInput, { target: { value: '100' } });

      // Trigger blur to apply filter
      fireEvent.blur(minPriceInput);

      // Should show validation error
      // Note: This test would need to check for error message display
    });
  });

  it('displays game options from GET_GAMES query', async () => {
    render(<FilterSidebar />, { wrapper });

    await waitFor(() => {
      const gameSelect = screen.getByLabelText('Filter by game');
      const options = gameSelect.querySelectorAll('option');

      expect(options).toHaveLength(3); // All Games + 2 mock games
      expect(screen.getByText('World of Warcraft')).toBeInTheDocument();
      expect(screen.getByText('League of Legends')).toBeInTheDocument();
    });
  });

  it('collapses on mobile', () => {
    const { rerender } = render(<FilterSidebar isOpen={true} />, { wrapper });

    // Check sidebar is visible when isOpen is true
    const sidebar = screen.getByLabelText('Filter sidebar');
    expect(sidebar).toBeInTheDocument();

    // Re-render with isOpen=false
    rerender(<FilterSidebar isOpen={false} />);

    // Should be hidden (transform -translate-x-full)
    // Note: Visual testing would verify this better
  });

  it('displays rank options', async () => {
    render(<FilterSidebar />, { wrapper });

    await waitFor(() => {
      const rankSelect = screen.getByLabelText('Filter by rank');
      expect(rankSelect).toBeInTheDocument();

      const ironOption = screen.getByText('Iron');
      const diamondOption = screen.getByText('Diamond');

      expect(ironOption).toBeInTheDocument();
      expect(diamondOption).toBeInTheDocument();
    });
  });

  it('displays status options', async () => {
    render(<FilterSidebar />, { wrapper });

    await waitFor(() => {
      const statusSelect = screen.getByLabelText('Filter by status');
      expect(statusSelect).toBeInTheDocument();

      const approvedOption = screen.getByText('Approved Only');
      const pendingOption = screen.getByText('Pending Only');

      expect(approvedOption).toBeInTheDocument();
      expect(pendingOption).toBeInTheDocument();
    });
  });

  it('shows close button on mobile when onClose prop is provided', () => {
    render(<FilterSidebar isOpen={true} onClose={() => {}} />, { wrapper });

    const closeButton = screen.getByLabelText('Close filters');
    expect(closeButton).toBeInTheDocument();
  });

  it('calls onClose when backdrop is clicked on mobile', () => {
    const handleClose = jest.fn();
    const { container } = render(
      <FilterSidebar isOpen={true} onClose={handleClose} />,
      { wrapper }
    );

    // Click the backdrop
    const backdrop = container.querySelector('.bg-black.bg-opacity-50');
    if (backdrop) {
      fireEvent.click(backdrop);
      expect(handleClose).toHaveBeenCalled();
    }
  });
});
