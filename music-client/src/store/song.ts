import { defineStore } from "pinia";

import { fetchRemoteLyrics, normalizeRemoteLyricBody, saveSongLyricToDb } from "@/api/lyrics";
import { useConfigureStore } from "@/store/configure";
import { useUserStore } from "@/store/user";
import { isPlaceholderLyric, normalizeDurationSeconds, parseSongTitleFromAudioPath } from "@/utils";

export const useSongStore = defineStore("song", {
  state: () => ({
    songId: "" as string | number,
    songTitle: "",
    songUrl: "",
    songPic: "/img/songPic/tubiao.jpg",
    singerName: "",
    /** LRC 原文（字符串）；勿用 [] 作初值，否则在 JS 里为 truthy 会误走解析逻辑 */
    lyric: "" as string,
    isPlay: false,
    volume: 0,
    duration: 0,
    curTime: 0,
    changeTime: 0,
    /** 与 changeTime 同步递增，保证拖到同一秒也能触发 seek */
    seekTick: 0,
    autoNext: true,
    currentPlayList: [] as any[],
    songDetails: null as any,
    currentPlayIndex: -1,
  }),
  actions: {
    setSongId(songId: string | number) {
      this.songId = songId;
    },
    setSongTitle(songTitle: string) {
      this.songTitle = songTitle;
    },
    setSongUrl(songUrl: string) {
      this.songUrl = songUrl;
    },
    setSongPic(songPic: string) {
      this.songPic = songPic;
    },
    setSingerName(singerName: string) {
      this.singerName = singerName;
    },
    setAutoNext(autoNext: boolean) {
      this.autoNext = autoNext;
    },
    setLyric(lyric: string | unknown) {
      this.lyric = lyric == null ? "" : (typeof lyric === "string" ? lyric : String(lyric));
    },
    setIsPlay(isPlay: boolean) {
      this.isPlay = isPlay;
    },
    setVolume(volume: number) {
      this.volume = volume;
    },
    setDuration(duration: number) {
      this.duration = duration;
    },
    setCurTime(curTime: number) {
      this.curTime = curTime;
    },
    setChangeTime(changeTime: number) {
      const t = Number(changeTime);
      this.changeTime = Number.isFinite(t) ? Math.max(0, t) : 0;
      this.seekTick += 1;
    },
    setCurrentPlayList(currentPlayList: any[]) {
      this.currentPlayList = currentPlayList;
    },
    setSongDetails(songDetails: any) {
      this.songDetails = songDetails;
    },
    setCurrentPlayIndex(currentPlayIndex: number) {
      this.currentPlayIndex = currentPlayIndex;
    },
    /** 与当前播放索引对应的列表项写入歌词，避免切歌后列表仍带占位 */
    patchCurrentTrackLyric(text: string) {
      const idx = this.currentPlayIndex;
      const list = this.currentPlayList;
      if (idx < 0 || !Array.isArray(list) || !list[idx]) {
        return;
      }
      const next = [...list];
      next[idx] = { ...next[idx], lyric: text };
      this.setCurrentPlayList(next);
    },
    /**
     * 占位/空歌词时在后台向 LRCLIB 拉取，进入歌词页前即可就绪；失败则静默（歌词页会再试一次）。
     */
    prefetchRemoteLyricIfPlaceholder() {
      void (async () => {
        if (!isPlaceholderLyric(this.lyric)) {
          return;
        }
        const title =
          (this.songTitle || "").trim() ||
          parseSongTitleFromAudioPath(this.songUrl) ||
          "";
        const cur = this.currentPlayList[this.currentPlayIndex];
        const durationSec =
          normalizeDurationSeconds(cur?.duration) ??
          normalizeDurationSeconds(this.duration) ??
          undefined;
        const albumHint =
          cur && typeof cur.introduction === "string" && cur.introduction.trim()
            ? cur.introduction.trim()
            : "";
        try {
          const res = await fetchRemoteLyrics({
            trackName: title,
            artistName: (this.singerName || "").trim(),
            albumName: albumHint,
            duration: durationSec,
          });
          const text = res.success ? normalizeRemoteLyricBody(res.data) : null;
          if (text) {
            this.setLyric(text);
            this.patchCurrentTrackLyric(text);
            try {
              const configureStore = useConfigureStore();
              if (configureStore.token && this.songId !== "" && this.songId != null) {
                await saveSongLyricToDb(this.songId, text);
              }
            } catch {
              /* 保存失败不影响播放 */
            }
          }
        } catch {
          /* 网络错误等 */
        }
      })();
    },
    playMusic({ id, url, pic, index, songTitle, singerName, lyric, currentSongList }) {
      this.setSongId(id);
      this.setSongUrl(url);
      this.setSongPic(pic);
      this.setCurrentPlayIndex(index);
      this.setSongTitle(songTitle);
      this.setSingerName(singerName);
      this.setLyric(lyric);
      this.setCurrentPlayList(currentSongList);
      this.setCurTime(0);
      this.changeTime = 0;
      this.seekTick = 0;
      this.prefetchRemoteLyricIfPlaceholder();
      try {
        const configureStore = useConfigureStore();
        const userStore = useUserStore();
        const token = configureStore.token;
        const uid = userStore.userId;
        if (
          token &&
          uid !== "" &&
          uid != null &&
          id !== "" &&
          id != null
        ) {
          void import("@/api/user")
            .then(({ addUserPlayHistory }) => addUserPlayHistory(uid, id))
            .catch(() => undefined);
        }
      } catch {
        /* Pinia 未就绪等，忽略 */
      }
    },
  },
});
