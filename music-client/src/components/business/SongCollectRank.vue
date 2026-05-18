<template>
  <section
    v-if="items.length || showEmptyState"
    class="song-rank"
    :class="{ 'song-rank--page': showEmptyState }"
    :aria-label="title"
  >
    <div class="song-rank__head">
      <h2 class="song-rank__title">{{ title }}</h2>
      <span class="song-rank__hint">{{ hint }}</span>
    </div>
    <div v-if="items.length && props.labelLayout === 'songThenArtist'" class="song-rank__list">
      <article
        v-for="(item, index) in items"
        :key="item.id"
        class="song-rank__row"
        role="button"
        tabindex="0"
        @click="playAt(index)"
        @keydown.enter.prevent="playAt(index)"
      >
        <span class="song-rank__row-rank" :class="rankNumClass(index + 1)">{{ index + 1 }}</span>
        <div class="song-rank__row-cover">
          <el-image class="song-rank__row-img" fit="cover" :src="attachImageUrl(item.pic)" />
        </div>
        <div class="song-rank__row-info">
          <p class="song-rank__row-title">{{ displaySongThenArtist(item) }}</p>
        </div>
        <button type="button" class="song-rank__row-play" aria-label="播放" @click.stop="playAt(index)">
          <Play class="song-rank__row-play-icon" />
        </button>
        <div class="song-rank__row-fav" aria-hidden="true" @click.stop>
          <Heart class="song-rank__row-heart" />
          <span class="song-rank__row-fav-count">{{ formatCount(item.collectCount) }}</span>
        </div>
      </article>
    </div>
    <div v-else-if="items.length" class="song-rank__scroll">
      <article
        v-for="(item, index) in items"
        :key="item.id"
        class="song-rank__card"
        @click="playAt(index)"
      >
        <span class="song-rank__badge" :class="badgeClass(index + 1)">{{ index + 1 }}</span>
        <div class="song-rank__cover">
          <el-image class="song-rank__img" fit="cover" :src="attachImageUrl(item.pic)" />
          <div class="song-rank__mask">
            <Play class="song-rank__play-icon" />
          </div>
        </div>
        <div class="song-rank__meta">
          <p class="song-rank__name">{{ displayTitle(item) }}</p>
          <p class="song-rank__sub">{{ subSingerLabel(item) }}</p>
          <p class="song-rank__count">
            <Heart class="song-rank__heart" /> {{ formatCount(item.collectCount) }}
          </p>
        </div>
      </article>
    </div>
    <el-empty v-else class="song-rank__empty" description="暂无排行数据" />
  </section>
</template>

<script lang="ts" setup>
import { computed, ref } from "vue";
import { Play, Heart } from "lucide-vue-next";
import { fetchSongCollectRank } from "@/api/song";
import { ensureArray } from "@/utils/api-response-data";
import { attachImageUrl, getSingerName, resolveDisplaySongTitle } from "@/utils";
import { useAppActions } from "@/composables/useAppActions";

const props = withDefaults(
  defineProps<{
    /** 区块标题 */
    title?: string;
    /** 副标题说明 */
    hint?: string;
    /** 为 true 时无数据也展示区域（独立页用） */
    showEmptyState?: boolean;
    /** stacked：歌名一行+歌手一行；songThenArtist：单行「歌曲名-作者」 */
    labelLayout?: "stacked" | "songThenArtist";
  }>(),
  {
    title: "收藏排行榜",
    hint: "按用户收藏次数",
    showEmptyState: false,
    labelLayout: "stacked",
  },
);

type RankRow = {
  id: number | string;
  name?: string;
  singerName?: string;
  pic?: string;
  url?: string;
  lyric?: string;
  collectCount?: number | string;
};

const { playMusic } = useAppActions();

const rawItems = ref<RankRow[]>([]);

const items = computed(() => (Array.isArray(rawItems.value) ? rawItems.value : []));

function badgeClass(rank: number) {
  if (rank === 1) return "is-gold";
  if (rank === 2) return "is-silver";
  if (rank === 3) return "is-bronze";
  return "";
}

/** 列表式排名数字颜色（前三高亮） */
function rankNumClass(rank: number) {
  if (rank === 1) return "is-top1";
  if (rank === 2) return "is-top2";
  if (rank === 3) return "is-top3";
  return "";
}

function displayTitle(row: RankRow) {
  const name = (row.name || "").trim();
  return name || "未知歌曲";
}

function subSingerLabel(row: RankRow) {
  const s = String(row.singerName ?? "").trim();
  return s || "未知歌手";
}

/** 展示为「歌曲名-作者」（与站内「歌手-歌名」存储格式解耦） */
function displaySongThenArtist(row: RankRow) {
  const rawName = (row.name || "").trim();
  const url = row.url;
  const apiSinger = String(row.singerName ?? "").trim();
  const title =
    (resolveDisplaySongTitle(rawName, url) || "").trim() || rawName || "未知歌曲";
  const artist = apiSinger || getSingerName(rawName).trim() || "未知作者";
  return `${title}-${artist}`;
}

function formatCount(v: unknown) {
  const n = typeof v === "number" ? v : Number(v);
  return Number.isFinite(n) ? n : 0;
}

function playAt(index: number) {
  const source = items.value;
  if (!source.length || index < 0 || index >= source.length) return;

  const playList = source.map((raw) => {
    const rawName = (raw.name || "").trim();
    const singer = (String(raw.singerName ?? "").trim() || getSingerName(rawName)).trim();
    const title =
      (resolveDisplaySongTitle(rawName, raw.url) || "").trim() || rawName || "未知歌曲";
    const combined =
      singer && title ? `${singer}-${title}` : rawName || singer || title || "未知歌曲";
    return { ...raw, name: combined };
  });

  const row = playList[index];
  playMusic({
    id: row.id,
    url: row.url,
    pic: row.pic,
    index,
    name: row.name,
    lyric: row.lyric ?? "",
    currentSongList: playList,
  });
}

async function loadRank() {
  try {
    const res = await fetchSongCollectRank();
    if (res.success) {
      rawItems.value = ensureArray<RankRow>(res.data);
    } else {
      rawItems.value = [];
    }
  } catch {
    rawItems.value = [];
  }
}

loadRank();

defineExpose({ reload: loadRank });
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.song-rank {
  width: 90%;
  margin: 0 auto;
  padding-top: 12px;

  &--page {
    width: 92%;
    max-width: 1200px;
    padding-top: 20px;
  }

  &__head {
    display: flex;
    align-items: baseline;
    justify-content: center;
    gap: 12px;
    margin-bottom: 8px;
  }

  &__title {
    margin: 0;
    font-size: 1.35rem;
    font-weight: 600;
    color: $color-black;
  }

  &__hint {
    font-size: 0.85rem;
    color: rgba(0, 0, 0, 0.45);
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: 0;
    padding: 4px 0 16px;
  }

  &__row {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 8px;
    border-radius: 10px;
    cursor: pointer;
    transition: background-color 0.15s ease;
    outline: none;

    &:hover {
      background-color: rgba(0, 0, 0, 0.04);
    }

    &:focus-visible {
      box-shadow: 0 0 0 2px rgba($color-blue-active, 0.35);
    }
  }

  &__row-rank {
    flex: 0 0 26px;
    width: 26px;
    text-align: center;
    font-size: 15px;
    font-weight: 700;
    color: rgba(0, 0, 0, 0.35);

    &.is-top1 {
      color: #e85a5a;
    }
    &.is-top2 {
      color: #c97a3a;
    }
    &.is-top3 {
      color: #b8892e;
    }
  }

  &__row-cover {
    flex: 0 0 56px;
    width: 56px;
    height: 56px;
    border-radius: 8px;
    overflow: hidden;
    background: $theme-background-color;
  }

  &__row-img {
    width: 56px;
    height: 56px;
    display: block;
  }

  &__row-info {
    flex: 1;
    min-width: 0;
    padding-right: 4px;
  }

  &__row-title {
    margin: 0;
    font-size: 0.95rem;
    font-weight: 600;
    color: $color-black;
    line-height: 1.35;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &__row-play {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 38px;
    height: 38px;
    padding: 0;
    border: none;
    border-radius: 10px;
    background: rgba(149, 210, 246, 0.45);
    color: $color-blue-dark;
    cursor: pointer;
    transition: background-color 0.15s ease, transform 0.1s ease;

    &:hover {
      background: rgba(149, 210, 246, 0.75);
    }

    &:active {
      transform: scale(0.96);
    }
  }

  &__row-play-icon {
    width: 20px;
    height: 20px;
  }

  &__row-fav {
    flex: 0 0 auto;
    display: inline-flex;
    align-items: center;
    justify-content: flex-end;
    gap: 6px;
    min-width: 56px;
    color: rgba(0, 0, 0, 0.45);
    font-size: 0.8rem;
  }

  &__row-heart {
    width: 16px;
    height: 16px;
    color: rgba(0, 0, 0, 0.4);
  }

  &__row-fav-count {
    font-variant-numeric: tabular-nums;
    color: rgba(0, 0, 0, 0.52);
  }

  &__scroll {
    display: flex;
    gap: 14px;
    overflow-x: auto;
    padding: 8px 4px 16px;
    scroll-snap-type: x mandatory;
    -webkit-overflow-scrolling: touch;

    &::-webkit-scrollbar {
      height: 6px;
    }
    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.15);
      border-radius: 4px;
    }
  }

  &__card {
    position: relative;
    flex: 0 0 148px;
    scroll-snap-align: start;
    cursor: pointer;
    border-radius: 10px;
    transition: transform 0.2s ease;

    &:hover {
      transform: translateY(-2px);
    }
  }

  &__badge {
    position: absolute;
    top: -6px;
    left: -6px;
    z-index: 2;
    min-width: 22px;
    height: 22px;
    padding: 0 6px;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 700;
    line-height: 22px;
    text-align: center;
    color: #fff;
    background: $color-grey;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);

    &.is-gold {
      background: linear-gradient(135deg, #e6c06a, #c4932d);
    }
    &.is-silver {
      background: linear-gradient(135deg, #cfd3d8, #8e949c);
    }
    &.is-bronze {
      background: linear-gradient(135deg, #d4a27f, #9a6345);
    }
  }

  &__cover {
    position: relative;
    width: 100%;
    padding-bottom: 100%;
    border-radius: 10px;
    overflow: hidden;
    background: $theme-background-color;
  }

  &__img {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
  }

  &__mask {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(20, 20, 20, 0.35);
    opacity: 0;
    transition: opacity 0.2s ease;
  }

  &__card:hover &__mask {
    opacity: 1;
  }

  &__play-icon {
    width: 28px;
    height: 28px;
    color: #fff;
    filter: drop-shadow(0 1px 3px rgba(0, 0, 0, 0.4));
  }

  &__meta {
    margin-top: 8px;
    text-align: center;
  }

  &__name {
    margin: 0;
    font-size: 0.9rem;
    font-weight: 600;
    color: $color-black;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__sub {
    margin: 2px 0 0;
    font-size: 0.75rem;
    color: rgba(0, 0, 0, 0.5);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__count {
    margin: 4px 0 0;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    font-size: 0.72rem;
    color: $color-red;
  }

  &__heart {
    width: 12px;
    height: 12px;
  }

  &__empty {
    padding: 28px 0 40px;
  }
}

@media screen and (max-width: $sm) {
  .song-rank__row {
    gap: 8px;
    padding: 8px 4px;
  }

  .song-rank__row-cover,
  .song-rank__row-img {
    width: 48px;
    height: 48px;
  }

  .song-rank__row-cover {
    flex-basis: 48px;
  }

  .song-rank__row-play {
    width: 34px;
    height: 34px;
  }

  .song-rank__row-play-icon {
    width: 18px;
    height: 18px;
  }

  .song-rank__row-fav {
    min-width: 48px;
    font-size: 0.74rem;
  }
}
</style>
