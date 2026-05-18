<template>
  <audio
    :src="playbackSrc"
    controls
    ref="audioRef"
    preload="metadata"
    @loadedmetadata="syncDurationFromEl"
    @durationchange="syncDurationFromEl"
    @canplay="canplay"
    @timeupdate="timeupdate"
    @ended="ended"
  >
    <!-- controls：原生控件；本页用自定义条，故样式隐藏 -->
    <!-- canplay：新 src 首次就绪时记时长并尝试自动播放（仅一次，避免 seek 后再次 canplay 拉回开头） -->
    <!-- timeupdate / ended：同步 store -->
  </audio>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted } from "vue";

import { useSongStore } from "../../store/song";
import { resolvePlaybackUrl } from "../../utils";

const songStore = useSongStore();
const audioRef = ref<HTMLAudioElement | null>(null);
const pendingUserGesturePlay = ref(false);
/** 每个音频地址只在首次 canplay 时自动 play，避免 seek 后再次 canplay 把进度拉回开头 */
const didAutoplayAfterLoad = ref(false);

const songUrl = computed(() => songStore.songUrl); // 音乐链接
const playbackSrc = computed(() => resolvePlaybackUrl(songUrl.value));
const isPlay = computed(() => songStore.isPlay); // 播放状态
const volume = computed(() => songStore.volume); // 音量
const changeTime = computed(() => songStore.changeTime); // 指定播放时刻
const seekTick = computed(() => songStore.seekTick);
const autoNext = computed(() => songStore.autoNext); // 用于触发自动播放下一首

watch(playbackSrc, () => {
  didAutoplayAfterLoad.value = false;
});

watch(isPlay, () => togglePlay());

watch(
  () => [changeTime.value, seekTick.value] as const,
  ([t]) => {
    const el = audioRef.value;
    if (!el || !Number.isFinite(t)) {
      return;
    }
    const max = Number.isFinite(el.duration) && el.duration > 0 ? el.duration : t;
    const clamped = Math.min(Math.max(0, t), max);
    const shouldResume = isPlay.value;
    el.currentTime = clamped;
    /** 立刻同步，避免进度条在 isDragging 置 false 后仍用旧的 curTime 把滑块打回去（像拉不动 / 从头唱） */
    songStore.setCurTime(clamped);
    /** 部分浏览器在设 currentTime 后会处于暂停，与 store 的「正在播放」不一致，表现为像从头重播 */
    if (shouldResume) {
      void el.play().catch(() => undefined);
    }
  },
  { flush: "sync" },
);

function syncDurationFromEl() {
  const el = audioRef.value;
  if (!el) return;
  const d = el.duration;
  songStore.setDuration(Number.isFinite(d) && d > 0 ? d : 0);
}

watch(volume, (value) => {
  if (audioRef.value) {
    audioRef.value.volume = value;
  }
});

async function tryPlay() {
  if (!audioRef.value) return;
  try {
    await audioRef.value.play();
    pendingUserGesturePlay.value = false;
    songStore.setIsPlay(true);
  } catch {
    // 浏览器自动播放策略导致失败时，等待用户交互后重试
    pendingUserGesturePlay.value = true;
    songStore.setIsPlay(false);
  }
}

// 开始 / 暂停
function togglePlay() {
  if (!audioRef.value) return;
  if (isPlay.value) {
    tryPlay();
  } else {
    audioRef.value.pause();
  }
}

// 新音频就绪：只记时长；自动播放仅在「该 src 第一次 canplay」时触发，避免拖动进度后 canplay 再次触发 tryPlay 导致从头播
function canplay() {
  const el = audioRef.value;
  if (!el) return;
  syncDurationFromEl();
  if (!didAutoplayAfterLoad.value) {
    didAutoplayAfterLoad.value = true;
    tryPlay();
  }
}

// 音乐播放时记录音乐的播放位置
function timeupdate() {
  if (!audioRef.value) return;
  songStore.setCurTime(audioRef.value.currentTime);
}

// 音乐播放结束时触发
function ended() {
  songStore.setIsPlay(false);
  songStore.setCurTime(0);
  songStore.setAutoNext(!autoNext.value);
}

const retryAfterGesture = () => {
  if (pendingUserGesturePlay.value && audioRef.value && audioRef.value.src) {
    tryPlay();
  }
};

onMounted(() => {
  window.addEventListener("pointerdown", retryAfterGesture, { passive: true });
  window.addEventListener("keydown", retryAfterGesture);
  window.addEventListener("touchstart", retryAfterGesture, { passive: true });
});

defineExpose({
  getAudioElement: () => audioRef.value,
});

</script>

<style scoped>
audio {
  display: none;
}
</style>
