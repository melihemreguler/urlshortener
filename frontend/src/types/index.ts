export interface ShortUrl {
  id: string;
  originalUrl: string;
  shortUrl: string;
  shortCode?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface PendingDelete {
  id: string;
  url: ShortUrl;
  timeoutId: number;
}

export interface ToastData {
  id: string;
  message: string;
  severity: 'success' | 'error' | 'warning' | 'info';
  onUndo?: () => void;
  undoText?: string;
}

export type ThemeMode = 'light' | 'dark';
export type ToastType = 'info' | 'success' | 'warning' | 'error';
