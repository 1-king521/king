<template>
  <main class="lyric-page">
    <section class="lyric-content">
      <section class="lyric-track">
        <div class="lyric-panel">
          <transition-group name="lyric-fade">
            <ul :style="{ top: lrcTop }" class="has-lyric" v-if="lyricArr.length" key="has-lyric">
              <li
                v-for="(item, index) in lyricArr"
                :key="index"
                :class="{ 'is-active': index === activeLyricIndex }"
                @click="seekToLyric(item[0])"
              >
                {{ item[1] }}
              </li>
            </ul>
            <div v-else class="no-lyric" key="no-lyric">
              <span>暂无歌词</span>
            </div>
          </transition-group>
        </div>
      </section>
      <aside class="lyric-art">
        <div class="vinyl-disc">
          <div class="vinyl-inner-ring"></div>
          <el-image class="song-pic" fit="cover" :src="attachImageUrl(songPic)" />
        </div>
        <header class="song-meta song-meta--right">
          <h1 class="song-title">{{ songTitle || "未知歌曲" }}</h1>
          <p class="song-singer">{{ singerName || "未知歌手" }}</p>
        </header>
      </aside>
    </section>
  </main>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";

import { useSongStore } from "../../store/song";
import { useConfigureStore } from "../../store/configure";
import {
  attachImageUrl,
  getSingerName,
  resolveDisplaySongTitle,
  isPlaceholderLyric,
  normalizeDurationSeconds,
  parseLyric,
  parseSongTitleFromAudioPath,
} from "../../utils";
import { fetchSongDetail } from "@/api/song";
import { fetchRemoteLyrics, normalizeRemoteLyricBody, saveSongLyricToDb } from "@/api/lyrics";
import { elMessageTypeFromResponse } from "@/api/types";

const route = useRoute();
const songStore = useSongStore();
const configureStore = useConfigureStore();

type LyricLine = [number, string];

const lyricArr = ref<LyricLine[]>([]); // 当前歌曲的歌词（每行一句）
const activeLyricIndex = ref(-1);
const lyricLineHeight = ref(54);
const lyricScrollOffset = 50;
const remoteFetchInFlight = ref(false);

const songId = computed(() => songStore.songId); // 歌曲ID
const lyric = computed(() => songStore.lyric); // 歌词
const currentPlayList = computed(() => songStore.currentPlayList); // 存放的音乐
const currentPlayIndex = computed(() => songStore.currentPlayIndex); // 当前歌曲在歌曲列表的位置
const curTime = computed(() => songStore.curTime);
const songTitle = computed(() => songStore.songTitle); // 歌名
const singerName = computed(() => songStore.singerName); // 歌手名
const songPic = computed(() => songStore.songPic); // 歌曲图片

const lrcTop = computed(
  () =>
    `${-Math.max(activeLyricIndex.value, 0) * lyricLineHeight.value + lyricScrollOffset}px`,
);

/** 将一行歌词拆成「一句一行」，并在本行与下一行时间之间插值 */
function expandLyricOneSentencePerLine(lines: LyricLine[]): LyricLine[] {
  const out: LyricLine[] = [];

  const resolveNextTime = (start: number, currentTime: number) => {
    for (let k = start + 1; k < lines.length; k++) {
      if (lines[k][0] > currentTime) return lines[k][0];
    }
    return currentTime + 3;
  };

  for (let i = 0; i < lines.length; i++) {
    const [t, raw] = lines[i];
    const text = String(raw || "").trim();
    if (!text) continue;
    const nextT = resolveNextTime(i, t);
    const chunks = splitIntoSingleSentences(text);
    if (chunks.length <= 1) {
      out.push([t, text]);
      continue;
    }
    const span = Math.max(0.6, nextT - t);
    chunks.forEach((chunk, j) => {
      out.push([t + (span * j) / chunks.length, chunk]);
    });
  }
  return out.sort((a, b) => a[0] - b[0]);
}

function splitIntoSingleSentences(text: string): string[] {
  const trimmed = text.trim();
  if (!trimmed) return [];
  const byEnd = trimmed
    .split(/(?<=[。！？；])/u)
    .map((s) => s.trim())
    .filter(Boolean);
  if (byEnd.length > 1) return byEnd;
  if (/[,，]/.test(trimmed)) {
    return trimmed
      .split(/[,，]/)
      .map((s) => s.trim())
      .filter(Boolean);
  }
  // 有些歌词没有标点，会用多个空格分隔成两句
  const bySpaces = trimmed
    .split(/[\s\u3000]{2,}/)
    .map((s) => s.trim())
    .filter(Boolean);
  if (bySpaces.length > 1) return bySpaces;
  return [trimmed];
}

async function hydrateSongFromRoute() {
  const raw = route.params.id;
  if (raw == null || raw === "") {
    return;
  }
  const sid = Array.isArray(raw) ? raw[0] : String(raw);
  if (!sid) {
    return;
  }
  // 仅当 store 里已有「可展示」歌词时再跳过详情；否则列表接口可能没带 lyric，会导致永远不请求 DB
  if (
    String(songStore.songId) === String(sid) &&
    songStore.songTitle &&
    !isPlaceholderLyric(songStore.lyric)
  ) {
    return;
  }
  const res = await fetchSongDetail(sid);
  if (!res.success || !res.data) {
    return;
  }
  const d = res.data as {
    id: number;
    name?: string;
    pic?: string;
    lyric?: string;
    introduction?: string;
    duration?: number;
  };
  songStore.setSongId(d.id);
  const fullName = typeof d.name === "string" ? d.name : "";
  songStore.setSongTitle(resolveDisplaySongTitle(fullName, d.url));
  songStore.setSingerName(getSingerName(fullName));
  songStore.setSongPic(d.pic || "");
  songStore.setLyric(typeof d.lyric === "string" ? d.lyric : "");
  if (typeof d.url === "string" && d.url) {
    songStore.setSongUrl(d.url);
  }
  const dur = normalizeDurationSeconds(d.duration);
  if (dur != null) {
    songStore.setDuration(dur);
  }
}

function patchCurrentListLyric(text: string) {
  songStore.patchCurrentTrackLyric(text);
}

async function resolveCurrentLyric() {
  if (typeof window !== "undefined") {
    lyricLineHeight.value = window.matchMedia("(max-width: 668px)").matches
      ? 46
      : 54;
  }

  await hydrateSongFromRoute();

  const fromStore = typeof lyric.value === "string" && lyric.value.trim() ? lyric.value : "";
  const currentSong = currentPlayList.value[currentPlayIndex.value];
  const fromList =
    currentSong && typeof currentSong.lyric === "string" && currentSong.lyric.trim()
      ? currentSong.lyric
      : "";
  let raw = fromStore || fromList;

  const title =
    songTitle.value?.trim() || parseSongTitleFromAudioPath(songStore.songUrl) || "";
  const canRemoteFetch =
    title &&
    !remoteFetchInFlight.value &&
    isPlaceholderLyric(raw);

  if (canRemoteFetch) {
    remoteFetchInFlight.value = true;
    try {
      const durationSec =
        normalizeDurationSeconds(currentSong?.duration) ??
        normalizeDurationSeconds(songStore.duration) ??
        undefined;
      const albumHint =
        (currentSong && typeof currentSong.introduction === "string" && currentSong.introduction.trim()
          ? currentSong.introduction
          : "") || "";

      const res = await fetchRemoteLyrics({
        trackName: title,
        artistName: singerName.value?.trim() || "",
        albumName: albumHint,
        duration: durationSec,
      });
      const fetched = res.success ? normalizeRemoteLyricBody(res.data) : null;
      if (fetched) {
        raw = fetched;
        songStore.setLyric(raw);
        patchCurrentListLyric(raw);
        if (configureStore.token && songId.value !== "" && songId.value != null) {
          const save = await saveSongLyricToDb(songId.value, raw);
          if (!save.success) {
            ElMessage({ message: save.message, type: elMessageTypeFromResponse(save) });
          }
        }
      } else if (!res.success) {
        ElMessage({ message: res.message, type: elMessageTypeFromResponse(res) });
      }
    } catch {
      ElMessage({ message: "获取歌词失败，请确认后端可访问外网或稍后再试", type: "warning" });
    } finally {
      remoteFetchInFlight.value = false;
    }
  }

  /* 并发拉取时本轮可能因 inFlight 跳过请求，但 songStore / 列表已被 prefetch 或其它次轮次更新 */
  const idx = currentPlayIndex.value;
  const list = currentPlayList.value;
  const row = idx >= 0 && Array.isArray(list) && list[idx] ? list[idx] : null;
  const latestStore =
    typeof songStore.lyric === "string" && songStore.lyric.trim() ? songStore.lyric.trim() : "";
  const latestList =
    row && typeof row.lyric === "string" && row.lyric.trim() ? row.lyric.trim() : "";
  raw = latestStore || latestList || raw;

  let parsed: LyricLine[] = [];
  if (raw && !isPlaceholderLyric(raw)) {
    parsed = parseLyric(raw);
    if (!parsed.length && raw.length > 10) {
      parsed = [[0, raw.replace(/\[[^\]]+\]/g, "").replace(/\r?\n+/g, " ").trim() || raw]];
    }
  }
  lyricArr.value = expandLyricOneSentencePerLine(parsed);
  activeLyricIndex.value = -1;
}

watch(
  () => [route.params.id, songStore.songId, songStore.lyric] as const,
  () => void resolveCurrentLyric(),
  { immediate: true },
);

watch(curTime, (value) => {
  if (!lyricArr.value.length) {
    activeLyricIndex.value = -1;
    return;
  }

  let index = -1;
  for (let i = lyricArr.value.length - 1; i >= 0; i--) {
    if (value >= lyricArr.value[i][0]) {
      index = i;
      break;
    }
  }
  activeLyricIndex.value = index;
});

function seekToLyric(seconds: number) {
  if (Number.isNaN(seconds) || seconds < 0) return;
  songStore.setChangeTime(seconds);
}
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.lyric-page {
  height: calc(100vh - 100px);
  padding: 36px 56px 44px;
  background: linear-gradient(180deg, #e8f0fb 0%, #dbe9f8 100%);
  overflow: hidden;
  box-sizing: border-box;
}

.lyric-content {
  max-width: 1280px;
  height: 100%;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(460px, 1fr) minmax(360px, 520px);
  gap: 24px;
  align-items: center;
}

.lyric-track {
  font-family: $font-family;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.song-meta {
  margin-bottom: 18px;
}

.song-title {
  margin: 0;
  font-size: 30px;
  line-height: 1.25;
  color: #1d1f25;
  font-weight: 700;
}

.song-singer {
  margin: 8px 0 0;
  font-size: 18px;
  color: rgba(29, 31, 37, 0.7);
}

.lyric-panel {
  position: relative;
  height: 520px;
  overflow-y: auto;
  overflow-x: hidden;
  mask-image: linear-gradient(
    to bottom,
    transparent 0,
    #000 56px,
    #000 calc(100% - 56px),
    transparent 100%
  );
  -webkit-mask-image: linear-gradient(
    to bottom,
    transparent 0,
    #000 56px,
    #000 calc(100% - 56px),
    transparent 100%
  );
}

.has-lyric {
  position: relative;
  display: flex;
  flex-direction: column;
  left: 0;
  width: 100%;
  transition: top 0.2s linear;

  li {
    height: 54px;
    line-height: 54px;
    font-size: 32px;
    color: rgba(24, 24, 24, 0.36);
    font-weight: 500;
    letter-spacing: 0.01em;
    transition: all 0.25s ease;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    text-align: left;
    cursor: pointer;

    &.is-active {
      color: #222;
      font-weight: 700;
      text-shadow: none;
    }
  }
}

.no-lyric {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  @include layout(flex-start, center);
  padding-top: 30px;

  span {
    font-size: 28px;
    color: rgba(31, 35, 40, 0.5);
  }
}

.lyric-art {
  @include layout(center, flex-start, column);
  gap: 20px;
}

.vinyl-disc {
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background:
    radial-gradient(circle at center, rgba(31, 31, 31, 0.12) 0, rgba(31, 31, 31, 0.12) 24%, transparent 25%),
    radial-gradient(circle at center, #6f9bd8 0, #6f9bd8 56%, #9ec0ea 66%, #5f8ecc 82%, #527fba 100%);
  position: relative;
  @include layout(center, center);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.35);
}

.vinyl-inner-ring {
  width: 290px;
  height: 290px;
  border-radius: 50%;
  border: 10px solid rgba(178, 209, 241, 0.55);
  position: absolute;
}

.song-pic {
  width: 260px;
  height: 260px;
  border-radius: 50%;
  border: 6px solid rgba(255, 255, 255, 0.25);
  overflow: hidden;
}

.song-meta--right {
  width: 100%;
  @include layout(center, center, column);
  text-align: center;
}

.lyric-fade-enter,
.lyric-fade-leave-to {
  transform: translateY(18px);
  opacity: 0;
}

.lyric-fade-enter-active,
.lyric-fade-leave-active {
  transition: all 0.3s ease;
}

@media screen and (min-width: $sm) {
  .lyric-page {
    overflow: hidden;
  }
}

@media screen and (max-width: $sm) {
  .lyric-page {
    padding: 18px 16px 26px;
    overflow: hidden;
  }

  .lyric-content {
    grid-template-columns: 1fr;
  }

  .song-title {
    font-size: 24px;
  }

  .song-singer {
    font-size: 16px;
  }

  .has-lyric li {
    font-size: 26px;
    height: 46px;
    line-height: 46px;
  }

  .lyric-panel {
    height: 420px;
    mask-image: linear-gradient(
      to bottom,
      transparent 0,
      #000 40px,
      #000 calc(100% - 40px),
      transparent 100%
    );
    -webkit-mask-image: linear-gradient(
      to bottom,
      transparent 0,
      #000 40px,
      #000 calc(100% - 40px),
      transparent 100%
    );
  }

  .lyric-art {
    display: none;
  }
}
</style>
