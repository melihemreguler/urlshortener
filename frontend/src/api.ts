// src/api.ts
import type { PageResponse, ShortUrl } from './types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export async function fetchUrls(page = 0, size = 10): Promise<PageResponse<ShortUrl>> {
  const res = await fetch(`${API_BASE_URL}/api/url?page=${page}&size=${size}`);
  if (!res.ok) {
    throw new Error(`HTTP ${res.status}: ${res.statusText}`);
  }
  const data = await res.json();
  
  return {
    content: data.content.map((item: any) => ({
      id: item.id,
      originalUrl: item.longUrl,
      shortUrl: item.shortCode
        ? `${API_BASE_URL}/${item.shortCode}`
        : '',
    })),
    page: data.page,
    size: data.size,
    totalElements: data.totalElements,
    totalPages: data.totalPages,
    first: data.first,
    last: data.last
  };
}

export async function searchUrls(searchTerm: string, page = 0, size = 10): Promise<PageResponse<ShortUrl>> {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString()
  });
  
  if (searchTerm && searchTerm.trim()) {
    params.append('q', searchTerm.trim());
  }
  
  const res = await fetch(`${API_BASE_URL}/api/url/search?${params}`);
  if (!res.ok) {
    throw new Error(`HTTP ${res.status}: ${res.statusText}`);
  }
  const data = await res.json();
  
  return {
    content: data.content.map((item: any) => ({
      id: item.id,
      originalUrl: item.longUrl,
      shortUrl: item.shortCode
        ? `${API_BASE_URL}/${item.shortCode}`
        : '',
    })),
    page: data.page,
    size: data.size,
    totalElements: data.totalElements,
    totalPages: data.totalPages,
    first: data.first,
    last: data.last
  };
}

export async function createShortUrl(originalUrl: string) {
  const trimmedUrl = originalUrl.trim();
  const res = await fetch(`${API_BASE_URL}/api/url`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ longUrl: trimmedUrl }),
  });
  
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || `HTTP ${res.status}: ${res.statusText}`);
  }
  
  const item = await res.json();
  const shortCode = item.shortUrl ? item.shortUrl.split('/').pop() : '';
  
  return {
    id: shortCode,
    originalUrl: trimmedUrl,
    shortUrl: item.shortUrl,
    shortCode: shortCode
  };
}

export async function deleteShortUrl(id: string) {
  const res = await fetch(`${API_BASE_URL}/api/url/${id}`, { method: 'DELETE' });
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || `HTTP ${res.status}: ${res.statusText}`);
  }
}
