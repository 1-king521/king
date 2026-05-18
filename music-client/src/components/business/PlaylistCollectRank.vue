<template>
  <section
    v-if="items.length || showEmptyState"
    class="playlist-rank"
    :class="{ 'playlist-rank--page': showEmptyState }"
    aria-label="歌单排行榜"
  >
    <div class="playlist-rank__head">
      <h2 class="playlist-rank__title">{{ title }}</h2>
      <span class="playlist-rank__hint">{{ hint }}</span>
    </div>
    <div v-if="items.length" class="playlist-rank__list">
      <article
        v-for="(item, index) in items"
        :key="item.id"
        class="playlist-rank__row"
        role="button"
        tabindex="0"
        @click="openDetail(item)"
        @keydown.enter.prevent="openDetail(item)"
      >
        <span class="playlist-rank__row-rank" :class="rankNumClass(index + 1)">{{ index + 1 }}</span>
        <div class="playlist-rank__row-cover">
          <el-image class="playlist-rank__row-img" fit="cover" :src="attachImageUrl(item.pic)" />
        </div>
        <div class="playlist-rank__row-info">
          <p class="playlist-rank__row-title">{{ displayTitleLine(item) }}</p>
          <p v-if="subLine(item)" class="playlist-rank__row-sub">{{ subLine(item) }}</p>
        </div>
        <button
          type="button"
          class="playlist-rank__row-go"
          aria-label="进入歌单"
          @click.stop="openDetail(item)"
        >
          <ChevronRight class="playlist-rank__row-go-icon" />
        </button>
        <div class="playlist-rank__row-fav" aria-hidden="true" @click.stop>
          <Heart class="playlist-rank__row-heart" />
          <span class="playlist-rank__row-fav-count">{{ formatCount(item.collectCount) }}</span>
        </div>
      </article>
    </div>
    <el-empty v-else class="playlist-rank__empty" description="暂无排行数据" />
  </section>
</template>

<script lang="ts" setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { ChevronRight, Heart } from "lucide-vue-next";
import { fetchSongListCollectRank } from "@/api/songSheet";
import { ensureArray } from "@/utils/api-response-data";
import { attachImageUrl } from "@/utils";
import { useSongStore } from "@/store/song";

const props = withDefaults(
  defineProps<{
    title?: string;
    hint?: string;
    showEmptyState?: boolean;
  }>(),
  {
    title: "歌单排行榜",
    hint: "按用户收藏歌单次数",
    showEmptyState: true,
  },
);

type PlaylistRankRow = {
  id: number | string;
  title?: string;
  pic?: string;
  style?: string;
  introduction?: string;
  collectCount?: number | string;
};

const router = useRouter();
const songStore = useSongStore();

const rawItems = ref<PlaylistRankRow[]>([]);
const items = computed(() => (Array.isArray(rawItems.value) ? rawItems.value : []));

function rankNumClass(rank: number) {
  if (rank === 1) return "is-top1";
  if (rank === 2) return "is-top2";
  if (rank === 3) return "is-top3";
  return "";
}

/** 主行：歌单标题（与歌曲榜「歌名-作者」对应，歌单无单独作者字段） */
function displayTitleLine(row: PlaylistRankRow) {
  return (row.title || "").trim() || "未命名歌单";
}

function subLine(row: PlaylistRankRow) {
  const st = String(row.style ?? "").trim();
  if (st) return st;
  const intro = String(row.introduction ?? "").trim();
  if (!intro) return "";
  return intro.length > 36 ? `${intro.slice(0, 36)}…` : intro;
}

function formatCount(v: unknown) {
  const n = typeof v === "number" ? v : Number(v);
  return Number.isFinite(n) ? n : 0;
}

function openDetail(row: PlaylistRankRow) {
  const id = row.id;
  if (id == null || id === "") return;
  songStore.setSongDetails({
    id,
    title: displayTitleLine(row),
    pic: row.pic ?? "",
    introduction: typeof row.introduction === "string" ? row.introduction : "",
  });
  router.push({ path: `/song-sheet-detail/${id}` });
}

async function loadRank() {
  try {
    const res = await fetchSongListCollectRank();
    if (res.success) {
      rawItems.value = ensureArray<PlaylistRankRow>(res.data);
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

.playlist-rank {
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

  &__row-sub {
    margin: 4px 0 0;
    font-size: 0.78rem;
    color: rgba(0, 0, 0, 0.48);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__row-go {
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

  &__row-go-icon {
    width: 22px;
    height: 22px;
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

  &__empty {
    padding: 28px 0 40px;
  }
}

@media screen and (max-width: $sm) {
  .playlist-rank__row {
    gap: 8px;
    padding: 8px 4px;
  }

  .playlist-rank__row-cover,
  .playlist-rank__row-img {
    width: 48px;
    height: 48px;
  }

  .playlist-rank__row-cover {
    flex-basis: 48px;
  }

  .playlist-rank__row-go {
    width: 34px;
    height: 34px;
  }

  .playlist-rank__row-go-icon {
    width: 20px;
    height: 20px;
  }

  .playlist-rank__row-fav {
    min-width: 48px;
    font-size: 0.74rem;
  }
}
</style>
