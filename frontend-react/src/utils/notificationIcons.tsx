// Icon and color configuration for notification types
import {
  CheckCircle,
  XCircle,
  DollarSign,
  Receipt,
  CreditCard,
  Bell,
  type LucideIcon,
} from 'lucide-react';
import { NotificationType } from '../types/notifications';

export interface NotificationIconConfig {
  icon: LucideIcon;
  color: string;
  bgColor: string;
}

/**
 * Get icon configuration for a notification type.
 * Returns icon component and Tailwind color classes.
 */
export function getNotificationIcon(type: NotificationType): NotificationIconConfig {
  switch (type) {
    case NotificationType.ACCOUNT_APPROVED:
      return {
        icon: CheckCircle,
        color: 'text-green-600 dark:text-green-400',
        bgColor: 'bg-green-100 dark:bg-green-900/20',
      };

    case NotificationType.ACCOUNT_REJECTED:
      return {
        icon: XCircle,
        color: 'text-red-600 dark:text-red-400',
        bgColor: 'bg-red-100 dark:bg-red-900/20',
      };

    case NotificationType.ACCOUNT_SOLD:
      return {
        icon: DollarSign,
        color: 'text-blue-600 dark:text-blue-400',
        bgColor: 'bg-blue-100 dark:bg-blue-900/20',
      };

    case NotificationType.NEW_TRANSACTION:
      return {
        icon: Receipt,
        color: 'text-blue-600 dark:text-blue-400',
        bgColor: 'bg-blue-100 dark:bg-blue-900/20',
      };

    case NotificationType.PAYMENT_RECEIVED:
      return {
        icon: CreditCard,
        color: 'text-purple-600 dark:text-purple-400',
        bgColor: 'bg-purple-100 dark:bg-purple-900/20',
      };

    default:
      return {
        icon: Bell,
        color: 'text-gray-600 dark:text-gray-400',
        bgColor: 'bg-gray-100 dark:bg-gray-900/20',
      };
  }
}

/**
 * Get toast configuration for react-hot-toast based on notification type.
 */
export function getNotificationToast(type: NotificationType) {
  const config = getNotificationIcon(type);

  return {
    icon: config.icon,
    style: {
      background: config.bgColor,
      color: config.color,
    },
  };
}
