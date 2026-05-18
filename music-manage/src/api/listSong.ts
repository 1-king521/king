import { api } from "@/utils/request";
import { routeQueryValue } from "@/utils/routeQuery";

export function setListSong(params: { songId: number; songListId: string | number }) {
  return api({ method: "post", url: "listSong/add", data: params });
}

export function getListSongOfSongId(songListId: unknown) {
  return api({ url: `listSong/detail?songListId=${routeQueryValue(songListId)}` });
}

/** 删除「指定歌单」内的一首关联；须传 songListId + songId，后端会同步清理 Redis 缓存 */
export function deleteListSong(songListId: string | number, songId: string | number) {
  return api({
    url: `listSong/delete?songListId=${routeQueryValue(songListId)}&songId=${routeQueryValue(songId)}`,
  });
}
