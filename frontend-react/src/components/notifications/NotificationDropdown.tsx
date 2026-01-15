import React from 'react';
import { formatDistanceToNow } from 'date-fns';
import { Check, Trash2, X } from 'lucide-react';
import { useNotification } from '../../contexts/NotificationContext';
import { getNotificationIcon } from '../../utils/notificationIcons';
import { Button } from '../ui/button';
import { cn } from '../../lib/utils';

export interface NotificationDropdownProps {
  onClose?: () => void;
}

/**
 * Notification dropdown panel showing list of notifications.
 * Includes mark all as read and clear all actions.
 */
export const NotificationDropdown: React.FC<NotificationDropdownProps> = ({ onClose }) => {
  const { notifications, markAsRead, markAllAsRead, clearAll } = useNotification();

  const handleMarkAsRead = (id: string) => {
    markAsRead(id);
  };

  const handleMarkAllAsRead = () => {
    markAllAsRead();
  };

  const handleClearAll = () => {
    clearAll();
    onClose?.();
  };

  return (
    <div className="absolute right-0 mt-2 w-80 md:w-96 bg-background border border-border rounded-lg shadow-lg z-50 max-h-96 flex flex-col">
      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 border-b">
        <h3 className="font-semibold text-sm">Notifications</h3>
        <div className="flex items-center gap-1">
          {notifications.length > 0 && (
            <>
              <Button
                variant="ghost"
                size="sm"
                onClick={handleMarkAllAsRead}
                className="h-7 px-2 text-xs"
                aria-label="Mark all as read"
              >
                <Check className="h-3 w-3 mr-1" />
                Mark all read
              </Button>
              <Button
                variant="ghost"
                size="sm"
                onClick={handleClearAll}
                className="h-7 px-2 text-xs text-destructive"
                aria-label="Clear all notifications"
              >
                <Trash2 className="h-3 w-3 mr-1" />
                Clear
              </Button>
            </>
          )}
          <Button
            variant="ghost"
            size="icon"
            onClick={onClose}
            className="h-7 w-7"
            aria-label="Close notifications"
          >
            <X className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {/* Notifications List */}
      <div className="flex-1 overflow-y-auto">
        {notifications.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
            <div className="w-12 h-12 rounded-full bg-muted flex items-center justify-center mb-3">
              <svg
                className="w-6 h-6 text-muted-foreground"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
                aria-hidden="true"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                />
              </svg>
            </div>
            <p className="text-sm text-muted-foreground">No notifications yet</p>
            <p className="text-xs text-muted-foreground mt-1">
              You'll see notifications here
            </p>
          </div>
        ) : (
          <ul className="divide-y divide-border">
            {notifications.slice(0, 20).map((notification) => {
              const Icon = getNotificationIcon(notification.type).icon;

              return (
                <li
                  key={notification.id}
                  className={cn(
                    'relative px-4 py-3 hover:bg-muted/50 transition-colors cursor-pointer',
                    !notification.isRead && 'bg-muted/30'
                  )}
                  onClick={() => handleMarkAsRead(notification.id)}
                >
                  {/* Unread Indicator */}
                  {!notification.isRead && (
                    <span className="absolute left-2 top-1/2 -translate-y-1/2 w-1 h-8 bg-primary rounded-full" />
                  )}

                  <div className="flex gap-3 pl-3">
                    {/* Icon */}
                    <div
                      className={cn(
                        'flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center',
                        getNotificationIcon(notification.type).bgColor
                      )}
                    >
                      <Icon
                        className={cn(
                          'h-5 w-5',
                          getNotificationIcon(notification.type).color
                        )}
                      />
                    </div>

                    {/* Content */}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between gap-2">
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium truncate">
                            {notification.title}
                          </p>
                          <p className="text-sm text-muted-foreground line-clamp-2">
                            {notification.message}
                          </p>
                        </div>
                      </div>

                      {/* Timestamp */}
                      <p className="text-xs text-muted-foreground mt-1">
                        {formatDistanceToNow(new Date(notification.createdAt), {
                          addSuffix: true,
                        })}
                      </p>
                    </div>
                  </div>
                </li>
              );
            })}
          </ul>
        )}
      </div>
    </div>
  );
};

export default NotificationDropdown;
