import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, FormControl, InputLabel, Select, MenuItem, IconButton } from '@mui/material';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import type { ThemeMode } from '../types';

interface HeaderProps {
  themeMode: ThemeMode;
  onThemeToggle: () => void;
}

const Header: React.FC<HeaderProps> = ({ themeMode, onThemeToggle }) => {
  const { i18n } = useTranslation();

  return (
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
      <IconButton onClick={onThemeToggle} color="inherit">
        {themeMode === 'dark' ? <Brightness7Icon /> : <Brightness4Icon />}
      </IconButton>
    </Box>
  );
};

export default Header;
