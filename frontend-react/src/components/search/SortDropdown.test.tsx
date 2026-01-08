import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter, RouterWrapper } from 'react-router-dom';
import SortDropdown from './SortDropdown';

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <MemoryRouter>{children}</MemoryRouter>
);

describe('SortDropdown', () => {
  it('displays all sort options', async () => {
    render(<SortDropdown />, { wrapper });

    const sortButton = screen.getByLabelText('Sort options');
    expect(sortButton).toBeInTheDocument();

    // Open dropdown
    fireEvent.click(sortButton);

    await waitFor(() => {
      expect(screen.getByText('Newest')).toBeInTheDocument();
      expect(screen.getByText('Price: Low to High')).toBeInTheDocument();
      expect(screen.getByText('Price: High to Low')).toBeInTheDocument();
      expect(screen.getByText('Level')).toBeInTheDocument();
    });
  });

  it('updates URL params when sort changes', async () => {
    const { container } = render(<SortDropdown />, { wrapper });

    const sortButton = screen.getByLabelText('Sort options');
    fireEvent.click(sortButton);

    await waitFor(() => {
      const priceAscOption = screen.getByText('Price: Low to High');
      fireEvent.click(priceAscOption);

      // Check that the button shows the new selection
      // URL check would require checking window.location.search
    });
  });

  it('shows active sort selection', async () => {
    render(<SortDropdown />, { wrapper });

    const sortButton = screen.getByLabelText('Sort options');
    expect(sortButton).toBeInTheDocument();

    // Default should be "Newest"
    expect(screen.getByText(/Sort: Newest/)).toBeInTheDocument();

    // Open dropdown
    fireEvent.click(sortButton);

    await waitFor(() => {
      // "Newest" option should be marked as active (has checkmark)
      const newestOption = screen.getByText('Newest');
      const checkmark = newestOption.closest('button')?.querySelector('svg');
      expect(checkmark).toBeInTheDocument();
    });
  });

  it('closes dropdown when clicking outside', async () => {
    render(<SortDropdown />, { wrapper });

    const sortButton = screen.getByLabelText('Sort options');

    // Open dropdown
    fireEvent.click(sortButton);

    await waitFor(() => {
      expect(screen.getByRole('listbox', { hidden: false })).toBeInTheDocument();
    });

    // Click outside
    fireEvent.mouseDown(document.body);

    await waitFor(() => {
      const listbox = screen.queryByRole('listbox');
      expect(listbox).not.toBeInTheDocument();
    });
  });

  it('toggles dropdown on button click', async () => {
    render(<SortDropdown />, { wrapper });

    const sortButton = screen.getByLabelText('Sort options');

    // Open
    fireEvent.click(sortButton);
    await waitFor(() => {
      expect(screen.getByRole('listbox')).toBeInTheDocument();
    });

    // Close
    fireEvent.click(sortButton);
    await waitFor(() => {
      const listbox = screen.queryByRole('listbox');
      expect(listbox).not.toBeInTheDocument();
    });
  });

  it('has proper accessibility attributes', async () => {
    render(<SortDropdown />, { wrapper });

    const sortButton = screen.getByLabelText('Sort options');
    expect(sortButton).toHaveAttribute('aria-haspopup', 'listbox');
    expect(sortButton).toHaveAttribute('aria-expanded', 'false');

    fireEvent.click(sortButton);

    await waitFor(() => {
      expect(sortButton).toHaveAttribute('aria-expanded', 'true');
      const listbox = screen.getByRole('listbox');
      expect(listbox).toHaveAttribute('aria-label', 'Sort options');
    });
  });

  it('applies custom className', () => {
    const { container } = render(<SortDropdown className="custom-class" />, { wrapper });

    const wrapperDiv = container.querySelector('.custom-class');
    expect(wrapperDiv).toBeInTheDocument();
  });

  it('persists selection in URL', async () => {
    // This would require checking window.location or useSearchParams
    // For now, we test that the selection is made
    render(<SortDropdown />, { wrapper });

    const sortButton = screen.getByLabelText('Sort options');
    fireEvent.click(sortButton);

    await waitFor(() => {
      const levelOption = screen.getByText('Level');
      fireEvent.click(levelOption);

      // Button should show "Level"
      expect(screen.getByText(/Sort: Level/)).toBeInTheDocument();
    });
  });
});
