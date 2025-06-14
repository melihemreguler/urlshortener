import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, TextField, InputAdornment, IconButton } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import ClearIcon from '@mui/icons-material/Clear';

interface SearchBoxProps {
  searchTerm: string;
  onSearchChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onSearchClear: () => void;
}

const SearchBox: React.FC<SearchBoxProps> = ({ 
  searchTerm, 
  onSearchChange, 
  onSearchClear 
}) => {
  const { t } = useTranslation();

  return (
    <Box sx={{ width: '100%' }}>
      <TextField
        fullWidth
        variant="outlined"
        label={t('search_placeholder')}
        value={searchTerm}
        onChange={onSearchChange}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon />
            </InputAdornment>
          ),
          endAdornment: searchTerm && (
            <InputAdornment position="end">
              <IconButton onClick={onSearchClear} size="small">
                <ClearIcon />
              </IconButton>
            </InputAdornment>
          )
        }}
        placeholder={t('search_hint')}
        sx={{ mb: 2 }}
      />
    </Box>
  );
};

export default SearchBox;
