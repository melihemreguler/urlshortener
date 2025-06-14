import React from 'react';
import { useTranslation } from 'react-i18next';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  IconButton,
  useTheme
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import type { ShortUrl } from '../types';

interface UrlTableProps {
  urls: ShortUrl[];
  searchTerm: string;
  onDelete: (id: string) => void;
}

const UrlTable: React.FC<UrlTableProps> = ({ urls, searchTerm, onDelete }) => {
  const { t } = useTranslation();
  const theme = useTheme();

  const getEmptyMessage = () => {
    if (searchTerm && searchTerm.trim()) {
      return t('no_search_results');
    }
    return t('no_urls');
  };

  return (
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
          {urls.length === 0 ? (
            <TableRow>
              <TableCell colSpan={3} align="center" sx={{ py: 4 }}>
                <Typography variant="body1" color="text.secondary">
                  {getEmptyMessage()}
                </Typography>
              </TableCell>
            </TableRow>
          ) : (
            urls.map((url: ShortUrl) => (
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
                    onClick={() => onDelete(url.id)}
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
  );
};

export default UrlTable;
