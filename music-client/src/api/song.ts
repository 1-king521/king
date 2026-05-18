import { api, getBlob, type ApiResponse } from "@/utils/request";
import { ensureArray } from "@/utils/api-response-data";
import type { HomeCardItem } from "@/api/models";
import { resolvePlaybackUrl } from "@/utils";

export async function fetchSongListBySingerId(singerId: string | number): Promise<HomeCardItem[]> {
  try {
    const result = await api({ url: `song/singer/detail?singerId=${singerId}` });
    return ensureArray<HomeCardItem>(result?.data);
  } catch {
    return [];
  }
}

/** 下载：返回二进制 Blob，供前端创建对象 URL */
export async function fetchDownloadMusic(songUrl: string): Promise<Blob> {
  const pathOrFull = resolvePlaybackUrl(songUrl);
  if (!pathOrFull) {
    return Promise.reject(new Error("empty song url"));
  }
  const u = pathOrFull.replace(/^https?:\/\/[^/]+/i, "");
  return getBlob(u.startsWith("/") ? u : `/${u}`);
}

export async function fetchSongBySingerName(keywords: string): Promise<ApiResponse> {
  return await api({ url: `song/singerName/detail?name=${keywords}` });
}

export async function fetchSongDetail(id: string | number): Promise<ApiResponse> {
  return await api({ url: `song/detail?id=${id}` });
}

/** 收藏数 Top 歌曲（GET /song/rank，游客可访问） */
export async function fetchSongCollectRank(): Promise<ApiResponse<unknown[]>> {
  return await api({ url: "song/rank" });
}
