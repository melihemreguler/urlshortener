import React from 'react';
import { Box } from '@mui/material';

interface DebugInfoProps {
  currentPage: number;
  totalPages: number;
  pageSize: number;
  totalElements: number;
  deleteToastsCount: number;
  pendingDeletesCount: number;
}

const DebugInfo: React.FC<DebugInfoProps> = ({
  currentPage,
  totalPages,
  pageSize,
  totalElements,
  deleteToastsCount,
  pendingDeletesCount
}) => {
  // Only show in development mode
  if (import.meta.env.PROD) {
    return null;
  }

  return (
    <Box sx={{ 
      display: 'flex', 
      justifyContent: 'center', 
      p: 1, 
      fontSize: '0.8rem', 
      color: 'text.secondary',
      flexDirection: 'column',
      textAlign: 'center'
    }}>
      <div>
        Page {currentPage} of {totalPages} (Page size: {pageSize}, Total URLs: {totalElements})
      </div>
      <div>
        Active toasts: {deleteToastsCount}, Pending deletes: {pendingDeletesCount}
      </div>
    </Box>
  );
};

export default DebugInfo;
