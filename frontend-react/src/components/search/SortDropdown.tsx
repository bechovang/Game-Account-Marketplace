import React, { useState, useRef, useEffect } from 'react';
import { useFilters } from '../../hooks/useFilters';

export interface SortOption {
  value: string;
  label: string;
  direction: 'ASC' | 'DESC';
}

const SORT_OPTIONS: SortOption[] = [
  { value: 'createdAt', label: 'Newest', direction: 'DESC' },
  { value: 'price', label: 'Price: Low to High', direction: 'ASC' },
  { value: 'price', label: 'Price: High to Low', direction: 'DESC' },
  { value: 'level', label: 'Level', direction: 'DESC' },
];

interface SortDropdownProps {
  className?: string;
}

const SortDropdown: React.FC<SortDropdownProps> = ({ className = '' }) => {
  const { filters, setFilters } = useFilters();
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // Get current sort option
  const getCurrentOption = (): SortOption | null => {
    return SORT_OPTIONS.find(
      (option) =>
        option.value === filters.sortBy && option.direction === filters.sortDirection
    ) || null;
  };

  // Handle sort selection
  const handleSelectSort = (option: SortOption) => {
    setFilters({
      sortBy: option.value,
      sortDirection: option.direction,
    });
    setIsOpen(false);
  };

  const currentOption = getCurrentOption();

  return (
    <div className={`relative ${className}`} ref={dropdownRef}>
      {/* Sort Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center gap-2 px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
        aria-label="Sort options"
        aria-haspopup="listbox"
        aria-expanded={isOpen}
      >
        <svg
          className="w-5 h-5 text-gray-500"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          aria-hidden="true"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M3 4h13M3 8h9m-9 4h6m4 4l4-4m0 0l4 4m-4-4h-4"
          />
        </svg>
        <span className="text-sm font-medium text-gray-700">
          Sort: {currentOption?.label || 'Newest'}
        </span>
        <svg
          className={`w-4 h-4 text-gray-500 transition-transform ${isOpen ? 'rotate-180' : ''}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          aria-hidden="true"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M19 9l-7 7-7-7"
          />
        </svg>
      </button>

      {/* Dropdown Menu */}
      {isOpen && (
        <div
          className="absolute right-0 mt-2 w-56 bg-white border border-gray-200 rounded-lg shadow-lg z-50"
          role="listbox"
          aria-label="Sort options"
        >
          <ul className="py-1">
            {SORT_OPTIONS.map((option, index) => {
              const isActive =
                option.value === filters.sortBy && option.direction === filters.sortDirection;

              return (
                <li key={`${option.value}-${option.direction}-${index}`}>
                  <button
                    onClick={() => handleSelectSort(option)}
                    className={`w-full text-left px-4 py-2 text-sm hover:bg-gray-100 transition-colors ${
                      isActive ? 'bg-blue-50 text-blue-700 font-medium' : 'text-gray-700'
                    }`}
                    role="option"
                    aria-selected={isActive}
                  >
                    <div className="flex items-center justify-between">
                      <span>{option.label}</span>
                      {isActive && (
                        <svg
                          className="w-4 h-4 text-blue-600"
                          fill="currentColor"
                          viewBox="0 0 20 20"
                          aria-hidden="true"
                        >
                          <path
                            fillRule="evenodd"
                            d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                            clipRule="evenodd"
                          />
                        </svg>
                      )}
                    </div>
                  </button>
                </li>
              );
            })}
          </ul>
        </div>
      )}
    </div>
  );
};

export default React.memo(SortDropdown);
