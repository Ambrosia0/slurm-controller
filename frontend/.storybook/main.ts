import type { StorybookConfig } from "@storybook/react-vite";

const config: StorybookConfig = {
    stories: ["../src/**/*.mdx", "../src/**/*.stories.@(mdx|ts|tsx)"],
    addons: ["@storybook/addon-links", "@storybook/addon-docs"],

    framework: {
        name: "@storybook/react-vite",
        options: {},
    },

    staticDirs: ['../public']
};
export default config;
