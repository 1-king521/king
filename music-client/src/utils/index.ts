import { getBaseURL } from "@/utils/request";

/**
 * 品牌占位（仅保留在源码/需要显式引用的场景；日常无封面请用中性灰占位，避免歌单卡片出现 K）
 */
export const IMAGE_PLACEHOLDER_K = `data:image/svg+xml,${encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="96" height="96" viewBox="0 0 96 96"><defs><linearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%"><stop offset="0%" stop-color="#6B7FD7"/><stop offset="100%" stop-color="#4A59A8"/></linearGradient></defs><circle cx="48" cy="48" r="44" fill="url(#g)"/><text x="48" y="66" text-anchor="middle" font-family="system-ui,-apple-system,Segoe UI,sans-serif" font-size="44" font-weight="700" fill="#fff">K</text></svg>',
)}`;

/** 无封面时默认：纯灰底、无文字，不依赖外网 CDN */
const IMAGE_PLACEHOLDER_NEUTRAL = `data:image/svg+xml,${encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="96" height="96" viewBox="0 0 96 96"><rect width="96" height="96" rx="8" fill="#e8e8e8"/></svg>',
)}`;

const DEFAULT_PLACEHOLDER = IMAGE_PLACEHOLDER_NEUTRAL;

/**
 * 对 path 的每一段做 encodeURIComponent，保留 `/`，避免中文或空格文件名导致请求 404。
 */
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
 * 拼接后端静态 / MinIO 地址。已是 http(s) 的则原样返回，避免重复拼接 base。
 * 若含 ? 查询串，只对 path 分段编码，不把 ?id= 编成 %3F（否则上传类 URL 会 404）。
 */
export function attachImageUrl(url: unknown): string {
  if (url == null || url === "") {
    return DEFAULT_PLACEHOLDER;
  }
  const s = String(url).trim();
  if (!s) {
    return DEFAULT_PLACEHOLDER;
  }
  if (/^https?:\/\//i.test(s)) {
    return s;
  }
  const base = (getBaseURL() || "").replace(/\/+$/, "");
  const path = s.startsWith("/") ? s : `/${s}`;
  const q = path.indexOf("?");
  const pathPart = q >= 0 ? path.slice(0, q) : path;
  const queryPart = q >= 0 ? path.slice(q) : "";
  const encodedPath = encodeResourcePath(pathPart);
  return base ? `${base}${encodedPath}${queryPart}` : `${encodedPath}${queryPart}`;
}

/** 音频地址：无 url 时返回空串，不能用图片占位图作为 audio src */
export function resolvePlaybackUrl(url: unknown): string {
  if (url == null || url === "") {
    return "";
  }
  const s = String(url).trim();
  if (!s) {
    return "";
  }
  if (/^https?:\/\//i.test(s)) {
    return s;
  }
  const base = (getBaseURL() || "").replace(/\/+$/, "");
  const path = s.startsWith("/") ? s : `/${s}`;
  const q = path.indexOf("?");
  const pathPart = q >= 0 ? path.slice(0, q) : path;
  const queryPart = q >= 0 ? path.slice(q) : "";
  const encodedPath = encodeResourcePath(pathPart);
  return base ? `${base}${encodedPath}${queryPart}` : `${encodedPath}${queryPart}`;
}

/** 将接口里的时长（number / 数字字符串）规范为秒；无效返回 null */
export function normalizeDurationSeconds(raw: unknown): number | null {
  if (raw == null || raw === "") {
    return null;
  }
  const n =
    typeof raw === "number"
      ? raw
      : typeof raw === "string"
        ? Number.parseFloat(raw.trim())
        : Number.NaN;
  if (!Number.isFinite(n) || n <= 0) {
    return null;
  }
  return Math.round(n);
}

/**
 * 「歌手-歌名」按最后一个 `-` 分割（与后端常用「仅一个分隔」语义一致），
 * 避免 `G.E.M.邓紫棋-泡沫` 被 `split("-")` 拆成歌名 `E`、歌手 `G`。
 */
function splitArtistTitleByLastHyphen(combined: string): { artist: string; title: string } {
  const t = combined.trim();
  if (!t) {
    return { artist: "", title: "" };
  }
  const idx = t.lastIndexOf("-");
  if (idx <= 0) {
    return { artist: "", title: t };
  }
  const right = t.slice(idx + 1).trim();
  const left = t.slice(0, idx).trim();
  if (!right) {
    return { artist: left, title: "" };
  }
  return { artist: left, title: right };
}

// 解析歌曲标题（歌手-歌名 => 歌名）
export function getSongTitle(name: string) {
  return splitArtistTitleByLastHyphen(typeof name === "string" ? name : "").title;
}

// 解析歌手名（歌手-歌名 => 歌手；无分隔符时整串视为歌名，歌手为空）
export function getSingerName(name: string) {
  const { artist, title } = splitArtistTitleByLastHyphen(typeof name === "string" ? name : "");
  if (!artist && title) {
    return "";
  }
  return artist;
}

/** 从音频路径取文件名（去扩展名），再按「最后一个 -」取歌名；用于 name 写成 `G.E.M.邓紫棋-` 而 url 仍为 `…-泡沫.mp3` */
export function parseSongTitleFromAudioPath(url: unknown): string {
  if (url == null || url === "") {
    return "";
  }
  let path = String(url).trim();
  if (/^https?:\/\//i.test(path)) {
    try {
      path = new URL(path).pathname;
    } catch {
      path = path.replace(/^https?:\/\/[^/]+/i, "");
    }
  }
  path = path.replace(/\?.*$/, "");
  const seg = path.split("/").pop() || "";
  const base = seg.replace(/\.[^.]+$/i, "").trim();
  if (!base) {
    return "";
  }
  const { title, artist } = splitArtistTitleByLastHyphen(base);
  if (title) {
    return title;
  }
  if (artist && !title) {
    return "";
  }
  return base;
}

/** 展示 / LRCLIB：优先库内 name 解析出的歌名，否则用 url 文件名推断 */
export function resolveDisplaySongTitle(name: unknown, url?: unknown): string {
  const fromName = getSongTitle(typeof name === "string" ? name : "");
  if (fromName) {
    return fromName;
  }
  return parseSongTitleFromAudioPath(url);
}

export function getUserSex(sex: number) {
  if (sex === 0) return "女";
  if (sex === 1) return "男";
  return "";
}

// 解析日期
export function getBirth(value) {
  if (value == null || value == "") return "";
  const date = new Date(value);
  const year = date.getFullYear();
  const month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
  const day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
  return year + "-" + month + "-" + day;
}

/**
 * 将接口/表单里的生日规范为有效 Date，供 el-date-picker 绑定。
 * 非法值会用 fallback，避免面板在异常 modelValue 下访问 DOM 报错（如 getElementsByTagName on null）。
 */
export function coerceDatePickerValue(raw: unknown, fallback: Date = new Date()): Date {
  if (raw instanceof Date && !Number.isNaN(raw.getTime())) {
    return raw;
  }
  if (typeof raw === "string" && raw.trim()) {
    const d = new Date(raw);
    if (!Number.isNaN(d.getTime())) {
      return d;
    }
  }
  if (typeof raw === "number" && Number.isFinite(raw)) {
    const d = new Date(raw);
    if (!Number.isNaN(d.getTime())) {
      return d;
    }
  }
  return fallback;
}

/**
 * 表格时间格式化
 */
export function formatDate(cellValue) {
  if (cellValue == null || cellValue == "") return "";
  const date = new Date(cellValue);
  const year = date.getFullYear();
  const month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
  const day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
  const hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
  const minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
  const seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
  return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
}

/** 匹配 LRC 时间标签：优先 [HH:mm:ss] / [HH:mm:ss.xx]，其次 [mm:ss] / [mm:ss.xx] */
function matchLrcTimeTags(line: string): RegExpMatchArray | null {
  return line.match(
    /\[\d{1,2}:\d{1,2}:\d{1,2}(?:\.\d{1,3})?\]|\[\d{1,3}:\d{1,2}(?:\.\d{1,3})?\]/g,
  );
}

function lineStartsWithLrcTime(line: string): boolean {
  const t = line.trim().replace(/^\uFEFF/, "");
  return (
    /^\[\d{1,2}:\d{1,2}:\d{1,2}(?:\.\d{1,3})?\]/.test(t) ||
    /^\[\d{1,3}:\d{1,2}(?:\.\d{1,3})?\]/.test(t)
  );
}

/** 标签内时间串 → 秒（从 0 起） */
function lrcInnerToSeconds(inner: string): number | null {
  const parts = inner.split(":");
  if (parts.length >= 3) {
    const h = parseInt(parts[0], 10);
    const m = parseInt(parts[1], 10);
    const s = parseFloat(parts[2]);
    if (Number.isNaN(h) || Number.isNaN(m) || Number.isNaN(s)) {
      return null;
    }
    return h * 3600 + m * 60 + s;
  }
  if (parts.length === 2) {
    const m = parseInt(parts[0], 10);
    const s = parseFloat(parts[1]);
    if (Number.isNaN(m) || Number.isNaN(s)) {
      return null;
    }
    return m * 60 + s;
  }
  return null;
}

/**
 * 将 LRC 文本解析为 [秒数, 歌词行] 列表，供与 `songStore.curTime` 对照高亮。
 * 支持常见 [mm:ss.xx] 以及库内使用的 [HH:mm:ss]（如 [00:00:00]暂无歌词）。
 */
export function parseLyric(text: unknown): [number, string][] {
  if (text == null || text === "") {
    return [];
  }
  if (typeof text !== "string") {
    return [];
  }
  const raw = text
    .replace(/^\uFEFF/, "")
    .replace(/\r\n/g, "\n")
    .replace(/\r/g, "\n")
    .trim();
  if (!raw) {
    return [];
  }

  if (!/\[[^\]]+\]/.test(raw)) {
    return [[0, raw]];
  }

  let lines = raw.split("\n");
  while (lines.length > 0 && !lineStartsWithLrcTime(lines[0])) {
    lines = lines.slice(1);
  }
  if (!lines.length) {
    return [];
  }

  const result: [number, string][] = [];
  if (lines[lines.length - 1].length === 0) {
    lines.pop();
  }

  for (const item of lines) {
    const tags = matchLrcTimeTags(item);
    if (!tags) {
      continue;
    }
    let value = item;
    for (const tag of tags) {
      value = value.replace(tag, "");
    }
    value = value
      .replace(/<\d{1,2}:\d{1,2}:\d{1,2}(?:\.\d{1,3})?>/g, "")
      .replace(/<\d{1,3}:\d{1,2}(?:\.\d{1,3})?>/g, "")
      .trim();
    for (const tag of tags) {
      const inner = tag.slice(1, -1);
      const seconds = lrcInnerToSeconds(inner);
      if (seconds == null) {
        continue;
      }
      if (value !== "") {
        result.push([seconds, value]);
      }
    }
  }
  result.sort((a, b) => a[0] - b[0]);
  return result;
}

/** 数据库占位、空串等：需要向 LRCLIB 拉取真歌词 */
export function isPlaceholderLyric(text: unknown): boolean {
  if (text == null) {
    return true;
  }
  if (typeof text !== "string") {
    return true;
  }
  const t = text.trim().replace(/^\uFEFF/, "");
  if (!t) {
    return true;
  }
  if (/暂无歌词/.test(t)) {
    return true;
  }
  if (/^[\s\r\n]*\[0*\s*:\s*0+\s*:\s*0+\s*(?:\.\d{1,3})?\]\s*暂无歌词\s*$/i.test(t)) {
    return true;
  }
  // 导入/工具截断的常见脏数据：只有零时刻一行 +「暂…」（完整应为「暂无歌词」）
  if (
    /^[\s\r\n]*\[0*\s*:\s*0+\s*:\s*0+\s*(?:\.\d{1,3})?\]\s*(?:暂|暂无|暂无歌|暂无歌词)\s*$/i.test(t)
  ) {
    return true;
  }
  return false;
}

// 解析播放时间
export function formatSeconds(value) {
  let theTime = parseInt(value);
  let theTime1 = 0;
  let theTime2 = 0;
  if (theTime > 60) {
    theTime1 = parseInt((theTime / 60).toString()); // 分
    theTime = parseInt((theTime % 60).toString()); // 秒
    // 是否超过一个小时
    if (theTime1 > 60) {
      theTime2 = parseInt((theTime1 / 60).toString()); // 小时
      theTime1 = 60; // 分
    }
  }
  // 多少秒
  let result = "";
  if (parseInt(theTime.toString()) < 10) {
    result = "0:0" + parseInt(theTime.toString());
  } else {
    result = "0:" + parseInt(theTime.toString());
  }
  // 多少分钟时
  if (theTime1 > 0) {
    if (parseInt(theTime.toString()) < 10) {
      result = "0" + parseInt(theTime.toString());
    } else {
      result = parseInt(theTime.toString()).toString();
    }
    result = parseInt(theTime1.toString()) + ":" + result;
  }
  // 多少小时时
  if (theTime2 > 0) {
    if (parseInt(theTime.toString()) < 10) {
      result = "0" + parseInt(theTime.toString());
    } else {
      result = parseInt(theTime.toString()).toString();
    }
    result = parseInt(theTime2.toString()) + ":" + parseInt(theTime1.toString()) + ":" + result;
  }
  return result;
}
