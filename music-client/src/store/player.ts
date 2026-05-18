import { defineStore } from "pinia";
import { useSongStore } from "@/store/song";
import { getSingerName, resolveDisplaySongTitle } from "@/utils";

type SongId = string | number;

export interface PlaySongItem {
  id: SongId;
  url: string;
  pic: string;
  lyric: unknown;
  name: string;
}

export interface PlaySongPayload {
  id: SongId;
  url: string;
  pic: string;
  lyric: unknown;
  index: number;
  name: string;
  currentSongList: PlaySongItem[];
}

export const usePlayerStore = defineStore("player", {
  state: () => ({
    lastPlayedId: "" as SongId,
  }),
  actions: {
    setLastPlayedId(id: SongId) {
      this.lastPlayedId = id;
    },
    playSong(payload: PlaySongPayload) {
      const { id, url, pic, lyric, index, name, currentSongList } = payload;
      const songStore = useSongStore();
      this.setLastPlayedId(id);
      songStore.playMusic({
        id,
        url,
        pic,
        index,
        songTitle: resolveDisplaySongTitle(name, url),
        singerName: getSingerName(name),
        lyric,
        currentSongList,
      });
    },
  },
});
