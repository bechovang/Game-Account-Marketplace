// Test file for TypingIndicator component
import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { TypingIndicator } from './TypingIndicator';

describe('TypingIndicator', () => {
  it('renders null when no users are typing', () => {
    const { container } = render(<TypingIndicator users={new Map()} />);
    expect(container.firstChild).toBeNull();
  });

  it('renders single user typing message', () => {
    const users = new Map([[1, 'user@example.com']]);
    render(<TypingIndicator users={users} />);

    expect(screen.getByText('user@example.com is typing...')).toBeInTheDocument();
  });

  it('renders multiple users typing message', () => {
    const users = new Map([
      [1, 'user1@example.com'],
      [2, 'user2@example.com'],
    ]);
    render(<TypingIndicator users={users} />);

    expect(screen.getByText(/user1@example.com and 1 other are typing/)).toBeInTheDocument();
  });

  it('renders multiple users typing message with plural', () => {
    const users = new Map([
      [1, 'user1@example.com'],
      [2, 'user2@example.com'],
      [3, 'user3@example.com'],
    ]);
    render(<TypingIndicator users={users} />);

    expect(screen.getByText(/user1@example.com and 2 others are typing/)).toBeInTheDocument();
  });

  it('renders animated dots', () => {
    const users = new Map([[1, 'user@example.com']]);
    const { container } = render(<TypingIndicator users={users} />);

    const dots = container.querySelectorAll('.animate-bounce');
    expect(dots.length).toBe(3);
  });
});
