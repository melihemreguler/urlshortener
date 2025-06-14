import { useState, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { fetchUrls, createShortUrl, deleteShortUrl, searchUrls } from '../api';
import type { ShortUrl, PendingDelete, ToastData } from '../types';

export const useUrlManagement = () => {
  const { t } = useTranslation();
  
  // State
  const [urls, setUrls] = useState<ShortUrl[]>([]);
  const [input, setInput] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(false);
  const [globalToastOpen, setGlobalToastOpen] = useState(false);
  const [globalToastMessage, setGlobalToastMessage] = useState('');
  const [globalToastType, setGlobalToastType] = useState<'info' | 'success' | 'warning' | 'error'>('info');
  const [deleteToasts, setDeleteToasts] = useState<ToastData[]>([]);
  const [pendingDeletes, setPendingDeletes] = useState<PendingDelete[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Ref to keep track of current pending deletes for cleanup
  const pendingDeletesRef = useRef<PendingDelete[]>([]);

  // Load URLs with pagination and search
  const loadUrls = async (page = currentPage, search = searchTerm) => {
    try {
      const response = search && search.trim() 
        ? await searchUrls(search, page - 1, pageSize)
        : await fetchUrls(page - 1, pageSize);
      
      setUrls(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      console.error('Error loading URLs:', error);
      setUrls([]);
      setTotalPages(0);
      setTotalElements(0);
    }
  };

  // Effects
  useEffect(() => {
    loadUrls();
  }, [currentPage]);

  // Debounced search effect
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      setCurrentPage(1); // Reset to first page on search
      loadUrls(1, searchTerm);
    }, 300); // 300ms debounce

    return () => clearTimeout(timeoutId);
  }, [searchTerm]);

  // Keep ref updated with current pending deletes
  useEffect(() => {
    pendingDeletesRef.current = pendingDeletes;
  }, [pendingDeletes]);

  // Cleanup timeouts on unmount
  useEffect(() => {
    return () => {
      pendingDeletesRef.current.forEach(item => {
        clearTimeout(item.timeoutId);
      });
    };
  }, []);

  // Computed value for URLs filtered by pending deletes
  const filteredUrls = urls.filter(url => 
    !pendingDeletes.some(pending => pending.url.id === url.id)
  );

  // Handlers
  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    setCurrentPage(value);
  };

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  const handleSearchClear = () => {
    setSearchTerm('');
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInput(event.target.value);
  };

  const handleCreate = () => {
    const trimmedInput = input.trim();
    if (!trimmedInput) return;
    
    const existingUrl = urls.find(url => url.originalUrl === trimmedInput);
    if (existingUrl) {
      setGlobalToastMessage(t('url_already_exists'));
      setGlobalToastType('warning');
      setGlobalToastOpen(true);
      return;
    }
    
    setLoading(true);
    createShortUrl(trimmedInput)
      .then((newUrl) => {
        if (newUrl.shortUrl && newUrl.originalUrl) {
          // Reload current page to show the new URL
          loadUrls();
        }
      })
      .catch((error) => {
        console.error('Error creating short URL:', error);
        setGlobalToastMessage(t('error_occurred'));
        setGlobalToastType('error');
        setGlobalToastOpen(true);
      })
      .finally(() => {
        setInput('');
        setLoading(false);
      });
  };

  const handleDelete = (id: string) => {
    // Cancel any existing timeout for this URL
    const existingPending = pendingDeletes.find(item => item.url.id === id);
    if (existingPending) {
      clearTimeout(existingPending.timeoutId);
      setPendingDeletes(prev => prev.filter(item => item.url.id !== id));
      setDeleteToasts(prev => prev.filter(item => item.id !== `toast-${id}`));
    }

    // Find URL to delete
    const urlToDelete = urls.find(url => url.id === id);
    if (urlToDelete) {
      // Create timeout for actual deletion
      const timeoutId = setTimeout(() => {
        deleteShortUrl(id)
          .then(() => {
            // Remove from pending deletes
            setPendingDeletes(prev => prev.filter(item => item.url.id !== id));
            // Remove toast
            setDeleteToasts(prev => prev.filter(item => item.id !== `toast-${id}`));
            // Reload URLs to get fresh data from server
            loadUrls();
          })
          .catch((error) => {
            console.error('Error deleting URL:', error);
            // Remove from pending deletes on error too
            setPendingDeletes(prev => prev.filter(item => item.url.id !== id));
            setDeleteToasts(prev => prev.filter(item => item.id !== `toast-${id}`));
            setGlobalToastMessage(t('error_occurred'));
            setGlobalToastType('error');
            setGlobalToastOpen(true);
          });
      }, 5000);

      // Add to pending deletes
      setPendingDeletes(prev => [...prev, {
        id: `delete-${id}`,
        url: urlToDelete,
        timeoutId
      }]);

      // Add delete toast
      const shortUrl = urlToDelete.shortUrl.split('/').pop() || '';
      setDeleteToasts(prev => [...prev, {
        id: `toast-${id}`,
        message: `${t('url_deleted')}: ${shortUrl}`,
        severity: 'success',
        onUndo: () => handleUndoDelete(id),
        undoText: t('undo')
      }]);
    }
  };

  const handleUndoDelete = (urlId: string) => {
    // Use the ref to get current state, avoiding closure issues
    const currentPendingDeletes = pendingDeletesRef.current;
    const pendingItem = currentPendingDeletes.find(item => item.url.id === urlId);
    
    if (pendingItem) {
      clearTimeout(pendingItem.timeoutId);
      
      // Remove from pending deletes
      setPendingDeletes(prev => prev.filter(item => item.url.id !== urlId));
      
      // Remove toast
      setDeleteToasts(prev => prev.filter(item => item.id !== `toast-${urlId}`));
      
      // Reload URLs to ensure the deleted URL is shown again
      loadUrls();
    }
  };

  const handleToastClose = (toastId: string) => {
    setDeleteToasts(prev => prev.filter(item => item.id !== toastId));
  };

  const handleGlobalToastClose = () => {
    setGlobalToastOpen(false);
  };

  return {
    // State
    urls: filteredUrls,
    input,
    searchTerm,
    loading,
    globalToastOpen,
    globalToastMessage,
    globalToastType,
    deleteToasts,
    pendingDeletes,
    currentPage,
    pageSize,
    totalPages,
    totalElements,
    
    // Handlers
    handlePageChange,
    handleSearchChange,
    handleSearchClear,
    handleInputChange,
    handleCreate,
    handleDelete,
    handleToastClose,
    handleGlobalToastClose
  };
};
