import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, FormControl, InputLabel, Select, MenuItem, IconButton, Button, Tooltip } from '@mui/material';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import ApiIcon from '@mui/icons-material/Api';
import type { ThemeMode } from '../types';
import { SWAGGER_UI_URL } from '../config/api';

interface HeaderProps {
  themeMode: ThemeMode;
  onThemeToggle: () => void;
}

const Header: React.FC<HeaderProps> = ({ themeMode, onThemeToggle }) => {
  const { i18n, t } = useTranslation();

  const handleSwaggerClick = () => {
    window.open(SWAGGER_UI_URL, '_blank', 'noopener,noreferrer');
  };

  return (
    <Box sx={{ position: 'absolute', top: 16, right: 16, display: 'flex', gap: 2, alignItems: 'center' }}>
      {/* Swagger API Documentation Button */}
      <Tooltip title={t('api_docs_tooltip')} arrow>
        <Button
          variant="outlined"
          size="small"
          startIcon={<ApiIcon />}
          onClick={handleSwaggerClick}
          sx={{
            borderRadius: 2,
            textTransform: 'none',
            fontSize: '0.75rem',
            minWidth: 'auto',
            px: 1.5,
            py: 0.5,
            borderColor: 'divider',
            color: 'text.secondary',
            '&:hover': {
              borderColor: 'primary.main',
              backgroundColor: 'action.hover',
              color: 'primary.main'
            }
          }}
        >
          {t('api_docs')}
        </Button>
      </Tooltip>

      {/* Language Selector */}
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

      {/* Theme Toggle */}
      <IconButton onClick={onThemeToggle} color="inherit">
        {themeMode === 'dark' ? <Brightness7Icon /> : <Brightness4Icon />}
      </IconButton>
    </Box>
  );
};

export default Header;
