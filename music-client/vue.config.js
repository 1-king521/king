const { defineConfig } = require("@vue/cli-service");

/** 与 music-manage 一致：开发时由 devServer 转发到后端，接口与页面同源，避免跨域导致 Axios Network Error */
const BACKEND_TARGET = process.env.VUE_APP_BACKEND_TARGET || "http://localhost:8888";

module.exports = defineConfig({
  pages: {
    index: {
      entry: "src/main.ts",
      title: "Wyk-music",
    },
  },
  transpileDependencies: true,
  devServer: {
    proxy: BACKEND_TARGET,
  },
  chainWebpack: (config) => {
    config.plugin("define").tap((definitions) => {
      Object.assign(definitions[0]["process.env"], {
        NODE_HOST: '"http://localhost:8888"',
        VUE_APP_NODE_HOST: '"http://localhost:8888"',
      });
      return definitions;
    });
  },
});
