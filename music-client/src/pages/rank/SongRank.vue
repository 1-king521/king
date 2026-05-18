<template>
  <div class="song-rank-page">
    <SongCollectRank
      title="歌曲排行榜"
      hint="按用户收藏次数"
      :show-empty-state="true"
      label-layout="songThenArtist"
    />
  </div>
</template>

<script lang="ts" setup>
import SongCollectRank from "@/components/business/SongCollectRank.vue";
</script>

<style lang="scss" scoped>
.song-rank-page {
  min-height: calc(100vh - 56px - 72px);
  box-sizing: border-box;
}
</style>
