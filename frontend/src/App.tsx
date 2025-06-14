import './i18n';
import { useTranslation } from 'react-i18next';
import { useState, useEffect, useRef } from 'react';
import { Container, Typography, Box, Button, TextField, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, IconButton, Select, MenuItem, FormControl, InputLabel, CssBaseline, Pagination } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { fetchUrls, createShortUrl, deleteShortUrl } from './api';
import type { ShortUrl } from './api';
import Toast from './Toast';
import MultiToast, { type ToastData } from './MultiToast';

interface PendingDelete {
  id: string;
  url: ShortUrl;
  timeoutId: number;
}

function App() {
  const { t, i18n } = useTranslation();
  const [themeMode, setThemeMode] = useState<'light' | 'dark'>('light');
  const [urls, setUrls] = useState<ShortUrl[]>([]);
  const [input, setInput] = useState('');
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

  // Load URLs with pagination
  const loadUrls = async (page = currentPage) => {
    try {
      const response = await fetchUrls(page - 1, pageSize); // API uses 0-based page
      console.log('Pagination response:', response);
      console.log('Total pages:', response.totalPages, 'Current page:', page);
      
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

  useEffect(() => {
    loadUrls();
  }, [currentPage]);

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
  }, []); // Empty dependency array - only run on unmount

  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    setCurrentPage(value);
  };

  // Computed value for URLs filtered by pending deletes
  const filteredUrls = urls.filter(url => 
    !pendingDeletes.some(pending => pending.url.id === url.id)
  );

  const handleThemeToggle = () => {
    setThemeMode((prev) => (prev === 'light' ? 'dark' : 'light'));
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
    console.log('Undo delete called for:', urlId);
    const pendingItem = pendingDeletes.find(item => item.url.id === urlId);
    if (pendingItem) {
      console.log('Found pending item, clearing timeout');
      clearTimeout(pendingItem.timeoutId);
      
      // Remove from pending deletes
      setPendingDeletes(prev => prev.filter(item => item.url.id !== urlId));
      
      // Remove toast
      setDeleteToasts(prev => prev.filter(item => item.id !== `toast-${urlId}`));
      
      // Reload URLs to ensure the deleted URL is shown again
      loadUrls();
    } else {
      console.log('Pending item not found for:', urlId);
    }
  };

  const handleToastClose = (toastId: string) => {
    setDeleteToasts(prev => prev.filter(item => item.id !== toastId));
  };

  const handleGlobalToastClose = () => {
    setGlobalToastOpen(false);
  };

  const theme = createTheme({
    palette: { mode: themeMode },
  });

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ position: 'absolute', top: 16, right: 16, display: 'flex', gap: 2 }}>
        <FormControl size="small">
          <InputLabel>Lang</InputLabel>
          <Select
            value={i18n.language}
            label="Lang"
            onChange={(e) => i18n.changeLanguage(e.target.value)}
          >
            <MenuItem value="en">EN</MenuItem>
            <MenuItem value="tr">TR</MenuItem>
          </Select>
        </FormControl>
        <IconButton onClick={handleThemeToggle} color="inherit">
          {themeMode === 'dark' ? <Brightness7Icon /> : <Brightness4Icon />}
        </IconButton>
      </Box>
      <Container 
        maxWidth="xl" 
        sx={{ 
          mt: 4, 
          mb: 4,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '80vh',
          textAlign: 'center' // Added text alignment to center the content
        }}
      >
        <Box 
          sx={{ 
            width: '100%', 
            maxWidth: '1200px', // Increased maxWidth from 1000px to 1200px
            display: 'flex',
            flexDirection: 'column',
            gap: 3,
            margin: '0 auto' // Added margin to center the box horizontally
          }}
        >
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
            {t('title')}
          </Typography>
          <Box sx={{ display: 'flex', gap: 1, width: '100%' }}>
            <TextField
              fullWidth
              variant="outlined"
              label={t('url_placeholder')}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              sx={{ flexGrow: 1 }} // Allow TextField to take available space
            />
            <Button 
              variant="contained" 
              onClick={handleCreate} 
              disabled={loading}
              sx={{ whiteSpace: 'nowrap', flexShrink: 0 }} // Prevent button from shrinking and keep text on one line
            >
              {t('create')}
            </Button>
          </Box>

          <Paper elevation={2} sx={{ borderRadius: 2, overflow: 'hidden' }}>
            <TableContainer>
              <Table>
                <TableHead sx={{ backgroundColor: theme.palette.mode === 'dark' ? 'grey.800' : 'grey.100' }}>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 'bold' }}>{t('original_url')}</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>{t('short_url')}</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }} align="center">{t('actions')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filteredUrls.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={3} align="center" sx={{ py: 4 }}>
                        <Typography variant="body1" color="text.secondary">
                          {t('no_urls')}
                        </Typography>
                      </TableCell>
                    </TableRow>
                  ) : (
                    filteredUrls.map((url: ShortUrl) => (
                      <TableRow key={url.id} hover>
                        <TableCell sx={{ maxWidth: '300px', wordBreak: 'break-word' }}>
                          {url.originalUrl}
                          </TableCell>
                          <TableCell>
                            <a 
                              href={url.shortUrl} 
                              target="_blank" 
                              rel="noopener noreferrer"
                              style={{ 
                                color: theme.palette.primary.main,
                                textDecoration: 'none'
                              }}
                            >
                              {url.shortUrl}
                            </a>
                          </TableCell>
                          <TableCell align="center">
                            <IconButton 
                              color="error" 
                              onClick={() => handleDelete(url.id)}
                              size="small"
                            >
                              <DeleteIcon />
                            </IconButton>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
              
              {/* Pagination */}
              {totalPages > 1 && (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
                  <Pagination
                    count={totalPages}
                    page={currentPage}
                    onChange={handlePageChange}
                    color="primary"
                    size="large"
                    showFirstButton
                    showLastButton
                  />
                </Box>
              )}
              
              {/* Debug info - remove in production */}
              <Box sx={{ display: 'flex', justifyContent: 'center', p: 1, fontSize: '0.8rem', color: 'text.secondary' }}>
                Page {currentPage} of {totalPages} (Page size: {pageSize}, Total URLs: {totalElements})
                <br />
                Active toasts: {deleteToasts.length}, Pending deletes: {pendingDeletes.length}
              </Box>
            </Paper>
        </Box>
      </Container>
      
      {/* Global toast for general messages */}
      <Toast 
        open={globalToastOpen} 
        message={globalToastMessage} 
        onClose={handleGlobalToastClose}
        severity={globalToastType}
      />
      
      {/* Multi-toast for delete operations */}
      <MultiToast 
        toasts={deleteToasts}
        onClose={handleToastClose}
      />
    </ThemeProvider>
  );
}

export default App;
