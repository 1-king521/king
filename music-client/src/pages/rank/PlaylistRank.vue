<template>
  <div class="playlist-rank-page">
    <PlaylistCollectRank />
  </div>
</template>

<script lang="ts" setup>
import PlaylistCollectRank from "@/components/business/PlaylistCollectRank.vue";
</script>

<style lang="scss" scoped>
.playlist-rank-page {
  min-height: calc(100vh - 56px - 72px);
  box-sizing: border-box;
}
</style>
