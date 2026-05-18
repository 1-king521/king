import axios from "axios";
import type { ApiResponse } from "@/api/types";
import router from "../router";

function trimQuotes(s: string): string {
  return s.trim().replace(/^["']|["']$/g, "");
}

/**
 * 开发：请求当前 devServer 同源，由 vue.config.js 代理到后端，避免 CORS + credentials 导致的 Network Error。
 * 生产：使用环境变量中的后端地址（打包后无代理，需后端 CORS 或与站点同域反代）。
 */
function resolveBaseURL(): string {
  const raw = process.env.VUE_APP_NODE_HOST || process.env.NODE_HOST;
  const fromEnv = raw && typeof raw === "string" ? trimQuotes(raw) : "";

  if (process.env.NODE_ENV === "development") {
    if (typeof window !== "undefined" && window.location?.origin) {
      return `${window.location.origin}/`;
    }
    return "http://localhost:8080/";
  }

  if (fromEnv) {
    const u = fromEnv.replace(/\/+$/, "");
    return `${u}/`;
  }
  return "http://localhost:8888/";
}

const BASE_URL = resolveBaseURL();

/** 管理端登录后写入，用于在 Session Cookie 不可靠时仍能通过 JWT 通过 LoginCheckFilter */
export const ADMIN_TOKEN_KEY = "adminToken";

/**
 * `el-upload`、原生 XHR 不经过 axios 拦截器，须单独附带与 axios 相同的 JWT，否则服务端 LoginCheckFilter 会返回「未登录」。
 */
export function getUploadHeaders(): Record<string, string> {
  if (typeof sessionStorage === "undefined") return {};
  const t = sessionStorage.getItem(ADMIN_TOKEN_KEY);
  return t ? { token: t } : {};
}

/** 管理端本地持久化的管理员头像路径（admin 表无头像列） */
export const MANAGE_ADMIN_AVATAR_KEY = "manageAdminAvatarPath";

axios.defaults.timeout = 15000;
/** Session 与 dev 代理组合：同源请求带 Cookie 即可，无需跨域 */
axios.defaults.withCredentials = true;
axios.defaults.baseURL = BASE_URL;
axios.defaults.headers.post["Content-Type"] = "application/json;charset=UTF-8";

axios.interceptors.request.use((config) => {
  if (typeof sessionStorage !== "undefined") {
    const t = sessionStorage.getItem(ADMIN_TOKEN_KEY);
    if (t) {
      if (!config.headers) {
        config.headers = {};
      }
      config.headers.token = t;
    }
  }
  return config;
});

axios.interceptors.response.use(
  (response) => {
    if (response.status === 200) {
      return Promise.resolve(response);
    }
    return Promise.reject(response);
  },
  (error) => {
    const res = error.response;
    if (!res) {
      return Promise.reject(error);
    }
    if (res.status) {
      switch (res.status) {
        case 401:
          router.replace({ path: "/", query: {} });
          break;
        case 403:
          setTimeout(() => {
            router.replace({ path: "/", query: {} });
          }, 1000);
          break;
        case 404:
          break;
      }
      return Promise.reject(res);
    }
    return Promise.reject(error);
  },
);

export function getBaseURL(): string {
  if (process.env.NODE_ENV === "development" && typeof window !== "undefined" && window.location?.origin) {
    return window.location.origin;
  }
  return BASE_URL.replace(/\/+$/, "");
}

/**
 * 图片等静态资源的基础地址（无尾部斜杠）。
 * 开发环境：与 API 一致使用当前页 origin，经 devServer 代理到后端，避免直连 8888 时部分环境图片 404；
 * 生产环境：与 {@link getBaseURL} 一致。
 */
export function getResourceBaseURL(): string {
  if (process.env.NODE_ENV === "development") {
    if (typeof window !== "undefined" && window.location?.origin) {
      return window.location.origin;
    }
    return "http://localhost:8080";
  }
  return getBaseURL();
}

function rawGet(url: string, config?: object) {
  return new Promise((resolve, reject) => {
    axios.get(url, config).then(
      (response) => resolve(response.data),
      (error) => reject(error),
    );
  });
}

function rawPost(url: string, data: object = {}) {
  return new Promise((resolve, reject) => {
    axios.post(url, data).then(
      (response) => resolve(response.data),
      (error) => reject(error),
    );
  });
}

function rawDelete(url: string, data: object = {}) {
  return new Promise((resolve, reject) => {
    axios.delete(url, data).then(
      (response) => resolve(response.data),
      (error) => reject(error),
    );
  });
}

function rawPut(url: string, data: object = {}) {
  return new Promise((resolve, reject) => {
    axios.put(url, data).then(
      (response) => resolve(response.data),
      (error) => reject(error),
    );
  });
}

export type HttpMethod = "get" | "post" | "delete" | "put";

export interface ApiRequestOptions {
  /** 默认 `get` */
  method?: HttpMethod;
  url: string;
  /** `post` / `put` / `delete` 的 JSON 请求体 */
  data?: object;
  /** 仅 `get`：作为 axios 第二个参数（如 `{ params: { id: 1 } }`） */
  config?: object;
}

/**
 * 统一业务请求入口，返回 `ApiResponse<T>`。
 * - 未传 `method` 时为 GET
 * - 与历史 `get/post/deletes/put` 行为一致，仍走同一套 axios 实例与拦截器
 */
export function api<T = unknown>(options: ApiRequestOptions): Promise<ApiResponse<T>> {
  const method = (options.method ?? "get").toLowerCase() as HttpMethod;
  const { url, data = {}, config } = options;
  let raw: Promise<unknown>;
  switch (method) {
    case "post":
      raw = rawPost(url, data);
      break;
    case "delete":
      raw = rawDelete(url, data);
      break;
    case "put":
      raw = rawPut(url, data);
      break;
    case "get":
    default:
      raw = rawGet(url, config);
      break;
  }
  return raw as Promise<ApiResponse<T>>;
}
