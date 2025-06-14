import './i18n';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Container, Typography, Box, Paper, CssBaseline } from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';

// Components
import Header from './components/Header';
import SearchBox from './components/SearchBox';
import UrlCreationForm from './components/UrlCreationForm';
import UrlTable from './components/UrlTable';
import PaginationControls from './components/PaginationControls';
import DebugInfo from './components/DebugInfo';
import Toast from './Toast';
import MultiToast from './MultiToast';

// Hooks
import { useUrlManagement } from './hooks/useUrlManagement';

// Types
import type { ThemeMode } from './types';

function App() {
  const { t } = useTranslation();
  const [themeMode, setThemeMode] = useState<ThemeMode>('light');

  // Use our custom hook for URL management
  const {
    urls,
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
    handlePageChange,
    handleSearchChange,
    handleSearchClear,
    handleInputChange,
    handleCreate,
    handleDelete,
    handleToastClose,
    handleGlobalToastClose
  } = useUrlManagement();

  const handleThemeToggle = () => {
    setThemeMode((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  const theme = createTheme({
    palette: { mode: themeMode },
  });

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      
      <Header 
        themeMode={themeMode}
        onThemeToggle={handleThemeToggle}
      />
      
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
          textAlign: 'center'
        }}
      >
        <Box 
          sx={{ 
            width: '100%', 
            maxWidth: '1200px',
            display: 'flex',
            flexDirection: 'column',
            gap: 3,
            margin: '0 auto'
          }}
        >
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
            {t('title')}
          </Typography>
          
          <SearchBox
            searchTerm={searchTerm}
            onSearchChange={handleSearchChange}
            onSearchClear={handleSearchClear}
          />
          
          <UrlCreationForm
            input={input}
            loading={loading}
            onInputChange={handleInputChange}
            onSubmit={handleCreate}
          />

          <Paper elevation={2} sx={{ borderRadius: 2, overflow: 'hidden' }}>
            <UrlTable
              urls={urls}
              searchTerm={searchTerm}
              onDelete={handleDelete}
            />
            
            <PaginationControls
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
            
            <DebugInfo
              currentPage={currentPage}
              totalPages={totalPages}
              pageSize={pageSize}
              totalElements={totalElements}
              deleteToastsCount={deleteToasts.length}
              pendingDeletesCount={pendingDeletes.length}
            />
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
