const { defineConfig } = require("@vue/cli-service");

/** 后端地址；也可用环境变量覆盖 */
const BACKEND_TARGET = process.env.VUE_APP_BACKEND_TARGET || "http://localhost:8888";

module.exports = defineConfig({
  transpileDependencies: true,
  pages: {
    index: {
      title: "King-music 后台管理",
    },
  },
  /**
   * Vue CLI 会把 devServer.proxy 交给 prepareProxy，只支持：
   * - 字符串 target（推荐）
   * - 或「路径 → { target }」的普通对象
   * 不能传 webpack 文档里的 proxy「数组」，否则会报 proxy must be string or object。
   *
   * 字符串代理规则：非 GET 一律转发；GET 仅在请求不接受 text/html（多为接口）时转发，
   * 浏览器地址栏打开页面时带 text/html，会留在 devServer 走前端路由。
   */
  devServer: {
    proxy: BACKEND_TARGET,
  },
  chainWebpack: (config) => {
    config.plugin("define").tap((definitions) => {
      Object.assign(definitions[0]["process.env"], {
        VUE_APP_NODE_HOST: '"http://localhost:8888"',
        NODE_HOST: '"http://localhost:8888"',
      });
      return definitions;
    });
  },
});
