import React from 'react';
import { Preview } from '@storybook/react-vite';
import {
  createTheme,
  ThemeProvider,
} from "@mui/material/styles";

export const userAppTheme = createTheme({
  components: {
    MuiTypography: {
      styleOverrides: {
        root: {
          fontFamily: 'inherit'
        }
      }
    }
  }
});
 
const preview: Preview = {
  decorators: [
    (Story) => (
      <ThemeProvider theme={userAppTheme}>
        <Story />
      </ThemeProvider>
    ),
  ],

  parameters: {
    docs: {
      codePanel: true
    }
  }
};
 
export default preview;