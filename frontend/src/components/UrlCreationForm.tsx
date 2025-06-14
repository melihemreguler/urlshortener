import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, TextField, Button } from '@mui/material';

interface UrlCreationFormProps {
  input: string;
  loading: boolean;
  onInputChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: () => void;
}

const UrlCreationForm: React.FC<UrlCreationFormProps> = ({
  input,
  loading,
  onInputChange,
  onSubmit
}) => {
  const { t } = useTranslation();

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter') {
      onSubmit();
    }
  };

  return (
    <Box sx={{ display: 'flex', gap: 1, width: '100%' }}>
      <TextField
        fullWidth
        variant="outlined"
        label={t('url_placeholder')}
        value={input}
        onChange={onInputChange}
        onKeyPress={handleKeyPress}
        sx={{ flexGrow: 1 }}
      />
      <Button 
        variant="contained" 
        onClick={onSubmit} 
        disabled={loading}
        sx={{ whiteSpace: 'nowrap', flexShrink: 0 }}
      >
        {t('create')}
      </Button>
    </Box>
  );
};

export default UrlCreationForm;
