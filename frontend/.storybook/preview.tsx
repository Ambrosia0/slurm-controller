import React from 'react';
import { Preview } from '@storybook/react';
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
};
 
export default preview;