import axios, { AxiosRequestConfig } from "axios";
import router from "../router";
import { useConfigureStore } from "../store/configure";
import { useUserStore } from "../store/user";

function clearAuthSession() {
  try {
    const configureStore = useConfigureStore();
    const userStore = useUserStore();
    configureStore.setToken("");
    userStore.setUserId("");
    userStore.setUsername("");
    userStore.setUserPic("");
  } catch {
    /* Pinia 未就绪时忽略 */
  }
}

/**
 * 与后端统一响应约定一致：code / message / success / data。
 * `type` 为历史字段（部分接口仍下发）；与 `api()` 同文件导出，便于 `import { api, type ApiResponse } from "@/utils/request"`。
 */
export type ApiResponse<T = unknown> = {
  /** 与 manage `ApiResponse` 一致，兼容数字或字符串业务码 */
  code: number | string;
  message: string;
  success: boolean;
  data?: T;
  type?: string;
};

function trimEnvHost(raw: string): string {
  return raw.trim().replace(/^["']|["']$/g, "");
}

/**
 * 开发：与 devServer 代理组合，使用当前页 origin，请求走同源转发（避免直连 8888 的 CORS → Network Error）。
 * 生产：使用环境变量中的后端地址；未配置时回退本地 8888。
 */
function resolveBaseURL(): string {
  const raw = process.env.VUE_APP_NODE_HOST || process.env.NODE_HOST;
  const fromEnv = raw && typeof raw === "string" ? trimEnvHost(raw) : "";

  if (process.env.NODE_ENV === "development") {
    if (typeof window !== "undefined" && window.location?.origin) {
      return window.location.origin;
    }
    return "http://localhost:8080";
  }

  if (fromEnv) {
    return fromEnv.replace(/\/+$/, "");
  }
  return "http://localhost:8888";
}

const BASE_URL = resolveBaseURL();

const request = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  /** JWT 走 header，无需 Cookie；withCredentials 与部分 CORS 组合会导致浏览器直接拦截 → Axios Network Error */
  withCredentials: false,
  headers: {
    "Content-Type": "application/json;charset=UTF-8",
  },
});

request.interceptors.request.use((config) => {
  try {
    const jwt = useConfigureStore().token;
    if (jwt) {
      config.headers = config.headers ?? {};
      (config.headers as Record<string, string>)["token"] = jwt;
    }
  } catch {
    /* Pinia 未就绪 */
  }
  return config;
});

request.interceptors.response.use(
  (response) => {
    if (response.status === 200) {
      const body = response.data as ApiResponse | undefined;
      // 仅在本客户端曾保存过 JWT 时把「未登录」视为令牌失效，避免游客浏览接口误触发清会话与跳转
      if (body?.success === false && body.message === "未登录") {
        let hadToken = false;
        try {
          hadToken = !!useConfigureStore().token;
        } catch {
          hadToken = false;
        }
        if (hadToken) {
          clearAuthSession();
          router.replace({ path: "/", query: {} });
        }
      }
      return response;
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
          clearAuthSession();
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

export function getBaseURL() {
  return BASE_URL;
}

export type HttpMethod = "get" | "post" | "delete" | "put";

export interface ApiRequestOptions {
  /** 默认 `get` */
  method?: HttpMethod;
  url: string;
  /** `post` / `put` / `delete` 的 JSON 请求体 */
  data?: object;
  /**
   * GET / DELETE：axios 第二参数；
   * POST / PUT：axios 第三参数（如 `jsonRequestConfig` 或与 manage 一致的 headers）
   */
  config?: AxiosRequestConfig;
}

/**
 * 统一业务请求入口，返回 `ApiResponse<T>`（与 music-manage `utils/request` 的 `api()` 对齐）。
 * 未传 `method` 时为 GET。对 `response.data` 的断言集中在此处，调用方无需再写 `as ApiResponse`。
 */
export function api<T = unknown>(options: ApiRequestOptions): Promise<ApiResponse<T>> {
  const method = (options.method ?? "get").toLowerCase() as HttpMethod;
  const { url, data = {}, config } = options;
  const mapBody = (response: { data: unknown }) => response.data as ApiResponse<T>;

  switch (method) {
    case "post":
      return request.post(url, data, config).then(mapBody);
    case "delete":
      return request.delete(url, config).then(mapBody);
    case "put":
      return request.put(url, data, config).then(mapBody);
    case "get":
    default:
      return request.get(url, config).then(mapBody);
  }
}

/** 二进制响应（非统一 JSON），例如音乐文件下载 */
export function getBlob(url: string, config?: AxiosRequestConfig): Promise<Blob> {
  return request
    .get(url, { ...config, responseType: "blob" })
    .then((response) => response.data as Blob);
}
