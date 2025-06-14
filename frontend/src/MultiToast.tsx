import { Box, Alert, Button } from '@mui/material';

interface ToastData {
  id: string;
  message: string;
  severity: 'info' | 'success' | 'warning' | 'error';
  onUndo?: () => void;
  undoText?: string;
}

interface MultiToastProps {
  toasts: ToastData[];
  onClose: (id: string) => void;
}

export default function MultiToast({ toasts, onClose }: MultiToastProps) {
  return (
    <Box
      sx={{
        position: 'fixed',
        bottom: 16,
        right: 16,
        zIndex: 2000,
        display: 'flex',
        flexDirection: 'column-reverse', // Reverse to show newest at bottom
        gap: 1,
        maxWidth: '400px',
      }}
    >
      {toasts.map((toast, index) => (
        <Box key={toast.id} sx={{ width: '100%' }}>
          <Alert 
            onClose={() => onClose(toast.id)}
            severity={toast.severity}
            sx={{ 
              width: '100%',
              zIndex: 2000 - index,
            }}
            action={
              toast.onUndo && toast.undoText ? (
                <Button 
                  color="inherit" 
                  size="small" 
                  onClick={(e) => {
                    e.stopPropagation();
                    toast.onUndo?.();
                  }}
                  sx={{ color: 'white' }}
                >
                  {toast.undoText}
                </Button>
              ) : undefined
            }
          >
            {toast.message}
          </Alert>
        </Box>
      ))}
    </Box>
  );
}

export type { ToastData };
