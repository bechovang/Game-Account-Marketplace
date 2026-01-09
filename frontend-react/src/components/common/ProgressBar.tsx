/**
 * ProgressBar Component
 * 
 * Thin progress bar at the top of the page to indicate loading state
 * Provides subtle visual feedback without blocking content
 */

interface ProgressBarProps {
  loading: boolean;
  className?: string;
}

export function ProgressBar({ loading, className = '' }: ProgressBarProps) {
  if (!loading) return null;

  return (
    <div className={`fixed top-0 left-0 right-0 z-50 ${className}`}>
      <div className="h-1 w-full bg-gray-200 overflow-hidden">
        <div className="h-full bg-blue-600 animate-progress" />
      </div>
    </div>
  );
}

/**
 * Corner loading indicator - small, unobtrusive
 */
interface CornerLoaderProps {
  loading: boolean;
  position?: 'top-right' | 'bottom-right' | 'top-left' | 'bottom-left';
}

export function CornerLoader({ 
  loading, 
  position = 'bottom-right' 
}: CornerLoaderProps) {
  if (!loading) return null;

  const positionClasses = {
    'top-right': 'top-4 right-4',
    'bottom-right': 'bottom-4 right-4',
    'top-left': 'top-4 left-4',
    'bottom-left': 'bottom-4 left-4'
  };

  return (
    <div 
      className={`fixed ${positionClasses[position]} z-50 bg-white shadow-lg rounded-lg px-3 py-2 flex items-center gap-2 animate-fade-in`}
    >
      <div className="w-4 h-4 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
      <span className="text-sm text-gray-600">Updating...</span>
    </div>
  );
}

