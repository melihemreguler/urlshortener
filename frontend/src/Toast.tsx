import React from 'react';
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import type { AlertProps } from '@mui/material/Alert';

const Alert = React.forwardRef<HTMLDivElement, AlertProps>(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

export default function Toast({
  open,
  onClose,
  message,
  severity = 'info',
  showUndoAction = false,
  onUndo,
  undoText,
}: {
  open: boolean;
  onClose: () => void;
  message: string;
  severity?: 'info' | 'success' | 'warning' | 'error';
  showUndoAction?: boolean;
  onUndo?: () => void;
  undoText?: string;
}) {
  return (
    <Snackbar
      open={open}
      autoHideDuration={showUndoAction ? 5000 : 3000}
      onClose={onClose}
      anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      sx={{ zIndex: 1500 }}
    >
      <Alert 
        onClose={onClose} 
        severity={severity} 
        sx={{ width: '100%' }}
        action={
          showUndoAction && onUndo && undoText ? (
            <Button 
              color="inherit" 
              size="small" 
              onClick={onUndo}
              sx={{ color: 'white' }}
            >
              {undoText}
            </Button>
          ) : undefined
        }
      >
        {message}
      </Alert>
    </Snackbar>
  );
}
