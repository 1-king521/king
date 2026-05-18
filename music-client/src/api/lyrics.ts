import type { AxiosRequestConfig } from "axios";

import { api, type ApiResponse } from "@/utils/request";
import { jsonRequestConfig } from "@/api/types";

/** 统一解析 `/lyrics/fetch` 的 data（兼容少数环境下非 string） */
export function normalizeRemoteLyricBody(data: unknown): string | null {
  if (data == null) {
    return null;
  }
  if (typeof data === "string") {
    const t = data.trim();
    return t ? t : null;
  }
  if (typeof data === "object") {
    const o = data as Record<string, unknown>;
    for (const k of ["lyrics", "lyric", "content", "data"]) {
      const v = o[k];
      if (typeof v === "string" && v.trim()) {
        return v.trim();
      }
    }
  }
  return null;
}

const LYRICS_FETCH_CONFIG: AxiosRequestConfig = {
  timeout: 60_000,
};

export async function fetchRemoteLyrics(payload: {
  trackName: string;
  artistName?: string;
  albumName?: string;
  duration?: number;
}): Promise<ApiResponse<string>> {
  const q = new URLSearchParams();
  q.set("trackName", payload.trackName.trim());
  if (payload.artistName?.trim()) {
    q.set("artistName", payload.artistName.trim());
  }
  if (payload.albumName?.trim()) {
    q.set("albumName", payload.albumName.trim());
  }
  if (payload.duration != null && payload.duration > 0) {
    q.set("duration", String(Math.round(payload.duration)));
  }
  const res = await api<string>({
    url: `lyrics/fetch?${q.toString()}`,
    config: LYRICS_FETCH_CONFIG,
  });
  const text = normalizeRemoteLyricBody(res.data);
  if (text) {
    return { ...res, data: text };
  }
  return res;
}

export async function saveSongLyricToDb(id: string | number, lyric: string): Promise<ApiResponse> {
  return await api({
    method: "post",
    url: "song/lyric/update",
    data: { id, lyric },
    config: jsonRequestConfig,
  });
}
