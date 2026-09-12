import type { StorybookConfig } from "@storybook/react-vite";

const config: StorybookConfig = {
    stories: ["../src/**/*.mdx", "../src/**/*.stories.@(mdx|ts|tsx)"],
    addons: [
        "@storybook/addon-links",
        "@storybook/addon-essentials",
        "@storybook/addon-storysource",
        "@storybook/addon-interactions",
    ],
    framework: {
        name: "@storybook/react-vite",
        options: {},
    },
    docs: {
        autodocs: "tag",
    },
    staticDirs: ['../public']
};
export default config;
