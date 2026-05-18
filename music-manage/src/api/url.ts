import { getBaseURL, getResourceBaseURL } from "@/utils/request";

/** 对 path 各段编码，避免中文/空格导致图片 404 */
function encodeResourcePath(path: string): string {
  return path
    .split("/")
    .map((segment) => {
      if (!segment) {
        return "";
      }
      try {
        return encodeURIComponent(decodeURIComponent(segment));
      } catch {
        return encodeURIComponent(segment);
      }
    })
    .join("/");
}

/**
 * 拼接可访问的图片或静态资源地址（开发环境下直连后端，避免 devServer 代理未转发图片请求而 404）。
 * 若含查询串，仅对 path 分段编码。
 */
export function attachImageUrl(url: string | undefined | null): string {
  const rawUrl = typeof url === "string" ? url.trim() : "";
  const fallbackPath = "/img/avatorImages/user.jpg";
  const normalizedPath = rawUrl || fallbackPath;
  if (/^https?:\/\//i.test(normalizedPath)) {
    return normalizedPath;
  }
  const baseUrl = (getResourceBaseURL() || "").replace(/\/+$/, "");
  const path = normalizedPath.startsWith("/") ? normalizedPath : `/${normalizedPath}`;
  const q = path.indexOf("?");
  const pathPart = q >= 0 ? path.slice(0, q) : path;
  const queryPart = q >= 0 ? path.slice(q) : "";
  const encodedPath = encodeResourcePath(pathPart);
  return `${baseUrl}${encodedPath}${queryPart}`;
}

/**
 * 管理端表单上传、XHR POST 等须与页面同源（开发环境走 devServer 代理并携带 Session Cookie）。
 */
export function attachApiUrl(path: string): string {
  const p = typeof path === "string" ? path.trim() : "";
  const normalized = p.startsWith("/") ? p : `/${p}`;
  const baseUrl = (getBaseURL() || "").replace(/\/+$/, "");
  const q = normalized.indexOf("?");
  const pathPart = q >= 0 ? normalized.slice(0, q) : normalized;
  const queryPart = q >= 0 ? normalized.slice(q) : "";
  const encodedPath = encodeResourcePath(pathPart);
  return `${baseUrl}${encodedPath}${queryPart}`;
}

export function getBannerAddUploadUrl(): string {
  const base = (getBaseURL() || "").replace(/\/+$/, "");
  return `${base}/banner/add`;
}

export function getBannerUpdatePicUploadUrl(id: string | number): string {
  const base = (getBaseURL() || "").replace(/\/+$/, "");
  return `${base}/banner/updatePic?id=${id}`;
}

export function getSongUrlUpdateUploadUrl(id: string | number): string {
  const base = (getBaseURL() || "").replace(/\/+$/, "");
  return `${base}/song/url/update?id=${id}`;
}

export function getSongImgUpdateUploadUrl(id: string | number): string {
  const base = (getBaseURL() || "").replace(/\/+$/, "");
  return `${base}/song/img/update?id=${id}`;
}

/** 后台顶部管理员头像（仅保存到 MinIO 路径，前端 localStorage 记录；库表 admin 无头像字段） */
export function getAdminAvatarUploadUrl(): string {
  return attachApiUrl("/admin/avatar/upload");
}
