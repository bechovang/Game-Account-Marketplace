import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Heart, Star, Eye } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { cn, formatCurrency } from '@/lib/utils';

interface Account {
  id: string;
  title: string;
  description?: string;
  price: number;
  level?: number;
  rank?: string;
  status: string;
  viewsCount?: number;
  isFeatured?: boolean;
  isFavorited?: boolean;
  images?: string[];
  createdAt?: string;
  updatedAt?: string;
  seller: {
    id: string;
    fullName: string;
    avatar?: string;
    rating?: number;
    totalReviews?: number;
    email?: string;
    role?: string;
  };
  game: {
    id: string;
    name: string;
    slug: string;
    iconUrl?: string;
    description?: string;
  };
}

interface AccountCardProps {
  account: Account;
  variant?: 'default' | 'featured' | 'compact';
  className?: string;
  showFavorite?: boolean;
  onFavoriteToggle?: (accountId: string) => void;
}

const AccountCard: React.FC<AccountCardProps> = ({
  account,
  variant = 'default',
  className,
  showFavorite = true,
  onFavoriteToggle,
}) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/accounts/${account.id}`);
  };

  const handleFavoriteClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onFavoriteToggle?.(account.id);
  };

  const mainImage = account.images && account.images.length > 0 ? account.images[0];
  const gameIcon = account.game.iconUrl;
  const priceDisplay = formatCurrency(account.price);
  const rating = account.seller.rating || 0;
  const reviewCount = account.seller.totalReviews || 0;

  const getStatusColor = (status: string) => {
    switch (status.toUpperCase()) {
      case 'APPROVED':
        return 'success';
      case 'PENDING':
        return 'warning';
      case 'SOLD':
        return 'destructive';
      case 'REJECTED':
        return 'destructive';
      default:
        return 'secondary';
    }
  };

  const getRoleColor = (role?: string) => {
    switch (role?.toUpperCase()) {
      case 'ADMIN':
        return 'role-admin';
      case 'SELLER':
        return 'role-seller';
      case 'BUYER':
      default:
        return 'role-buyer';
    }
  };

  if (variant === 'compact') {
    return (
      <Card
        className={cn(
          "group cursor-pointer transition-all hover:shadow-lg",
          className
        )}
        onClick={handleClick}
      >
        <CardContent className="p-4">
          <div className="flex items-center space-x-4">
            {/* Thumbnail */}
            <div className="relative h-16 w-16 flex-shrink-0 overflow-hidden rounded-md bg-muted">
              {mainImage ? (
                <img
                  src={mainImage}
                  alt={account.title}
                  className="h-full w-full object-cover"
                  loading="lazy"
                />
              ) : (
                <div className="flex h-full w-full items-center justify-center">
                  <span className="text-xs text-muted-foreground">No Image</span>
                </div>
              )}
            </div>

            {/* Content */}
            <div className="flex min-w-0 flex-1 flex-col">
              <div className="flex items-center space-x-2">
                {gameIcon && (
                  <img
                    src={gameIcon}
                    alt={account.game.name}
                    className="h-4 w-4 rounded"
                    onError={(e) => {
                      e.currentTarget.style.display = 'none';
                    }}
                  />
                )}
                <h3 className="truncate text-sm font-semibold">{account.title}</h3>
              </div>
              <div className="mt-1 flex items-center space-x-2 text-xs text-muted-foreground">
                <span className="font-semibold text-success">{priceDisplay}</span>
                <span>•</span>
                <div className="flex items-center">
                  <Star className="mr-0.5 h-3 w-3 fill-yellow-500 text-yellow-500" />
                  <span>{rating.toFixed(1)}</span>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="flex flex-shrink-0 items-center space-x-2">
              <div className="flex items-center text-xs text-muted-foreground">
                <Eye className="mr-1 h-3 w-3" />
                <span>{account.viewsCount || 0}</span>
              </div>
              {showFavorite && (
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8"
                  onClick={handleFavoriteClick}
                >
                  <Heart
                    className={cn(
                      "h-4 w-4",
                      account.isFavorited
                        ? "fill-destructive text-destructive"
                        : "text-muted-foreground"
                    )}
                  />
                </Button>
              )}
            </div>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card
      className={cn(
        "group cursor-pointer transition-all hover:shadow-lg overflow-hidden",
        variant === 'featured' && "border-primary shadow-md",
        className
      )}
      onClick={handleClick}
    >
      {/* Image */}
      <div className="relative aspect-video overflow-hidden bg-muted">
        {mainImage ? (
          <img
            src={mainImage}
            alt={account.title}
            className="h-full w-full object-cover transition-transform group-hover:scale-105"
            loading="lazy"
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center">
            <span className="text-muted-foreground">No Image</span>
          </div>
        )}

        {/* Overlays */}
        <div className="absolute left-0 top-0 flex space-x-2 p-2">
          {account.isFeatured && (
            <Badge variant="default" className="shadow-sm">
              Featured
            </Badge>
          )}
          <Badge variant={getStatusColor(account.status)} className="shadow-sm">
            {account.status}
          </Badge>
        </div>

        {showFavorite && (
          <div className="absolute right-0 top-0 p-2">
            <Button
              variant="secondary"
              size="icon"
              className="h-8 w-8 rounded-full bg-background/80 backdrop-blur-sm shadow-sm"
              onClick={handleFavoriteClick}
            >
              <Heart
                className={cn(
                  "h-4 w-4 transition-colors",
                  account.isFavorited
                    ? "fill-destructive text-destructive"
                    : "text-muted-foreground"
                )}
              />
            </Button>
          </div>
        )}
      </div>

      {/* Content */}
      <CardContent className="p-4">
        {/* Game and Title */}
        <div className="mb-3 flex items-start space-x-2">
          {gameIcon && (
            <img
              src={gameIcon}
              alt={account.game.name}
              className="h-6 w-6 flex-shrink-0 rounded"
              onError={(e) => {
                e.currentTarget.style.display = 'none';
              }}
            />
          )}
          <div className="min-w-0 flex-1">
            <h3 className="truncate text-lg font-semibold leading-tight">
              {account.title}
            </h3>
            <p className="text-xs text-muted-foreground">{account.game.name}</p>
          </div>
        </div>

        {/* Stats */}
        {(account.level || account.rank) && (
          <div className="mb-3 flex flex-wrap gap-2">
            {account.level && (
              <Badge variant="outline" className="text-xs">
                Lvl {account.level}
              </Badge>
            )}
            {account.rank && (
              <Badge variant="outline" className="text-xs">
                {account.rank}
              </Badge>
            )}
          </div>
        )}

        {/* Price and Rating */}
        <div className="mb-3 flex items-center justify-between">
          <div className="text-2xl font-bold text-success">
            {priceDisplay}
          </div>
          <div className="flex items-center space-x-1 text-sm">
            <Star className="h-4 w-4 fill-yellow-500 text-yellow-500" />
            <span className="font-medium">{rating.toFixed(1)}</span>
            <span className="text-muted-foreground">({reviewCount})</span>
          </div>
        </div>

        {/* Seller Info */}
        <div className="flex items-center justify-between border-t pt-3">
          <div className="flex items-center space-x-2">
            <Avatar className="h-6 w-6">
              <AvatarImage src={account.seller.avatar} alt={account.seller.fullName} />
              <AvatarFallback className="text-xs">
                {account.seller.fullName
                  .split(' ')
                  .map((n) => n[0])
                  .join('')
                  .toUpperCase()
                  .slice(0, 2)}
                </AvatarFallback>
            </Avatar>
            <span className="text-sm text-muted-foreground">
              {account.seller.fullName}
            </span>
          </div>

          <div className="flex items-center space-x-3 text-xs text-muted-foreground">
            <div className="flex items-center">
              <Eye className="mr-1 h-3 w-3" />
              <span>{account.viewsCount || 0}</span>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default AccountCard;
